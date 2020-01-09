package com.jyblife.datasource.interceptor;

import java.lang.reflect.Method;

/**
 * 相当于一个单向的拦截器链表。其值域存放的是具体的拦截器方法。
 *
 */
public class IntercepterNodes implements IIntercepterChain {
    private IntercepterMethodDefination imd;//本拦截器方法定义
    private IntercepterNodes next;//下一个拦截器节点对象的引用

    public IntercepterNodes() {
    }

    public IntercepterNodes(IntercepterMethodDefination imd) {
        this.imd = imd;
    }

    public IntercepterMethodDefination getImd() {
        return imd;
    }

    public void setImd(IntercepterMethodDefination imd) {
        this.imd = imd;
    }

    public IntercepterNodes getNext() {
        return next;
    }

    public void setNext(IntercepterNodes next) {
        this.next = next;
    }

    //在首节点后追加拦截器。
    @Override
    public void add(IntercepterMethodDefination imd) {
        if(next == null) {
            next = new IntercepterNodes(imd);
            return;
        }else{
            if(next.getNext() == null) {
                next.setNext(new IntercepterNodes(imd));
            }else {
                next.getNext().add(imd);
            }
        }
    }

    @Override
    public boolean doBefore(Class<?> klass, Method method, Object[] args) {
        IntercepterTargetDefination itd = new IntercepterTargetDefination(klass, method);
        IntercepterNodes beforeInterFirstNode = IntercepterFactory.getBeforeNodes(itd);
        boolean result = true;
        for( ; beforeInterFirstNode != null;beforeInterFirstNode = beforeInterFirstNode.getNext()) {
            Method intercepterMethod = beforeInterFirstNode.getImd().getMethod();
            Object intercepterObj = beforeInterFirstNode.getImd().getObject();
            try {
                result = (boolean) intercepterMethod.invoke(intercepterObj, args);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(result == false) {
                return result;
            }
        }

        return result;
    }

    @Override
    public Object doAfter(Class<?> klass, Method method, Object args) {
        IntercepterTargetDefination itd = new IntercepterTargetDefination(klass, method);
        IntercepterNodes beforeInterFirstNode = IntercepterFactory.getAfterNodes(itd);
        Object result = null;
        for( ; beforeInterFirstNode != null;beforeInterFirstNode = beforeInterFirstNode.getNext()) {
            Method intercepterMethod = beforeInterFirstNode.getImd().getMethod();
            Object intercepterObj = beforeInterFirstNode.getImd().getObject();
            try {
                result = intercepterMethod.invoke(intercepterObj, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public void doException(Class<?> klass, Method method, Throwable E) {
        IntercepterTargetDefination itd = new IntercepterTargetDefination(klass, method);
        IntercepterNodes beforeInterFirstNode = IntercepterFactory.getExceptionNodes(itd);
        for( ; beforeInterFirstNode != null;beforeInterFirstNode = beforeInterFirstNode.getNext()) {
            Method intercepterMethod = beforeInterFirstNode.getImd().getMethod();
            Object intercepterObj = beforeInterFirstNode.getImd().getObject();
            try {
                intercepterMethod.invoke(intercepterObj, E);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

