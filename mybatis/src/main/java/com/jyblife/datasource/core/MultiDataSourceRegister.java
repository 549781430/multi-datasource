package com.jyblife.datasource.core;

import com.jyblife.datasource.annotation.MapperScan;
import com.jyblife.datasource.anotation.TargetDataSource;
import com.jyblife.datasource.constant.MybatisConstant;
import com.jyblife.datasource.util.ClassScanner;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 注册多数据源并完成mapper的扫描和绑定
 */
@Component
public class MultiDataSourceRegister extends MapperRegister  {

    private static final Logger logger = LoggerFactory.getLogger(MultiDataSourceRegister.class.getName());

    /**
     * 存所有MapperFactoryBean
     */
    public static Map<String, MapperFactoryBean<?>> mapperFactoryBeanMap = new HashMap<>();

    private String configLocation;

    private String basePackage;

    private String mapperLocations;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        this.logger.info("Searching for mappers");
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
        scanner.setEnvironment(this.env);
        try {
            AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(MapperScan.class.getName()));

            Class<? extends Annotation> annotationClass = annoAttrs.getClass("annotationClass");
            if (!Annotation.class.equals(annotationClass)) {
                scanner.setAnnotationClass(annotationClass);
            } else {
                scanner.setAnnotationClass(Mapper.class);
            }

            if (this.resourceLoader != null) {
                scanner.setResourceLoader(this.resourceLoader);
            }

            this.multiDatasourceConfigKey = annoAttrs.getString("multiDatasourceConfigKey");
            if (!StringUtils.hasText(this.multiDatasourceConfigKey)) {
                this.multiDatasourceConfigKey = MybatisConstant.MULTI_DATASOURCE_CONFIG_KEY;
            }

            this.basePackage = annoAttrs.getString("basePackage");
            if (!StringUtils.hasText(this.basePackage)) {
                this.basePackage = this.env.getProperty("mybatis.basePackage");
            }
            if (!StringUtils.hasText(basePackage)) {
                throw new RuntimeException("EnableDatasource必须配置basePackage属性");
            } else {
                addBasePackageIntoEnvironment(this.basePackage);
            }

            this.mapperLocations = annoAttrs.getString("mapperLocations");
            if (!StringUtils.hasText(this.mapperLocations)) {
                throw new RuntimeException("EnableDatasource必须配置mapperLocations属性");
            }

            this.configLocation = annoAttrs.getString("configLocation");
            if (!StringUtils.hasText(this.configLocation)) {
                logger.warn("EnableDatasource未配置configLocation属性");
            }

            loadConfigMap();
            getSqlSessionTemplateAndDataSource();
            getMapperFactoryBean();

            List<String> basePackages = new ArrayList<>();
            basePackages.add(this.basePackage);
            for (String pkg : annoAttrs.getStringArray("basePackages")) {
                if (StringUtils.hasText(pkg)) {
                    basePackages.add(pkg);
                }
            }
            for (Class<?> clazz : annoAttrs.getClassArray("basePackageClasses")) {
                basePackages.add(ClassUtils.getPackageName(clazz));
            }

            scanner.registerFilters();
            scanner.doScan(StringUtils.toStringArray(basePackages));
        } catch (IllegalStateException ex) {
            this.logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.", ex);
        }
    }

    /**
     * 获取SqlSessionTemplate
     *
     * @return
     * @throws Exception
     */
    private void getSqlSessionTemplateAndDataSource() {
        try {
            Set<Map.Entry<String, Map<String, Object>>> entries = this.configMap.entrySet();
            beanConfig = new HashMap<>(entries.size());

            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(this.mapperLocations);
            // 资源文件分钟，此操作比较耗时但节省内存，可以注释
            for (Resource resource : resources) {
                String database = getAttachDataBase(resource);
                this.addSource(database, resource);
            }

            for (Map.Entry<String, Map<String, Object>> entry : entries) {
                String key = entry.getKey();
                Map<String, Object> map = entry.getValue();
                DataSource dataSource = buildDataSource(map);
                //为每个数据源设置事务
                DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
                dataSourceTransactionManager.setDataSource(dataSource);

                SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
                //设置dataSource数据源
                sqlSessionFactoryBean.setDataSource(dataSource);
                //设置*mapper.xml路径
                sqlSessionFactoryBean.setMapperLocations(this.resouceMap.get(key));
                // sqlSessionFactoryBean.setPlugins(new Interceptor[]{});
                //设置configLocation
                Resource location = getConfigLocation(this.configLocation);
                if (null != location) {
                    sqlSessionFactoryBean.setConfigLocation(location);
                }
                SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactoryBean.getObject());

                this.beanConfig.put(key + "SqlSessionFactoryBean", sqlSessionFactoryBean);
                this.beanConfig.put(key + "SqlSessionFactory", sqlSessionFactoryBean.getObject());
                this.beanConfig.put(key + "SqlSessionTemplate", sqlSessionTemplate);
                this.beanConfig.put(key + "DataSource", dataSource);
                this.beanConfig.put(key + MybatisConstant.DATASOURCE_TRANSACTION_MANAGER_SUBFIX, dataSourceTransactionManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.info(e.getMessage());
        }
    }

    /**
     * 获取mapper接口操作的数据库
     *
     * @param mapperClazz
     * @return
     */
    public static Object getSqlSessionFactoryByClass(String mapperClazz) {
        TargetDataSource targetDataSource = null;
        try {
            targetDataSource = Class.forName(mapperClazz).getAnnotation(TargetDataSource.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String datasource = null != targetDataSource ? targetDataSource.value() : MybatisConstant.DEFAULT_DATASOURCE;
        return beanConfig.get(datasource + "SqlSessionFactory");
    }

    /**
     * 获取mapper接口关联的SqlSessionTemplate
     *
     * @param mapperClazz
     * @return
     */
    public static Object getSqlSessionTemplateByClass(String mapperClazz) {
        TargetDataSource targetDataSource = null;
        try {
            targetDataSource = Class.forName(mapperClazz).getAnnotation(TargetDataSource.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String datasource = null != targetDataSource ? targetDataSource.value() : MybatisConstant.DEFAULT_DATASOURCE;
        return beanConfig.get(datasource + "SqlSessionTemplate");
    }


    /**
     * 扫描包里的mapper接口并完成数据源的绑定
     */
    private void getMapperFactoryBean() {
        // 扫描包，获取所有的mapper接口
        Map<String, Class<?>> mapperInterface = null;
        try {
            mapperInterface = ClassScanner.getMapperInterface(basePackage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Set<Map.Entry<String, Class<?>>> entries = mapperInterface.entrySet();
        // 为mapper接口绑定SqlSessionTemplate和SqlSessionFactory
        for (Map.Entry<String, Class<?>> entry : entries) {
            String name = MybatisConstant.DEFAULT_DATASOURCE;
            Class<?> value = entry.getValue();
            TargetDataSource targetDataSource = value.getAnnotation(TargetDataSource.class);
            if (null != targetDataSource) {
                name = targetDataSource.value();
            }
            MapperFactoryBean mapperFactoryBean = new MapperFactoryBean(value);
            SqlSessionTemplate template = (SqlSessionTemplate) beanConfig.get(name + "SqlSessionTemplate");
            mapperFactoryBean.setMapperInterface(value);
            mapperFactoryBean.setSqlSessionTemplate(template);
            mapperFactoryBean.setSqlSessionFactory(template.getSqlSessionFactory());
            mapperFactoryBean.afterPropertiesSet();
            this.mapperFactoryBeanMap.put(value.getName(), mapperFactoryBean);
        }
    }

    /**
     * 创建DataSource
     *
     * @param dsMap
     * @return
     */
    private DataSource buildDataSource(Map<String, Object> dsMap) throws ClassNotFoundException {
        try {
            Object type = dsMap.get("type");
            if (type == null)
                type = MybatisConstant.DATASOURCE_TYPE_DEFAULT;// 默认DataSource

            Class<? extends DataSource> dataSourceType;
            dataSourceType = (Class<? extends DataSource>) Class.forName((String) type);
            String driverName = "driver-class-name";
            if (null == dsMap.get("driver-class-name")) {
                driverName = "driverClassName";
            }
            String driverClassName = dsMap.get(driverName).toString();
            String url = dsMap.get("url").toString();
            String username = dsMap.get("username").toString();
            String password = dsMap.get("password").toString();
            DataSourceBuilder factory = DataSourceBuilder.create().driverClassName(driverClassName).url(url)
                    .username(username).password(password).type(dataSourceType);
            return factory.build();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
