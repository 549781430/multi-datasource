package com.jyblife.datasource.interceptor;

import java.lang.reflect.Method;

/**
 * 拦截目标类
 * 其中包含被拦截目标方法对象及其所在的类对象。
 * @author Lconf
 *
 */
public class IntercepterTargetDefination {
    private Class<?> targetClass;
    private Method targetMethod;

    public IntercepterTargetDefination(Class<?> targetClass, Method targetMethod) {
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(Method targetMethod) {
        this.targetMethod = targetMethod;
    }
}

