package com.jyblife.datasource.interceptor;

import com.jyblife.datasource.core.TransactionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
public class TransactionAfterAdvice implements AfterReturningAdvice {

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) {
            TransactionManager.commit();
            if(log.isInfoEnabled()){
                log.info("方法【{}】事务提交成功", method.toString());
            }
    }
}
