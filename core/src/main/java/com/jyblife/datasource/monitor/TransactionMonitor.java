package com.jyblife.datasource.monitor;

import com.jyblife.datasource.core.TransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//@Aspect
//@Component
public class TransactionMonitor {

    private static final Logger log = LoggerFactory.getLogger(TransactionMonitor.class);

    @Pointcut(value = "@annotation(com.jyblife.datasource.anotation.Transactional)")
    public void pointCut(){}

    @Around(value = "pointCut()")
    public Object aop(ProceedingJoinPoint point) throws Throwable {
        String method = null;
        try {
            Signature signature = point.getSignature();
            method = signature.toString();
            if(log.isInfoEnabled()){
                log.info("方法【{}】开启事务", method);
            }
            TransactionManager.open();
            Object ret = point.proceed();
            if(log.isInfoEnabled()){
                log.info("方法【{}】事务提交成功", method);
            }
            TransactionManager.commit();
            return ret;
        } catch (Throwable e) {
            TransactionManager.rollback();
            log.error("方法【{}】处理异常，事务回滚", method);
            throw e;
        }
    }
}
