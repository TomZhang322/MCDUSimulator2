package com.adcc.mcdu.simulator.ibmmq;

import com.google.common.collect.Lists;

import java.util.List;

public class IBMMQFactory {
    // AMQS
    private List<AmqsFactory> amqsFactoryList = Lists.newArrayList();

    // ActiveMode
    private int activeMode;

    // 连接超时时间
    private long timeout;

    // 最大连接数
    private int maxConnections;

    // 最大空闲连接数
    private int maxIdelConnections;


    public List<AmqsFactory> getAmqsFactoryList() {
        return amqsFactoryList;
    }

    public void setAmqsFactoryList(List<AmqsFactory> amqsFactoryList) {
        this.amqsFactoryList = amqsFactoryList;
    }

    public int getActiveMode() {
        return activeMode;
    }

    public void setActiveMode(int activeMode) {
        this.activeMode = activeMode;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMaxIdelConnections() {
        return maxIdelConnections;
    }

    public void setMaxIdelConnections(int maxIdelConnections) {
        this.maxIdelConnections = maxIdelConnections;
    }
}
