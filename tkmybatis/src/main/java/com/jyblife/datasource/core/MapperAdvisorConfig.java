package com.jyblife.datasource.core;

import com.jyblife.datasource.constant.MybatisConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan("com.jyblife.datasource")
public class MapperAdvisorConfig implements EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(MapperAdvisorConfig.class);


    private Environment evn;

    @Bean
    public AspectJExpressionPointcutAdvisor aspectJExpressionPointcutAdvisor(MapperBeforeAdvice advice){
        if(logger.isInfoEnabled()){
            logger.info("Init advisor for mapper.");
        }
        AspectJExpressionPointcutAdvisor aspectJExpressionPointcutAdvisor = new AspectJExpressionPointcutAdvisor();
        aspectJExpressionPointcutAdvisor.setExpression("execution( * " + evn.getProperty(MybatisConstant.EXECUTION_MYBATIS_BASEPACKAGE_KEY) + " .*.*(..))");
        aspectJExpressionPointcutAdvisor.setAdvice(advice);
        return aspectJExpressionPointcutAdvisor;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.evn = environment;
    }
}
