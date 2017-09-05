package com.mcp.fastcloud.proxy;

import org.springframework.beans.factory.FactoryBean;

/**
 * Created by shiqm on 2017-06-14.
 */
public class CloudServiceFactory<T>  implements FactoryBean<T> {


    private Class<T> cloudInterface;

    public CloudServiceFactory() {}

    @Override
    public T getObject() throws Exception {
        return CloudProxyMethodFactory.newInstance(cloudInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return  this.cloudInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }


    public Class<T> getCloudInterface() {
        return cloudInterface;
    }

    public void setCloudInterface(Class<T> cloudInterface) {
        this.cloudInterface = cloudInterface;
    }
}
