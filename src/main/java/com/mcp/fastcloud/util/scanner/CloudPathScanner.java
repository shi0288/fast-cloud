package com.mcp.fastcloud.util.scanner;

import com.mcp.fastcloud.proxy.CloudServiceFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Created by shiqm on 2017-06-14.
 */
public class CloudPathScanner extends ClassPathBeanDefinitionScanner {


    private Class<? extends Annotation> annotationClass;


    public CloudPathScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }


    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }


    public void registerFilters() {
        if(this.annotationClass != null) {
            this.addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }


    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        for (BeanDefinitionHolder holder : beanDefinitions) {
            GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
            Class<?> cloudInterface = null;
            try {
                cloudInterface = Class.forName(String.valueOf(definition.getBeanClassName()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
            String beanName = beanNameGenerator.generateBeanName(definition, this.getRegistry());
            definition.getPropertyValues().add("cloudInterface", cloudInterface);
            definition.setBeanClass(CloudServiceFactory.class);
            this.getRegistry().registerBeanDefinition(beanName, definition);
        }
        return beanDefinitions;
    }


}
