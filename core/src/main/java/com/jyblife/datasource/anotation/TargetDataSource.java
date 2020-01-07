package com.jyblife.datasource.anotation;
import com.jyblife.datasource.constant.DatasourceConstant;

import java.lang.annotation.*;

/**
 * 在方法上使用，用于指定使用哪个数据源
 *
 * @author  longhuahui
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String value() default DatasourceConstant.DEFAULT_DATASOURCE;
}
