package com.jyblife.datasource.interceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * 在包扫描时，将所有拦截器及其拦截目标放入工厂中
 * @author Lconf
 *
 */
public class IntercepterFactory {
    private static final Map<IntercepterTargetDefination, IntercepterNodes> beforeMap;
    private static final Map<IntercepterTargetDefination, IntercepterNodes> afterMap;
    private static final Map<IntercepterTargetDefination, IntercepterNodes> exceptionMap;

    static {
        beforeMap = new HashMap<>();
        afterMap = new HashMap<>();
        exceptionMap = new HashMap<>();
    }

    protected static IntercepterNodes getBeforeNodes(IntercepterTargetDefination itd) {
        return beforeMap.get(itd);
    }

    protected static IntercepterNodes getAfterNodes(IntercepterTargetDefination itd) {
        return afterMap.get(itd);
    }

    protected static IntercepterNodes getExceptionNodes(IntercepterTargetDefination itd) {
        return exceptionMap.get(itd);
    }

    /**
     * 增加拦截器的统一方式
     * 如果该类型节点之前未存在过，则进行首节点的创建，并将拦截器的值设定在首节点的值域中
     * 若该类型节点已经存在，则顺着链进行追加拦截器即可。
     * @param intercepterNodeMap
     * @param itd
     * @param imd
     */
    private void addIntercepter(Map<IntercepterTargetDefination, IntercepterNodes> intercepterNodeMap,
                                IntercepterTargetDefination itd, IntercepterMethodDefination imd) {
        IntercepterNodes firstNode = intercepterNodeMap.get(itd);

        if(firstNode == null) {
            /**
             * 其中考虑到单立数据的线程安全性
             * 创建首节点的的操作需要加锁，并且在锁中进行二次判断
             */
            synchronized (IntercepterFactory.class) {
                if(firstNode == null) {
                    firstNode = new IntercepterNodes();
                    intercepterNodeMap.put(itd, firstNode);
                    firstNode.setImd(imd);
                    return;
                }
            }
        }
        firstNode.add(imd);
    }

    protected void addBeforeIntercepter(IntercepterTargetDefination itd, IntercepterMethodDefination imd) {
        addIntercepter(beforeMap, itd, imd);
    }

    protected void addAfterIntercepter(IntercepterTargetDefination itd, IntercepterMethodDefination imd) {
        addIntercepter(afterMap, itd, imd);
    }

    protected void addExceptionIntercepter(IntercepterTargetDefination itd, IntercepterMethodDefination imd) {
        addIntercepter(exceptionMap, itd, imd);
    }

    protected IntercepterNodes getBeforeMethodList(IntercepterTargetDefination itd) {
        return beforeMap.get(itd);
    }

    protected IntercepterNodes getAfterMethodList(IntercepterTargetDefination itd) {
        return afterMap.get(itd);
    }

    protected IntercepterNodes getExceptionMethodList(IntercepterTargetDefination itd) {
        return exceptionMap.get(itd);
    }
}
