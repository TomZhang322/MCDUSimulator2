package com.adcc.mcdu.simulator.ibmmq;

public class AmqsFactory {
    private String host;
    private int port;
    private String queueManager;
    private String channel;
    private String recvQueue;
    private String sendQueue;
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
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
    }
    public String getRecvQueue() {
        return recvQueue;
    }
    public void setRecvQueue(String recvQueue) {
        this.recvQueue = recvQueue;
    }
    public String getSendQueue() {
        return sendQueue;
    }
    public void setSendQueue(String sendQueue) {
        this.sendQueue = sendQueue;
    }
}
