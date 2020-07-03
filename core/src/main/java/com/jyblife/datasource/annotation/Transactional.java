package com.jyblife.datasource.annotation;

import java.lang.annotation.*;

/**
 * 在方法上使用，用于开启事务管理
 *
 * @author  longhuahui
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transactional {
}
