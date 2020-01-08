package com.jyblife.datasource.core;

public interface MethodInterceptor {

    Object invoke(MethodInvocation methodInvocation) throws Throwable;
}
