package com.jyblife.datasource.monitor;

import com.jyblife.datasource.anotation.MapperTransactionAop;
import com.jyblife.datasource.core.BeforeAdvisor;
import com.jyblife.datasource.core.MapperAdvisor;
import com.jyblife.datasource.core.MapperCallback;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InvocationMapperMonitor  implements BeanPostProcessor, ApplicationContextAware {

    public static String mapperPackage;

    private ApplicationContext applicationContext;

    private List<MapperAdvisor> cacheAdvisors;//缓存提升性能

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        buildAdvisors();

        if(bean.toString().startsWith("org.apache.ibatis.binding.MapperProxy")){
            Map<Method, List<MapperAdvisor>> methodListMap = getMatchAdvisor(bean);
            if (methodListMap.isEmpty())
                return bean;
            Enhancer enhancer = new Enhancer();//创建代理
            enhancer.setSuperclass(bean.getClass());
            enhancer.setCallback(new MapperCallback(methodListMap));
            return enhancer.create();
        } else{
            return bean;
        }
    }

    private void buildAdvisors() {
        if (cacheAdvisors != null) {
            return;
        }

        if (cacheAdvisors == null) {
            cacheAdvisors = new ArrayList<>();
        }

        Map<String, Object> map = applicationContext.getBeansWithAnnotation(MapperTransactionAop.class);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object aopObj = entry.getValue();
            Method[] methods = aopObj.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals("before")) {
                    MapperAdvisor advisor = new BeforeAdvisor(aopObj, method);
                    if (!cacheAdvisors.contains(advisor)){
                        cacheAdvisors.add(advisor);
                    }
                } else if (method.getName().equals("after")) {

                }
            }
        }
    }


    private Map<Method, List<MapperAdvisor>> getMatchAdvisor(Object bean) {
        Class<?> clazz = bean.getClass();
        Method[] methods = clazz.getMethods();

        Map<Method, List<MapperAdvisor>> methodListMap = new HashMap<>();
        for(Method method: methods){
            for (MapperAdvisor advisor : cacheAdvisors) {
                //if (this.mapperAdvisor.isMatch(clazz, method)) {
                    List<MapperAdvisor> advisors = methodListMap.get(method);
                    if (advisors == null) {
                        advisors = new ArrayList<>();
                        methodListMap.put(method, advisors);
                    }
                    advisors.add(advisor);
                //}
            }
        }
        return methodListMap;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        if(!StringUtils.hasText(mapperPackage)){
            this.mapperPackage = this.applicationContext.getEnvironment().getProperty("mybatis.basePackage");
        }

    }
}
