package com.jyblife.datasource.monitor;


import com.jyblife.datasource.anotation.TargetDataSource;
import com.jyblife.datasource.constant.DatasourceConstant;
import com.jyblife.datasource.core.TransactionManager;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Properties;

@Intercepts({
        @Signature(type = StatementHandler.class,
                method = "prepare",
                args = {Connection.class, Integer.class})
})
public class PrepareInvocationMonitor implements Interceptor {

    private Properties properties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        while (metaStatementHandler.hasGetter("h")) {
            Object object = metaStatementHandler.getValue("h");
            metaStatementHandler = SystemMetaObject.forObject(object);
        }
        // 分离最后一个代理对象的目标类
        while (metaStatementHandler.hasGetter("target")) {
            Object object = metaStatementHandler.getValue("target");
            metaStatementHandler = SystemMetaObject.forObject(object);
        }
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        if(!SqlCommandType.SELECT.equals(sqlCommandType)){
            Class mapper = Class.forName(mappedStatement.getId().substring(0,mappedStatement.getId().lastIndexOf(".")));
            TargetDataSource annotation = (TargetDataSource) mapper.getAnnotation(TargetDataSource.class);
            String datasource = annotation != null ? annotation.value() : DatasourceConstant.DEFAULT_DATASOURCE;
            TransactionManager.putTrasaction(datasource);
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
