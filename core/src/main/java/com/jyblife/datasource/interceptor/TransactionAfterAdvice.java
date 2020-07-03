package com.jyblife.datasource.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class TransactionAfterAdvice implements AfterReturningAdvice {

    private static final Logger log = LoggerFactory.getLogger(TransactionAfterAdvice.class);

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) {
        if (log.isDebugEnabled()) {
            log.debug("--------------------------------------------------");
        }
    }
}
