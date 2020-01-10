package com.jyblife.datasource.interceptor;

import com.jyblife.datasource.core.TransactionManager;
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
        TransactionManager.commit();
        if (log.isInfoEnabled()) {
            log.info("方法【{}】事务提交成功", method.toString());
        }
    }
}
