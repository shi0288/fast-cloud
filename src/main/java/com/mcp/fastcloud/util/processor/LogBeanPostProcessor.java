package com.mcp.fastcloud.util.processor;

import com.mcp.fastcloud.annotation.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;

import java.lang.reflect.Field;

/**
 * Created by shiqm on 2017/3/20.
 */
@Configuration
public class LogBeanPostProcessor implements BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        Class clazz = bean.getClass();
        initializeLog(bean, clazz);
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    private void initializeLog(final Object bean, final Class<? extends Object> clazz) {
        ReflectionUtils.doWithFields(clazz, new FieldCallback() {
            @Override
            public void doWith(final Field field)
                    throws IllegalArgumentException, IllegalAccessException {
                boolean visabled = field.isAccessible();
                try {
                    field.setAccessible(true);
                    if (field.get(bean) == null) {
                        field.set(bean, LoggerFactory.getLogger(clazz));
                    }
                } catch (Exception e) {
                    throw new BeanInitializationException(String.format(
                            "初始化logger失败!bean=%s;field=%s", bean, field));
                } finally {
                    field.setAccessible(visabled);
                }
            }
        }, new FieldFilter() {
            @Override
            public boolean matches(final Field field) {
                if (field.getAnnotation(Log.class) == null) {
                    return false;
                }
                if (!field.getType().isAssignableFrom(Logger.class)) {
                    return false;
                }
                return true;
            }
        });
    }
}
