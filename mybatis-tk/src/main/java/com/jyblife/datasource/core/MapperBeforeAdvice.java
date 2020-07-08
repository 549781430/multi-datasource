package com.jyblife.datasource.core;

import com.jyblife.datasource.annotation.TargetDataSource;
import com.jyblife.datasource.constant.MybatisConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

@Component
public class MapperBeforeAdvice implements MethodBeforeAdvice {

    /**
     * 缓存类对应的MappedStatement
     */
    private static Map<String,Collection<MappedStatement>> cache = new HashMap<>();

    public void before(Method method, Object[] args, Object o) throws ClassNotFoundException {
        if(!TransactionManager.isOpen()){
            return;
        }
        Type genericInterfaces = AopUtils.getTargetClass(o).getGenericInterfaces()[0];
        String className = genericInterfaces.getTypeName();
        String methodName = className + "." + method.getName();
        Class clazz = Class.forName(className);
        Annotation annotation = clazz.getAnnotation(Mapper.class);
        if(null == annotation){
            return;
        }
        Collection<MappedStatement> mappedStatements;

        if(!cache.containsKey(methodName)){
            mappedStatements = (Collection<MappedStatement>) SystemMetaObject.forObject(o).getValue("h.sqlSession.configuration.mappedStatements");

            cache.put(className,mappedStatements);
        }else{
            mappedStatements = cache.get(className);
        }
        Iterator<MappedStatement> iterator = mappedStatements.iterator();
        while (iterator.hasNext()){
            Object object = iterator.next();
            if(object instanceof org.apache.ibatis.mapping.MappedStatement) {
                MappedStatement statement=(MappedStatement)object;
                if(methodName.equals(statement.getId())){
                    if(SqlCommandType.SELECT != statement.getSqlCommandType()){
                        TargetDataSource targetDataSource = (TargetDataSource) clazz.getAnnotation(TargetDataSource.class);
                        String datasource = null == targetDataSource ? MybatisConstant.DEFAULT_DATASOURCE : targetDataSource.value();
                        TransactionManager.putTrasaction(datasource);
                    }
                }
            }
        }
    }
}
