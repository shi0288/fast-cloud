package com.mcp.fastcloud.proxy;

import com.google.common.collect.Lists;
import com.mcp.fastcloud.annotation.ReturnDecoder;
import com.mcp.fastcloud.annotation.ServerName;
import com.mcp.fastcloud.util.HttpClient4Utils;
import com.mcp.fastcloud.util.Result;
import com.mcp.fastcloud.util.FastJsonDecoder;
import com.mcp.fastcloud.util.SpringIocUtil;
import com.mcp.fastcloud.util.contract.PostElectiveContract;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import feign.*;
import feign.codec.Decoder;
import feign.form.FormEncoder;
import feign.hystrix.FallbackFactory;
import feign.hystrix.HystrixFeign;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.mcp.fastcloud.util.enums.ResultCode.ERROR_CLOUD;

/**
 * Created by shiqm on 2017-06-14.
 */
public class CloudProxy<T> implements InvocationHandler {

    private Logger logger = LoggerFactory.getLogger(CloudProxy.class);

    private Class<T> methodInterface;

    public CloudProxy(Class<T> methodInterface) {
        this.methodInterface = methodInterface;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serverName = null;
        Class applyClass = null;
        if (method.getDeclaringClass().isAnnotationPresent(ServerName.class)) {
            ServerName serverNameAnnotation = method.getDeclaringClass().getAnnotation(ServerName.class);
            serverName = serverNameAnnotation.value();
            applyClass = serverNameAnnotation.applyClass();
        }
        EurekaClient eurekaClient = (EurekaClient) SpringIocUtil.getBean("eurekaClient");
        FallbackFactory<T> fallbackFactory = cause -> {
            Result result = new Result(ERROR_CLOUD);
            return (T) result;
        };
        HystrixFeign.Builder builder =
                HystrixFeign.builder().errorDecoder((String methodKey, Response response) -> feign.FeignException.errorStatus(methodKey, response))
                        .options(new Request.Options(200, 2000))
                        .retryer(Retryer.NEVER_RETRY);

        ReturnDecoder returnDecoder = method.getAnnotation(ReturnDecoder.class);
        if (returnDecoder != null) {
            builder.encoder(new FormEncoder()).decoder((Decoder) SpringIocUtil.getBean(returnDecoder.value()));
        } else if (method.getReturnType().isAssignableFrom(Result.class)) {
            builder.encoder(new FormEncoder()).decoder(SpringIocUtil.getBean(FastJsonDecoder.class));
        } else if (!method.getReturnType().isAssignableFrom(String.class)) {
            builder.encoder(new FormEncoder()).decoder(SpringIocUtil.getBean(FastJsonDecoder.class));
        } else {
            builder.encoder(new FormEncoder());
        }
        if (RequestInterceptor.class.isAssignableFrom(applyClass)) {
            try {
                RequestInterceptor forwardedForInterceptor = (RequestInterceptor) SpringIocUtil.getBean(applyClass);
                builder.requestInterceptor(forwardedForInterceptor);
            } catch (Exception e) {
                logger.warn("获取请求拦截bean错误==>RequestInterceptor：" + applyClass);
            }
        }

        List<InstanceInfo> instanceInfoList = Lists.newCopyOnWriteArrayList(eurekaClient.getInstancesByVipAddress(serverName, false));
        Collections.sort(instanceInfoList, (InstanceInfo ins1, InstanceInfo ins2) -> (new Random()).nextInt() % 2 == 0 ? 1 : -1);
        InstanceInfo instanceCur = null;
        for (InstanceInfo instance : instanceInfoList) {
            CloseableHttpResponse response = null;
            try {
                HttpGet httpGet = new HttpGet(instance.getStatusPageUrl());
                response = HttpClient4Utils.httpClient.execute(httpGet);
                int code = response.getStatusLine().getStatusCode();
                if (code != HttpStatus.SC_OK) {
                    continue;
                }
                instanceCur = instance;
                break;
            } catch (IOException ex) {
                ex.printStackTrace();
                logger.error(serverName + "服务不可用。。。。。。。。");
            } finally {
                if (response != null) {
                    try {
                        response.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        builder.contract(new PostElectiveContract());
        Object service = builder.target((Class<T>) method.getDeclaringClass(), instanceCur.getHomePageUrl(), fallbackFactory);
        return method.invoke(service, args);

    }


}
