package com.jyblife.datasource.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

public class ClassScanner {

    private static final Logger logger = LoggerFactory.getLogger(ClassScanner.class.getName());


    public static Map<String, Class<?>> getMapperInterface(String mapperInterfacePackage) throws Exception {
        logger.info(mapperInterfacePackage);
        Map<String, Class<?>> classMap = new HashMap<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        //将"."替换成"/"
        String packagePath = mapperInterfacePackage.replace(".", "/");
        URL url = loader.getResource(packagePath);
        if (url != null) {
            String type = url.getProtocol();

            if ("file".equals(type)) {
                ClassUtil.getClasses(mapperInterfacePackage).stream().forEach(item -> {
                    classMap.put(item.getName(), item);
                });
                return classMap;
            } else if ("jar".equals(type)) {
                ClassUtil.getClasses(mapperInterfacePackage).stream().forEach(item -> {
                    classMap.put(item.getName(), item);
                });
                return classMap;
            }
        }
        return null;
    }

    /**
     * 读取package下的所有类文件
     *
     * @param packagePath   包路径
     * @param childPackage  是否扫描子包
     * @return
     */
    private static List<String> getClassNameByPackage(String packagePath, boolean childPackage) {
        logger.info(packagePath);
        List<String> myClassName = new ArrayList<>();
        File file = new File(packagePath);
        File[] childFiles = file.listFiles();
        logger.info("文件数量：" + childFiles.length);
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    myClassName.addAll(getClassNameByPackage(childFile.getPath(), childPackage));
                }
            } else {
                String childFilePath = childFile.getPath();
                if (childFilePath.endsWith(".class")) {
                    childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9,
                            childFilePath.lastIndexOf("."));
                    childFilePath = childFilePath.replace("\\", ".");
                    myClassName.add(childFilePath);
                }
            }
        }
        return myClassName;
    }

    /**
     * 将Mapper的标准文件，转成 Mapper Class
     *
     * @param classPath
     * @return
     * @throws Exception
     */
    private static Map<String, Class<?>> getClassByPath(String classPath)
            throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Map<String, Class<?>> classMap = new HashMap<>();
        classMap.put(getClassAlias(classPath), loader.loadClass(getFullClassName(classPath)));
        return classMap;
    }

    /**
     * 将Mapper的标准文件，转成java标准的类名称
     *
     * @param classPath
     * @return
     * @throws Exception
     */
    private static String getFullClassName(String classPath)
            throws Exception {
        int comIndex = classPath.indexOf("com");
        classPath = classPath.substring(comIndex);
        classPath = classPath.replaceAll("\\/", ".");
        return classPath;
    }

    /**
     * 根据类地址，获取类的Alais，即根据名称，按照驼峰规则，生成可作为变量的名称
     *
     * @param classPath
     * @return
     * @throws Exception
     */
    private static String getClassAlias(String classPath) {
        String split = "\\/";
        String[] classTmp = classPath.split(split);
        String className = classTmp[classTmp.length - 1];
        return toLowerFisrtChar(className);
    }

    /**
     * 将字符串的第一个字母转小写
     *
     * @param className
     * @return
     */
    private static String toLowerFisrtChar(String className) {
        String fisrtChar = className.substring(0, 1);
        fisrtChar = fisrtChar.toLowerCase();
        return fisrtChar + className.substring(1);
    }
}
