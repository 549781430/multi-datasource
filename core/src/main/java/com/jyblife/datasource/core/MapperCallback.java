package com.jyblife.datasource.core;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MapperCallback implements MethodInterceptor {

    private Map<Method, List<MapperAdvisor>> methodListMap;

    public MapperCallback(Map<Method, List<MapperAdvisor>> methodListMap) {
        this.methodListMap = methodListMap;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        List<MapperAdvisor> advisors = methodListMap.get(method);

        if (advisors == null) {
            return proxy.invokeSuper(obj, args);
        }

        Collections.sort(advisors, new Comparator<MapperAdvisor>() {
            @Override
            public int compare(MapperAdvisor o1, MapperAdvisor o2) {
                return o2.getOrder() - o1.getOrder();
            }
        });

        MethodInvocation methodInvocation = new MethodInvocation(obj, method, args, proxy, advisors);

        return methodInvocation.proceed();
    }
}
