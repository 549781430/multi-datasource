package com.jyblife.datasource.core;

import com.jyblife.datasource.constant.XmlMapperData;
import org.aspectj.lang.JoinPoint;

public class ExecutionFilter {

    public void filter(JoinPoint pjp){
        String name = pjp.getSignature().getDeclaringType().getName();
        XmlMapperData.documentMap.get("name");

        TransactionManager.putTrasaction("");
    }
}
