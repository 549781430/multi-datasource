package com.jyblife.datasource.anotation;

import com.jyblife.datasource.constant.MybatisConstant;

import java.lang.annotation.*;

/**
 * 在方法上使用，用于指定使用哪个数据源
 *
 * @author  longhuahui
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TargetDataSource {
    String value() default MybatisConstant.DEFAULT_DATASOURCE;
}
