package com.adcc.mcdu.simulator.ibmmq.transfer;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * MQ连接池工厂
 * Created by zyy on 2016/7/14.
 */
public class MQConnectionPoolFactory {

    // 单例对象
    private static MQConnectionPoolFactory instance;

    // MQConnectionPool对象集合
    private Map<String, MQConnectionPool> mqConnectionPoolMap = Maps.newConcurrentMap();

    /**
     * 构造函数
     * */
    private MQConnectionPoolFactory() {
    }

    /**
     * 单例方法
     * */
    public static synchronized MQConnectionPoolFactory getInstance() {
        if (instance == null) {
            instance = new MQConnectionPoolFactory();
        }
        return instance;
    }

    public MQConnectionPool getConnectionPool(String id) {
        if (Strings.isNullOrEmpty(id)) {
            return null;
        } else {
            if (!mqConnectionPoolMap.containsKey(id)) {
                mqConnectionPoolMap.put(id, new MQConnectionPool());
            }
            return mqConnectionPoolMap.get(id);
        }
    }

}
