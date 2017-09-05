package com.mcp.fastcloud.proxy;

import com.mcp.fastcloud.annotation.ServerName;
import com.mcp.fastcloud.util.FastJsonDecoder;
import com.mcp.fastcloud.util.SpringIocUtil;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import feign.Feign;
import feign.RequestInterceptor;
import feign.form.FormEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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
        Feign.Builder builder = Feign.builder().encoder(new FormEncoder()).decoder(new FastJsonDecoder());
        if (RequestInterceptor.class.isAssignableFrom(applyClass)) {
            try {
                RequestInterceptor forwardedForInterceptor = (RequestInterceptor) SpringIocUtil.getBean(applyClass);
                builder.requestInterceptor(forwardedForInterceptor);
            } catch (Exception e) {
                logger.warn("获取请求拦截bean错误：" + applyClass);
            }
        }
        InstanceInfo instance = eurekaClient.getNextServerFromEureka(serverName, false);
        Object service = builder.target(method.getDeclaringClass(), instance.getHomePageUrl());
        return method.invoke(service, args);

    }


}
