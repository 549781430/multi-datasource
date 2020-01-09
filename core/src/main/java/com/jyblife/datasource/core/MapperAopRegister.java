package com.jyblife.datasource.core;

import com.jyblife.datasource.anotation.MapperScan;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MapperAopRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private Environment environment;

    private ResourceLoader resourceLoader;

    private static String basePackage;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

//        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(MapperScan.class.getName()));
//
//        basePackage = annoAttrs.getString("basePackage");
//        if(!StringUtils.hasText(basePackage)){
//            basePackage = this.environment.getProperty("mybatis.basePackage");
//        }
//        if(!StringUtils.hasText(basePackage)){
//            throw new RuntimeException("EnableDatasource必须配置basePackage属性");
//        }
//        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperAop.class);
//        BeanDefinition beanDefinition = builder.getBeanDefinition();
//        beanDefinition.getPropertyValues().add("expression", "execution( * " + basePackage + "*.*(..))");
//        beanDefinition.getPropertyValues().add("advice", new MapperBeforeAdvice());
//        registry.registerBeanDefinition(MapperAop.class.getName(), beanDefinition);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
