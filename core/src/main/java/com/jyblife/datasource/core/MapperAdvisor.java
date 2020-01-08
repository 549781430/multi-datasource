package com.jyblife.datasource.core;


import java.lang.reflect.Method;

public interface MapperAdvisor extends MethodInterceptor,Orderable{

    boolean isMatch(Class<?> clazz, Method method);
}
