package com.jyblife.datasource.core;

import com.jyblife.datasource.anotation.TargetDataSource;
import com.jyblife.datasource.constant.MybatisConstant;
import com.jyblife.datasource.util.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public abstract class MapperRegister implements BeanFactoryPostProcessor, ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware{

    private static Logger logger = LoggerFactory.getLogger(MapperRegister.class);

    public static final ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();

    static {
        //由于部分数据源配置不同，所以在此处添加别名，避免切换数据源出现某些参数无法注入的情况
        aliases.addAliases("url", new String[]{"jdbc-url"});
        aliases.addAliases("username", new String[]{"user"});
    }

    public Environment env;

    public ResourceLoader resourceLoader;

    public Binder binder;

    public static String multiDatasourceConfigKey;

    public static Map<String, Document> documentMap = new HashMap<>();

    /**
     * 存MapperFactory，DataSource，SqlSessionTemplate，DataSourceTransactionManager
     */
    public static Map<String, Object> beanConfig = new HashMap<>();
    /**
     * 存储各个数据配置
     */
    public static Map<String, Map<String, Object>> configMap = new HashMap<>();
    /**
     * 存与各个数据源有关的mapper资源文件
     */
    public static Map<String, Resource[]> resouceMap = new HashMap<>();

    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) {
        for (Map.Entry<String, Object> entry : this.beanConfig.entrySet()) {
            configurableListableBeanFactory.registerSingleton(entry.getKey(), entry.getValue());
        }
    }

    public abstract void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry);

    public void addBasePackageIntoEnvironment(String basePackage) {
        ConfigurableEnvironment c = (ConfigurableEnvironment) this.env;
        MutablePropertySources m = c.getPropertySources();
        Properties p = new Properties();
        p.put(MybatisConstant.EXECUTION_MYBATIS_BASEPACKAGE_KEY, basePackage);
        m.addFirst(new PropertiesPropertySource(MybatisConstant.ASPECT_MYBATIS_PROPERTIES_NAME, p));
    }

    public Resource getConfigLocation(String configLocation) {
        if (StringUtils.hasText(configLocation)) {
            Resource resource = this.resourceLoader.getResource(configLocation);
            if (null != resource) {
                return resource;
            }
            configLocation = this.env.getProperty(MybatisConstant.MYBATIS_CONFIG_LOCATION_KEY);
            return this.resourceLoader.getResource(configLocation);
        }
        try {
            // 如果匹配多个则返回第一个资源
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(MybatisConstant.DEFAULT_CONFIGLOCATION);
            if (null != resources && resources.length >= 1) {
                return resources[0];
            } else {
                this.logger.warn("config file can not find.");
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            this.logger.warn(e.getMessage());
            return null;
        }
    }

    public void loadConfigMap() {
        Map config, properties, defaultConfig = this.binder.bind("spring.datasource", Map.class).get(); //获取所有数据源配置
        properties = defaultConfig;
        this.configMap.put(MybatisConstant.DEFAULT_DATASOURCE, properties);
        this.resouceMap.put(MybatisConstant.DEFAULT_DATASOURCE, new Resource[0]);

        List<Map> configs = this.binder.bind(this.multiDatasourceConfigKey, Bindable.listOf(Map.class)).get(); //获取其他数据源配置
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


    public void addSource(String dataSource, Resource resource) {
        Resource[] oldResource = this.resouceMap.get(dataSource);
        Resource[] newResource = new Resource[oldResource.length + 1];
        System.arraycopy(oldResource, 0, newResource, 0, oldResource.length);
        newResource[oldResource.length] = resource;
        this.resouceMap.put(dataSource, newResource);
    }

    /**
     * @param resource
     * @return
     */
    public String getAttachDataBase(Resource resource) throws ClassNotFoundException, IOException {
        // 读取mapper.xml对应的数据库
        this.logger.info("正在解析【{}】", resource.getFilename());
        Map<String, Document> namespace;
        try {
            namespace = XmlUtil.getNamespace(resource.getFile());
            this.documentMap.putAll(namespace);
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
        if (null == annotation) {
            return MybatisConstant.DEFAULT_DATASOURCE;
        } else {
            return annotation.value();
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
        // 绑定配置器
        this.binder = Binder.get(environment);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
