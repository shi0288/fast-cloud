package com.mcp.fastcloud.proxy;

import java.lang.reflect.Proxy;

/**
 * Created by shiqm on 2017-06-14.
 */
public class CloudProxyMethodFactory {

    public static <T> T newInstance(Class<T> methodInterface) {
        final CloudProxy<T> methodProxy = new CloudProxy<T>(methodInterface);
        return (T) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{methodInterface},
                methodProxy);
    }

}
