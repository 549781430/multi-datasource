package com.jyblife.datasource.annotation;

import com.jyblife.datasource.constant.MybatisConstant;
import com.jyblife.datasource.core.MultiDataSourceRegister;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({MultiDataSourceRegister.class})
@Documented
@Inherited
public @interface MapperScan {

    String basePackage() default "";

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

    String multiDatasourceConfigKey() default MybatisConstant.MULTI_DATASOURCE_CONFIG_KEY;

    String mapperLocations() default "";

    String configLocation() default "";

    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    Class<? extends Annotation> annotationClass() default Annotation.class;

    Class<?> markerInterface() default Class.class;

    String[] properties() default {};

    String mapperHelperRef() default "";
}
