package com.jyblife.datasource.interceptor;

import java.lang.reflect.Method;

/**
 * 拦截器定义。其中包含拦截器所在的类名、方法对象、所在类的对象
 * @author Lconf
 *
 */
public class IntercepterMethodDefination {
    private Class<?> klass;//拦截器所在的类
    private Method method;//拦截器方法
    private Object object;//拦截器方法所在的类的实例，以供在拦截时调用

    public IntercepterMethodDefination(Class<?> klass, Method method, Object object) {
        this.klass = klass;
        this.method = method;
        this.object = object;
    }

    public Class<?> getKlass() {
        return klass;
    }

    public void setKlass(Class<?> klass) {
        this.klass = klass;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
