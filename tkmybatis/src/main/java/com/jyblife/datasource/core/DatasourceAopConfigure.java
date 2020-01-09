package com.jyblife.datasource.core;

import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatasourceAopConfigure {

//    @Bean
//    public ProxyFactory getProxyFactory() {
//        ProxyFactory proxyFactory = new ProxyFactory();
//        proxyFactory.setProxyTargetClass(true);
//        return proxyFactory;
//    }
//

    @Bean
    public AspectJExpressionPointcutAdvisor aspectJExpressionPointcutAdvisor(MapperBeforeAdvice advice){
        AspectJExpressionPointcutAdvisor aspectJExpressionPointcutAdvisor = new AspectJExpressionPointcutAdvisor();
        aspectJExpressionPointcutAdvisor.setExpression("execution( * com.jyblife.datasource.dao.*.*(..))");
        aspectJExpressionPointcutAdvisor.setAdvice(advice);
        return aspectJExpressionPointcutAdvisor;
    }
}
