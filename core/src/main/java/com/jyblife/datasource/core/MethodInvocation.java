package com.jyblife.datasource.core;

import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

public class MethodInvocation implements Proceed{

    private Object target;

    private Method method;

    private Object[] arguments;

    private MethodProxy methodProxy;

    private List<MapperAdvisor> advisors;

    public MethodInvocation(Object target, Method method, Object[] arguments, MethodProxy proxy, List<MapperAdvisor> advisors) {
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.methodProxy = proxy;
        this.advisors = advisors;
    }

    private int currentInterceptorIndex = -1;

    @Override
    public Object proceed() throws Throwable {
        if (currentInterceptorIndex == advisors.size() - 1){
            return methodProxy.invokeSuper(target, arguments);
        }

        ++currentInterceptorIndex;

        MapperAdvisor advisor = advisors.get(currentInterceptorIndex);

        return advisor.invoke(this);
    }

    public Object[] getArguments() {
        return arguments;
    }
}
