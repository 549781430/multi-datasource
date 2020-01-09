package com.jyblife.datasource.config;

import com.jyblife.datasource.anotation.TargetDataSource;
import com.jyblife.datasource.core.TransactionManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;


@Aspect
@Component
public class DynamicDataSourceAspect {

    @Pointcut("execution( * com.jyblife.datasource.dao.mapper.*.*(..))")
    private void aspect() {
    }

    @Before("aspect()")
    public void invoke(JoinPoint pjp) {
        try {
            //获得当前访问的class
            Class<?> className = pjp.getTarget().getClass();
            String dataSource = null;
            try {
                Type type = className.getGenericInterfaces()[0];
                String clazz = type.getTypeName();
                Class c = Class.forName(clazz);
                // 判断是否存在@TargetDataSource注解
                if (c.isAnnotationPresent(TargetDataSource.class)) {
                    TargetDataSource annotation = (TargetDataSource) c.getAnnotation(TargetDataSource.class);
                    // 取出注解中的数据源名
                    dataSource = annotation.value();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            TransactionManager.putTrasaction(dataSource);
        } finally {
            System.out.println("拦截结束");
        }
    }
}
