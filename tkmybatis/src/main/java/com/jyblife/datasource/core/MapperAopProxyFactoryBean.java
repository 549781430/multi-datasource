package com.jyblife.datasource.core;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;

public class MapperAopProxyFactoryBean extends ProxyFactoryBean {

    private MapperBeforeAdvice mapperBeforeAdvice;

    //声明一个aspectj切点
    private AspectJExpressionPointcut cut = new AspectJExpressionPointcut();

    public void setExpression(String expression){
        cut.setExpression(expression);
    }

    @Autowired
    public void setAdvice(MapperBeforeAdvice mapperBeforeAdvice) {
        this.mapperBeforeAdvice = mapperBeforeAdvice;
        //切面=切点+通知
        Advisor advisor = new DefaultPointcutAdvisor(cut, this.mapperBeforeAdvice);
        this.addAdvisor(advisor);
    }
}
