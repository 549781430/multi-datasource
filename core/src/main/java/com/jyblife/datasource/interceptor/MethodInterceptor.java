package com.jyblife.datasource.interceptor;

import com.jyblife.datasource.core.TransactionManager;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class MethodInterceptor implements IntroductionInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object ret;
        try {
            if(log.isInfoEnabled()){
                log.info("方法【{}】开启事务", invocation.getMethod().toString());
            }
            TransactionManager.open();
            ret = invocation.proceed();
            if(log.isInfoEnabled()){
                log.info("方法【{}】事务提交成功", invocation.getMethod().toString());
            }
            TransactionManager.commit();
            return ret;
        } catch (Throwable e) {
            TransactionManager.rollback();
            log.error("方法【{}】处理异常，事务回滚", invocation.getMethod().toString());
            throw e;
        }
    }

    @Override
    public boolean implementsInterface(Class<?> intf) {
        return false;
    }
}
