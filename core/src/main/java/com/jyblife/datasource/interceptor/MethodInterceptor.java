package com.jyblife.datasource.interceptor;

import com.jyblife.datasource.core.TransactionManager;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.stereotype.Component;

@Component
public class MethodInterceptor implements IntroductionInterceptor {

    private static final Logger log = LoggerFactory.getLogger(MethodInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String methodName = invocation.getMethod().toString();
        try {
            TransactionManager.open();
            if(log.isInfoEnabled()){
                log.info("方法【{}】开启事务", methodName);
            }
            Object ret = invocation.proceed();
            TransactionManager.commit();
            if(log.isInfoEnabled()){
                log.info("方法【{}】事务提交成功", methodName);
            }
            return ret;
        } catch (Throwable e) {
            TransactionManager.rollback();
            log.error("方法【{}】处理异常，事务回滚", methodName);
            throw e;
        }
    }

    @Override
    public boolean implementsInterface(Class<?> intf) {
        return false;
    }
}
