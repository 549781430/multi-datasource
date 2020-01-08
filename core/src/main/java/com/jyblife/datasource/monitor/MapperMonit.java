package com.jyblife.datasource.monitor;

import com.jyblife.datasource.anotation.MapperTransactionAop;

@MapperTransactionAop
public class MapperMonit {

    public void before(Object[] args) {
        System.out.println("xxx");
    }

    public void after(Object[] args) {
        System.out.println("xxx");
    }
}
