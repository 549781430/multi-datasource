package com.jyblife.datasource.interceptor;

import java.lang.reflect.Method;

public interface IIntercepterChain {
    void add(IntercepterMethodDefination imd);
    boolean doBefore(Class<?> klass, Method method, Object[] args);
    Object doAfter(Class<?> klass, Method method, Object args);
    void doException(Class<?> klass, Method method, Throwable E);
}
