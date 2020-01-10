package com.jyblife.datasource.core;

import com.jyblife.datasource.constant.MybatisConstant;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MapperAdvisorConfig implements EnvironmentAware {

    private Environment evn;

    @Bean
    public AspectJExpressionPointcutAdvisor aspectJExpressionPointcutAdvisor(MapperBeforeAdvice advice){
        AspectJExpressionPointcutAdvisor aspectJExpressionPointcutAdvisor = new AspectJExpressionPointcutAdvisor();
        aspectJExpressionPointcutAdvisor.setExpression("execution( * " + evn.getProperty(MybatisConstant.EXECUTION_MYBATIS_BASEPACKAGE) + " .*.*(..))");
        aspectJExpressionPointcutAdvisor.setAdvice(advice);
        return aspectJExpressionPointcutAdvisor;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.evn = environment;
    }
}
