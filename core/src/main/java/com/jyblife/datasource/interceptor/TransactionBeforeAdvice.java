package com.jyblife.datasource.interceptor;

import com.jyblife.datasource.core.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class TransactionBeforeAdvice implements MethodBeforeAdvice {

    private static final Logger log = LoggerFactory.getLogger(TransactionBeforeAdvice.class);


    public void before(Method method, Object[] args, Object o) {
        if (log.isInfoEnabled()) {
            log.info("方法【{}】开启事务", method.toString());
        }
        TransactionManager.open();
    }
}
