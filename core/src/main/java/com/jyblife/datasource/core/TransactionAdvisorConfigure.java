package com.jyblife.datasource.core;

import com.jyblife.datasource.interceptor.MethodInterceptor;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.jyblife.datasource")
public class TransactionAdvisorConfigure {

    @Bean
    public AspectJExpressionPointcutAdvisor transactionAdvisor(MethodInterceptor advice){
        AspectJExpressionPointcutAdvisor aspectJExpressionPointcutAdvisor = new AspectJExpressionPointcutAdvisor();
        aspectJExpressionPointcutAdvisor.setExpression("@within(com.jyblife.datasource.anotation.Transactional)||@annotation(com.jyblife.datasource.anotation.Transactional)");
        aspectJExpressionPointcutAdvisor.setAdvice(advice);
        return aspectJExpressionPointcutAdvisor;
    }
}
