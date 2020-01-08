package com.jyblife.datasource.core;

import com.jyblife.datasource.annotation.EnableDatasources;
import com.jyblife.datasource.anotation.TargetDataSource;
import com.jyblife.datasource.constant.DatasourceConstant;
import com.jyblife.datasource.constant.XmlMapperData;
import com.jyblife.datasource.monitor.InvocationMapperMonitor;
import com.jyblife.datasource.monitor.PrepareInvocationMonitor;
import com.jyblife.datasource.util.ClassScanner;
import com.jyblife.datasource.util.XmlUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import tk.mybatis.spring.mapper.MapperFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 动态数据源注册
 */
@Component
public class MultiDataSourceRegister implements BeanFactoryPostProcessor,
        ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(MultiDataSourceRegister.class.getName());

    // 如配置文件中未指定数据源类型，使用该默认值
    private static final Object DATASOURCE_TYPE_DEFAULT = "org.apache.tomcat.jdbc.pool.DataSource";
    private final static ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases(); //别名
    static {
        //由于部分数据源配置不同，所以在此处添加别名，避免切换数据源出现某些参数无法注入的情况
        aliases.addAliases("url", new String[]{"jdbc-url"});
        aliases.addAliases("username", new String[]{"user"});
    }

    private Environment evn; //配置上下文

    private Binder binder; //参数绑定工具

    private ResourceLoader resourceLoader;

    public static final String DEFAULT_CONFIGLOCATION = "classpath:mybatis/**/mybatis-config.xml";

    private String configLocation;

    /**
     * 存储各个数据配置
     */
    private static Map<String, Map<String, Object>> configMap = new HashMap<>();

    /**
     * 存MapperFactory，DataSource，SqlSessionTemplate，DataSourceTransactionManager
     */
    public static Map<String, Object> beanConfig = new HashMap<>();

    /**
     * 存与各个数据源有关的mapper资源文件
     */
    private static Map<String, Resource[]> resouceMap = new HashMap<>();


    public static Map<String, MapperFactoryBean<?>> mapperFactoryBeanMap = new HashMap<>();

    /**
     * 获取mapper接口操作的数据库
     * @param clazz
     * @return
     */
    public static SqlSessionFactory getSqlSessionFactoryByClass(String clazz) {
        TargetDataSource targetDataSource = null;
        try {
            targetDataSource = Class.forName(clazz).getAnnotation(TargetDataSource.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String name = null != targetDataSource ? targetDataSource.value() : DatasourceConstant.DEFAULT_DATASOURCE;
        return (SqlSessionFactory) beanConfig.get(name + "SqlSessionFactory");
    }

    /**
     * 获取mapper接口关联的SqlSessionTemplate
     * @param clazz
     * @return
     */
    public static SqlSessionTemplate getSqlSessionTemplateByClass(String clazz) {
        TargetDataSource targetDataSource = null;
        try {
            targetDataSource = Class.forName(clazz).getAnnotation(TargetDataSource.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String name = null != targetDataSource ? targetDataSource.value() : DatasourceConstant.DEFAULT_DATASOURCE;
        return (SqlSessionTemplate) beanConfig.get(name + "SqlSessionTemplate");
    }

    private void addScource(String dataSource, Resource resource) {
        Resource[] oldResource = resouceMap.get(dataSource);
        Resource[] newResource = new Resource[oldResource.length + 1];
        System.arraycopy(oldResource,0, newResource, 0, oldResource.length);
        newResource[oldResource.length] = resource;
        resouceMap.put(dataSource, newResource);
    }

    private void loadConfigMap() {
        Map config, properties, defaultConfig = binder.bind("spring.datasource", Map.class).get(); //获取所有数据源配置
        properties = defaultConfig;
        this.configMap.put(DatasourceConstant.DEFAULT_DATASOURCE, properties);
        this.resouceMap.put(DatasourceConstant.DEFAULT_DATASOURCE, new Resource[0]);

        List<Map> configs = binder.bind("spring.datasource.multi", Bindable.listOf(Map.class)).get(); //获取其他数据源配置
        for (int i = 0; i < configs.size(); i++) { //遍历生成其他数据源
            config = configs.get(i);
            if ((boolean) config.getOrDefault("extend", Boolean.TRUE)) { //获取extend字段，未定义或为true则为继承状态
                properties = new HashMap(defaultConfig); //继承默认数据源配置
                properties.putAll(config); //添加数据源参数
            } else {
                properties = config; //不继承默认配置
            }
            String key = config.get("key").toString();
            this.configMap.put(key, properties);
            this.resouceMap.put(key, new Resource[0]);
        }
    }

    /**
     * 获取SqlSessionTemplate
     *
     * @return
     * @throws Exception
     */
    private void getSqlSessionTemplateAndDataSource(String mapperLocations) {
        try {
            Set<Map.Entry<String, Map<String, Object>>> entries = this.configMap.entrySet();
            beanConfig = new HashMap<>(entries.size());

            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(mapperLocations);
            for(Resource resource: resources){
                String database = getAttachDataBase(resource);
                addScource(database, resource);
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
                sqlSessionFactoryBean.setMapperLocations(resouceMap.get(key));
                sqlSessionFactoryBean.setPlugins(new Interceptor[]{new PrepareInvocationMonitor()});
                //设置configLocation
                Resource location = getConfigLocation();
                if(null != location){
                    sqlSessionFactoryBean.setConfigLocation(getConfigLocation());
                }
                SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactoryBean.getObject());

                beanConfig.put(key + "SqlSessionFactoryBean", sqlSessionFactoryBean);
                beanConfig.put(key + "SqlSessionFactory", sqlSessionFactoryBean.getObject());
                beanConfig.put(key + "SqlSessionTemplate", sqlSessionTemplate);
                beanConfig.put(key + "DataSource", dataSource);
                beanConfig.put(key + DatasourceConstant.DATASOURCE_TRANSACTION_MANAGER_SUBFIX, dataSourceTransactionManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
    }

    private Resource getConfigLocation(){

        if(StringUtils.hasText(configLocation)){
            Resource resource = this.resourceLoader.getResource(configLocation);
            if(null != resource){
                return resource;
            }
            configLocation = this.evn.getProperty("mybatis.configLocation");
            return this.resourceLoader.getResource(configLocation);
        }

        try {
            // 如果匹配多个则返回第一个资源
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(DEFAULT_CONFIGLOCATION);
            if(null != resources && resources.length >= 1){
                return resources[0];
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn(e.getMessage());
            return null;
        }
    }

    private void getMapperFactoryBean(String basePackage){
        // 扫描包，获取所有的mapper接口
        Map<String, Class<?>> mapperInterface = null;
        try {
            mapperInterface = ClassScanner.getMapperInterface(basePackage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Set<Map.Entry<String, Class<?>>> entries = mapperInterface.entrySet();
        for (Map.Entry<String, Class<?>> entry : entries) {
            // 获取接口所操作的数据库
            String name = DatasourceConstant.DEFAULT_DATASOURCE;
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

            mapperFactoryBeanMap.put(value.getName(),mapperFactoryBean);
        }
    }

    /**
     *
     * @param resource
     * @return
     */
    private String getAttachDataBase(Resource resource) throws ClassNotFoundException, IOException {
        // 读取mapper.xml对应的数据库
        logger.info("正在解析【{}】", resource.getFilename());
        Map<String, Document> namespace = null;
        try {
            namespace = XmlUtil.getNamespace(resource.getFile());
            XmlMapperData.documentMap.putAll(namespace);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("文件【{}】未找到", resource.getFilename());
            throw e;
        }
        Class<?> name;
        try {
            name = Class.forName(namespace.keySet().toArray()[0].toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.error("类【{}】未找到", namespace);
            throw e;
        }
        TargetDataSource annotation = name.getAnnotation(TargetDataSource.class);
        if(null == annotation){
            return DatasourceConstant.DEFAULT_DATASOURCE;
        }else{
            return annotation.value();
        }
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) {
        try {
            for (Map.Entry<String, Object> entry : beanConfig.entrySet()) {
                configurableListableBeanFactory.registerSingleton(entry.getKey(),entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        logger.info("Searching for mappers");
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
        scanner.setMapperProperties(this.evn);
        try {

            AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableDatasources.class.getName()));

            Class<? extends Annotation> annotationClass = annoAttrs.getClass("annotationClass");
            if (!Annotation.class.equals(annotationClass)) {
                scanner.setAnnotationClass(annotationClass);
            } else {
                scanner.setAnnotationClass(Mapper.class);
            }

            if (this.resourceLoader != null) {
                scanner.setResourceLoader(this.resourceLoader);
            }

            String basePackage = annoAttrs.getString("basePackage");
            if(!StringUtils.hasText(basePackage)){
                basePackage = this.evn.getProperty("mybatis.basePackage");
            }
            if(!StringUtils.hasText(basePackage)){
                throw new RuntimeException("EnableDatasource必须配置basePackage属性");
            }else {
                InvocationMapperMonitor.mapperPackage = basePackage;
            }

            String mapperLocations = annoAttrs.getString("mapperLocations");
            if(!StringUtils.hasText(mapperLocations)){
                throw new RuntimeException("EnableDatasource必须配置mapperLocations属性");
            }

            configLocation = annoAttrs.getString("configLocation");
            if(!StringUtils.hasText(configLocation)){
                logger.warn("EnableDatasource未配置configLocation属性");
            }

            this.loadConfigMap();
            this.getSqlSessionTemplateAndDataSource(mapperLocations);
            this.getMapperFactoryBean(basePackage);

            List<String> basePackages = new ArrayList<>();
            basePackages.add(basePackage);
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
            logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.", ex);
        }
    }


    /**
     * 创建DataSource
     *
     * @param dsMap
     * @return
     */
    private DataSource buildDataSource(Map<String, Object> dsMap) {
        try {
            Object type = dsMap.get("type");
            if (type == null)
                type = DATASOURCE_TYPE_DEFAULT;// 默认DataSource

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
        }
        return null;
    }

    private <T extends DataSource> T bind(Class<T> clazz, Map properties) {
        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(new ConfigurationPropertySource[]{source.withAliases(aliases)});
        return binder.bind(ConfigurationPropertyName.EMPTY, Bindable.of(clazz)).get(); //通过类型绑定参数并获得实例对象
    }

    @Override
    public void setEnvironment(Environment env) {
        this.evn = env;
        // 绑定配置器
        binder = Binder.get(evn);
    }


    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
