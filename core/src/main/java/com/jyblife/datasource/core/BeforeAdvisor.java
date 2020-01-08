package com.jyblife.datasource.core;


import java.lang.reflect.Method;

/**
 * 执行前增强
 */
public class BeforeAdvisor extends AbstractAdvisor {

    public BeforeAdvisor(Object aopObj, Method aopMethod) {
        super(aopObj, aopMethod);
        setOrder(1);
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object[] args = {methodInvocation.getArguments()};//将Object[]转为数组，不然报错
        aopMethod.invoke(aopObj, args);
        return methodInvocation.proceed();
    }
}
