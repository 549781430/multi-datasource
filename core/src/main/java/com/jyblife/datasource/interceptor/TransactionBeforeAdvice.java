package com.jyblife.datasource.interceptor;

import com.jyblife.datasource.core.TransactionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
public class TransactionBeforeAdvice implements MethodBeforeAdvice {

    public void before(Method method, Object[] args, Object o) {
        if(log.isInfoEnabled()){
            log.info("方法【{}】开启事务", method.toString());
        }
        TransactionManager.open();
    }
}
