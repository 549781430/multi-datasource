package com.jyblife.datasource.config;

import com.jyblife.datasource.core.ExecutionFilter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DynamicDataSourceAspect extends ExecutionFilter {

    @Pointcut("execution( * com.jyblife.datasource.dao.mapper.*.*(..))")
    private void aspect() {
    }

    @Before("aspect()")
    public void DynamicDataSourceAspect(JoinPoint pjp) {
        filter(pjp);
    }
}
