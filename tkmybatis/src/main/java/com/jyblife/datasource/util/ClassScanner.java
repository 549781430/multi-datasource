package com.jyblife.datasource.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassScanner {

    public static Map<String, Class<?>> getMapperInterface(String mapperInterfacePackage) throws Exception {
        Map<String, Class<?>> classMap = new HashMap<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        //将"."替换成"/"
        String packagePath = mapperInterfacePackage.replace(".", "/");
        URL url = loader.getResource(packagePath);
        List<String> fileNames = null;
        if (url != null) {
            String type = url.getProtocol();
            if ("file".equals(type)) {
                fileNames = getClassNameByFile(url.getPath(), null, true);
            }
        }
        for (String classPath : fileNames) {
            classMap.putAll(getClassByPath(classPath));
        }
        return classMap;
    }

    /**
     * 读取package下的所有类文件
     *
     * @param filePath
     * @param className
     * @param childPackage
     * @return
     */
    private static List<String> getClassNameByFile(String filePath, List<String> className, boolean childPackage) {
        List<String> myClassName = new ArrayList<>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    myClassName.addAll(getClassNameByFile(childFile.getPath(), myClassName, childPackage));
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
    private static String getClassAlias(String classPath)
            throws Exception {
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
