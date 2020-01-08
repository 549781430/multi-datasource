package com.jyblife.datasource.anotation;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Component
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MapperTransactionAop {
}
