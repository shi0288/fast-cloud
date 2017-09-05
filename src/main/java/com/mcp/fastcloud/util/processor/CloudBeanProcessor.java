package com.mcp.fastcloud.util.processor;

import com.mcp.fastcloud.annotation.ServerName;
import com.mcp.fastcloud.util.SpringIocUtil;
import com.mcp.fastcloud.util.scanner.CloudPathScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;


/**
 * Created by shiqm on 2017-06-14.
 */
@Configuration
public class CloudBeanProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private String[] packages;

    @Bean
    public SpringIocUtil initSpringIocUtil() {
        return new SpringIocUtil();
    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        registerBean(beanDefinitionRegistry);
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }

    @Override
    public void setEnvironment(Environment environment) {
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, "fastcloud.");
        String propertiesCloudPath = propertyResolver.getProperty("packages");
        if (!StringUtils.isEmpty(propertiesCloudPath)) {
            packages = propertiesCloudPath.split(",");
        }
    }

    private void registerBean(BeanDefinitionRegistry registry) {
        if (packages != null && packages.length > 0) {
            CloudPathScanner classPathMapperScanner = new CloudPathScanner(registry);
            classPathMapperScanner.setAnnotationClass(ServerName.class);
            classPathMapperScanner.registerFilters();
            classPathMapperScanner.doScan(packages);
        }
    }


}
