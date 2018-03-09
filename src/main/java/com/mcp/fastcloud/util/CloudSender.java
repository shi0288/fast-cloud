package com.mcp.fastcloud.util;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import feign.Client;
import feign.Request;
import feign.Response;
import feign.Util;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shiqm on 2017-11-21.
 */
public class CloudSender {

    public static void sendAll(String serverName, String path, String method, Map headers, Charset charset) {
        if (!StringUtils.isEmpty(path)) {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
        }
        EurekaClient eurekaClient = (EurekaClient) SpringIocUtil.getBean("eurekaClient");
        List<InstanceInfo> atrInstance = eurekaClient.getInstancesByVipAddress(serverName, false);
        String mapping = checkPath(path);
        atrInstance.forEach(
                instanceInfo -> {
                    String url = instanceInfo.getHomePageUrl() + mapping;
                    Request request = Request.create(
                            method,
                            url,
                            Collections.unmodifiableMap(headers),
                            null,
                            charset
                    );
                    Client client = new Client.Default(null, null);
                    try {
                        client.execute(request, new Request.Options());
                    } catch (Exception e) {
                        // TODO: 17-11-21
                    }
                }
        );
    }

    public static void sendAll(String serverName, String path, String method, Map headers, Map params, Charset charset) {
        sendAll(serverName, matchingPath(path, params), method, headers, charset);
    }


    public static String send(String serverName, String path, String method, Map headers, Charset charset) {
        EurekaClient eurekaClient = (EurekaClient) SpringIocUtil.getBean("eurekaClient");
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(serverName, false);
        String url = instanceInfo.getHomePageUrl() + checkPath(path);
        Request request = Request.create(
                method,
                url,
                Collections.unmodifiableMap(headers),
                null,
                charset
        );
        Client client = new Client.Default(null, null);
        try {
            Response response = client.execute(request, new Request.Options());
            return Util.toString(response.body().asReader());
        } catch (Exception e) {
            // TODO: 17-11-21
            return null;
        }
    }

    public static String send(String serverName, String path, String method, Map headers, Map params, Charset charset) {
        return send(serverName, matchingPath(path, params), method, headers, charset);
    }


    public static String checkPath(String path) {
        if (!StringUtils.isEmpty(path)) {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
        }
        return path;
    }

    public static String matchingPath(String path, Map params) {
        Pattern pattern = Pattern.compile("(?<=\\{)(.*?)(?=})");
        Matcher matcher = pattern.matcher(path);
        while (matcher.find()) {
            String temp = matcher.group();
            path = path.replace("{" + temp + "}", params.get(temp).toString());
        }
        return path;
    }

}
