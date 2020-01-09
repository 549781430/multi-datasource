package com.jyblife.datasource.core;

import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;


public class MapperAop extends AspectJExpressionPointcutAdvisor {


    public void setExpression(@Nullable String expression) {
        super.setExpression(expression);
    }

    @Autowired
    public void setAdvice(MapperBeforeAdvice mapperBeforeAdvice) {
        super.setAdvice(mapperBeforeAdvice);
    }

}
