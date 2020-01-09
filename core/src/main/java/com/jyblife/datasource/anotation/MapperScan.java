package com.jyblife.datasource.anotation;

import org.springframework.beans.factory.support.BeanNameGenerator;

import java.lang.annotation.*;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited // 声明注解具有继承性
public @interface MapperScan {

    String basePackage() default "";

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

    String mapperLocations() default "";

    String configLocation() default "";

    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    Class<? extends Annotation> annotationClass() default Annotation.class;

    Class<?> markerInterface() default Class.class;

    String[] properties() default {};

    String mapperHelperRef() default "";
}
