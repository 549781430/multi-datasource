package com.jyblife.datasource.core;

import com.jyblife.datasource.interceptor.MethodInterceptor;
import com.jyblife.datasource.interceptor.TransactionAfterAdvice;
import com.jyblife.datasource.interceptor.TransactionBeforeAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionAdvisorConfigure {

    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class.getName());

    /**
     * 事务拦截处理
     * @param advice
     * @return
     */
    @Bean
    public AspectJExpressionPointcutAdvisor transactionAdvisor(MethodInterceptor advice){
        if(logger.isInfoEnabled()){
            logger.info("Init advisor for transactional annotation.");
        }
        AspectJExpressionPointcutAdvisor aspectJExpressionPointcutAdvisor = new AspectJExpressionPointcutAdvisor();
        aspectJExpressionPointcutAdvisor.setExpression("@within(com.jyblife.datasource.anotation.Transactional)||@annotation(com.jyblife.datasource.anotation.Transactional)");
        aspectJExpressionPointcutAdvisor.setAdvice(advice);
        return aspectJExpressionPointcutAdvisor;
    }

    /**
     * 事务提交前处理
     * @param advice
     * @return
     */
    @Bean
    public AspectJExpressionPointcutAdvisor transactionBeforeAdvisor(TransactionBeforeAdvice advice){
        if(logger.isInfoEnabled()){
            logger.info("Init before advisor for transactional annotation.");
        }
        AspectJExpressionPointcutAdvisor aspectJExpressionPointcutAdvisor = new AspectJExpressionPointcutAdvisor();
        aspectJExpressionPointcutAdvisor.setExpression("@within(com.jyblife.datasource.anotation.Transactional)||@annotation(com.jyblife.datasource.anotation.Transactional)");
        aspectJExpressionPointcutAdvisor.setAdvice(advice);
        return aspectJExpressionPointcutAdvisor;
    }

    /**
     * 事务提交后处理
     * @param advice
     * @return
     */
    @Bean
    public AspectJExpressionPointcutAdvisor transactionAfterAdvisor(TransactionAfterAdvice advice){
        if(logger.isInfoEnabled()){
            logger.info("Init after advisor for transactional annotation.");
        }
        AspectJExpressionPointcutAdvisor aspectJExpressionPointcutAdvisor = new AspectJExpressionPointcutAdvisor();
        aspectJExpressionPointcutAdvisor.setExpression("@within(com.jyblife.datasource.anotation.Transactional)||@annotation(com.jyblife.datasource.anotation.Transactional)");
        aspectJExpressionPointcutAdvisor.setAdvice(advice);
        return aspectJExpressionPointcutAdvisor;
    }
}
