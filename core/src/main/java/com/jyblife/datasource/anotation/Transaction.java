package com.jyblife.datasource.anotation;

import java.lang.annotation.*;

/**
 * 在方法上使用，用于开启事务管理
 *
 * @author  longhuahui
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transaction {
}
