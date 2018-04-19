package com.adcc.mcdu.simulator.ibmmq.configuration;

import com.google.common.base.Strings;
import com.ibm.mq.MQC;

import java.util.Hashtable;

/**
 * MQ配置类
 */
public class MQConfiguration {

    // 主机名称
    private String host;

    // 端口
    private int port;

    // 队列管理器
    private String queueManager;

    // 通道
    private String channel;

    // MQ配置信息
    private Hashtable<Object,Object> params = new Hashtable<Object, Object>();

    /**
     * 构造函数
     */
    public MQConfiguration(){

    }

    /**
     * 构造函数
     * @param host
     * @param port
     * @param queueManager
     * @param channel
     */
    public MQConfiguration(String host,int port,String queueManager,String channel){
        this.host = host;
        if(!Strings.isNullOrEmpty(host)){
            params.put(MQC.HOST_NAME_PROPERTY,host);
        }
        this.port = port;
        if(port > 0 && port < 65535){
            params.put(MQC.PORT_PROPERTY,port);
        }
        this.queueManager = queueManager;
        this.channel = channel;
        if(!Strings.isNullOrEmpty(channel)){
            params.put(MQC.CHANNEL_PROPERTY,channel);
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
        if(!Strings.isNullOrEmpty(host)){
            params.put(MQC.HOST_NAME_PROPERTY,host);
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
        if(port > 0 && port < 65535){
            params.put(MQC.PORT_PROPERTY,port);
        }
    }

    public String getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(String queueManager) {
        this.queueManager = queueManager;

    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
        if(!Strings.isNullOrEmpty(channel)){
            params.put(MQC.CHANNEL_PROPERTY,channel);
        }
    }

    public Hashtable<Object, Object> getParams() {
        return params;
    }
}
