package com.adcc.mcdu.simulator.ibmmq.configuration;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * MQ配置类工厂
 * Created by zyy on 2016/7/14.
 */
public class MQConfigurationFactory {

    // 单例对象
    private static MQConfigurationFactory instance;

    // MQConfiguration对象集合
    private Map<String, MQConfiguration> mqConfigurationMap = Maps.newConcurrentMap();

    /**
     * 构造函数
     * */
    private MQConfigurationFactory() {
    }

    /**
     * 单例方法
     * */
    public static synchronized MQConfigurationFactory getInstance() {
        if (instance == null) {
            instance = new MQConfigurationFactory();
        }
        return instance;
    }

    public MQConfiguration getConfiguration(String id) {
        if (Strings.isNullOrEmpty(id)) {
            return null;
        } else {
            if (!mqConfigurationMap.containsKey(id)) {
                mqConfigurationMap.put(id, new MQConfiguration());
            }
            return mqConfigurationMap.get(id);
        }
    }
 }
