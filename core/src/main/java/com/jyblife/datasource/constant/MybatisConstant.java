package com.jyblife.datasource.constant;

public class MybatisConstant {

    // 如配置文件中未指定数据源类型，使用该默认值
    public static final String DATASOURCE_TYPE_DEFAULT = "org.apache.tomcat.jdbc.pool.DataSource";

    public static final String DEFAULT_DATASOURCE = "defaultTargetDataSource";

    public static final String DATASOURCE_TRANSACTION_MANAGER_SUBFIX = "DataSourceTransactionManager";

    public static final String EXECUTION_MYBATIS_BASEPACKAGE_KEY = "execution.mybatis.basePackage";

    public static final String ASPECT_MYBATIS_PROPERTIES_NAME = "aspect.mybatis.properties";

    public static final String DEFAULT_CONFIGLOCATION = "classpath:mybatis/**/mybatis-config.xml";

    public static final String MULTI_DATASOURCE_CONFIG_KEY = "spring.datasource.multi";

    public static final String MYBATIS_CONFIG_LOCATION_KEY = "mybatis.configLocation";

}
