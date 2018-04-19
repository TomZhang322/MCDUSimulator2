package com.adcc.mcdu.simulator.ibmmq.transfer;

import com.adcc.mcdu.simulator.ibmmq.configuration.MQConfiguration;
import com.adcc.mcdu.simulator.ibmmq.entity.Message;
import com.google.common.base.Optional;
import com.ibm.mq.MQQueueManager;

/**
 * MQTransfer
 */
public abstract class MQTransfer {

    // MQConfiguration
    protected MQConfiguration configuration;

    // MQConnectionPool
    protected MQConnectionPool connectionPool;

    /**
     * 构造函数
     */
    public MQTransfer(){

    }

    /**
     * 构造函数
     * @param configuration
     */
    public MQTransfer(MQConfiguration configuration){
        this.configuration = configuration;
    }

    /**
     * 构造函数
     * @param configuration
     * @param connectionPool
     */
    public MQTransfer(MQConfiguration configuration,MQConnectionPool connectionPool){
        this.configuration = configuration;
        this.connectionPool = connectionPool;
        this.connectionPool.initConnectionManager();
    }

    public void  setConfiguration(MQConfiguration configuration){
        this.configuration = configuration;
    }

    public void setConnectionPool(MQConnectionPool connectionPool){
        this.connectionPool = connectionPool;
        this.connectionPool.initConnectionManager();
    }

    /**
     * 取得QueueManager
     * @return
     */
    public abstract MQQueueManager getQueueManager() throws Exception;

    /**
     * 返还QueueManager
     * @param mqm
     * @throws Exception
     */
    public abstract void returnQueueManager(MQQueueManager mqm) throws Exception;

    /**
     * 是否连接QueuManager
     * @return
     * @throws Exception
     */
    public abstract boolean isConnectQM();

    /**
     * 从队列接收消息
     * @param queue
     * @return
     */
    public abstract Optional<Message> receiveQueue(String queue) throws Exception;

    /**
     * 从队列接收带头部的消息
     * @param queue
     * @param headKey
     * @return
     * @throws Exception
     */
    public abstract Optional<Message> receiveQueue(String queue,String headKey) throws Exception;

    /**
     * 向队列发送消息
     * @param queue
     * @param message
     */
    public abstract void sendQueue(String queue,byte[] message) throws Exception;

    /**
     * 向队列发送消息
     * @param queue
     * @param message
     * @param headKey
     * @param headValue
     */
    public abstract void sendQueue(String queue,byte[] message,String headKey,String headValue) throws Exception;

    /**
     * 取得队列深度
     * @param queue
     * @throws Exception
     */
    public abstract int getDepth(String queue) throws Exception;

    /**
     * 向主题发送消息
     * @param topic
     * @param message
     * @throws Exception
     */
    public abstract void sendToTopic(String topic,byte[] message) throws Exception;

    /**
     * 接收主题消息
     * @param topic
     * @return
     * @throws Exception
     */
    public abstract byte[] receiveByTopic(String topic) throws Exception;
}
