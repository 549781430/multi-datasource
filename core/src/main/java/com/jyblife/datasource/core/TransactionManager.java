package com.jyblife.datasource.core;

import com.jyblife.datasource.constant.MybatisConstant;
import com.jyblife.datasource.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StringUtils;

import java.util.Stack;

/**
 * 事务管理器
 */
public class TransactionManager {

    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class.getName());

    public static ThreadLocal<Stack<DataSourceTransactionManager>> dataSourceTransactionManagersLocal = new ThreadLocal<>();

    public static ThreadLocal<Stack<TransactionStatus>> transactionStatusLocal = new ThreadLocal<>();

    public static ThreadLocal<Boolean> openFlag = new ThreadLocal<>();

    public static boolean isOpen(){
        return null == openFlag.get() ? false : openFlag.get();
    }

    public static void putTrasaction(String name) {

        if (null != openFlag.get() && openFlag.get()) {
            if (StringUtils.isEmpty(name)) {
                name = MybatisConstant.DEFAULT_DATASOURCE;
            }

            Stack<DataSourceTransactionManager> dataSourceTransactionManagerStack = dataSourceTransactionManagersLocal.get();
            if (null == dataSourceTransactionManagerStack) {
                dataSourceTransactionManagerStack = new Stack<>();
            }
            Stack<TransactionStatus> transactionStatuStack = transactionStatusLocal.get();
            if (null == transactionStatuStack) {
                transactionStatuStack = new Stack<>();
            }
            //根据事务名称获取具体的事务
            DataSourceTransactionManager dataSourceTransactionManager = (DataSourceTransactionManager) SpringContextUtil
                    .getBean(name + MybatisConstant.DATASOURCE_TRANSACTION_MANAGER_SUBFIX);
            if (!dataSourceTransactionManagerStack.contains(dataSourceTransactionManager)) {
                TransactionStatus transactionStatus = dataSourceTransactionManager
                        .getTransaction(new DefaultTransactionDefinition());
                transactionStatuStack.push(transactionStatus);
                dataSourceTransactionManagerStack.push(dataSourceTransactionManager);

                dataSourceTransactionManagersLocal.set(dataSourceTransactionManagerStack);
                transactionStatusLocal.set(transactionStatuStack);
            }
        }
    }

    /**
     * 开启事务
     *
     * @return
     */
    public static void open() {
        if(isOpen()){
            return;
        }
        logger.info("open transaction.");
        openFlag.set(true);
        Stack<DataSourceTransactionManager> dataSourceTransactionManagerStack = new Stack<>();
        Stack<TransactionStatus> transactionStatuStack = new Stack<>();

        dataSourceTransactionManagersLocal.set(dataSourceTransactionManagerStack);
        transactionStatusLocal.set(transactionStatuStack);
    }

    /**
     * 提交处理方法
     */
    public static void commit() {
        if(dataSourceTransactionManagersLocal.get().size() < 1){
            return;
        }
        logger.info("commit transaction, transaction size {}", dataSourceTransactionManagersLocal.get().size());
        Stack<DataSourceTransactionManager> dataSourceTransactionManagers = dataSourceTransactionManagersLocal.get();

        while (!dataSourceTransactionManagers.isEmpty()) {
            DataSourceTransactionManager manager = dataSourceTransactionManagers.pop();
            TransactionStatus status = transactionStatusLocal.get().pop();
            manager.commit(status);
        }
    }

    /**
     * 回滚处理方法
     */
    public static void rollback() {
        logger.info("rollback transaction.");
        Stack<DataSourceTransactionManager> dataSourceTransactionManagers = dataSourceTransactionManagersLocal.get();
        while (!dataSourceTransactionManagers.isEmpty()) {
            dataSourceTransactionManagers.pop().rollback(transactionStatusLocal.get().pop());
        }
    }

    /**
     * 是否存在事务
     * @return
     */
    public static boolean hasTransaction() {
        return null != dataSourceTransactionManagersLocal.get() && dataSourceTransactionManagersLocal.get().size() > 0;
    }
}
