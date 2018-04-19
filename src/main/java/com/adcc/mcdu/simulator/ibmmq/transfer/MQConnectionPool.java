package com.adcc.mcdu.simulator.ibmmq.transfer;

import com.adcc.utility.log.Log;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.MQSimpleConnectionManager;

import java.util.Hashtable;

/**
 * MQ连接池
 */
public class MQConnectionPool {

    // 池模式
    private int activeMode = MQSimpleConnectionManager.MODE_AUTO;

    // 超时时间
    private long timeout = 3000;

    // 最大连接数
    private int maxConnections = 1024;

    // 空闲连接数
    private int maxIdelConnections = 500;

    // ConnectionManager
    private MQSimpleConnectionManager connectionManager;

    /**
     * 构造函数
     */
    MQConnectionPool() {

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

    /**
     * 实例化ConnectionManager
     */
    public void initConnectionManager (){
        connectionManager= new MQSimpleConnectionManager();
        if(activeMode == MQSimpleConnectionManager.MODE_AUTO){
            connectionManager.setActive(MQSimpleConnectionManager.MODE_AUTO);
        }else if(activeMode == MQSimpleConnectionManager.MODE_ACTIVE){
            connectionManager.setActive(MQSimpleConnectionManager.MODE_ACTIVE);
        }else if(activeMode == MQSimpleConnectionManager.MODE_INACTIVE){
            connectionManager.setActive(MQSimpleConnectionManager.MODE_INACTIVE);
        }else{
            connectionManager.setActive(MQSimpleConnectionManager.MODE_ACTIVE);
        }
        connectionManager.setTimeout(timeout);
        connectionManager.setMaxConnections(maxConnections);
        connectionManager.setMaxUnusedConnections(maxIdelConnections);
        MQEnvironment.setDefaultConnectionManager(connectionManager);
    }

    /**
     * 取得QueueManager
     * @param qm
     * @param hashtable
     * @return
     */
    public MQQueueManager getQueueManager(String qm,Hashtable<Object,Object> hashtable) throws Exception{
        try{
            MQQueueManager mqm = new MQQueueManager(qm,hashtable,connectionManager);
            return mqm;
        }catch (Exception ex){
            Log.error(MQConnectionPool.class.getName(), "getQueueManager() error", ex);
            throw ex;
        }
    }

    /**
     * 返还QueueManager
     * @param mqm
     */
    public void returnQueueManager(MQQueueManager mqm) throws Exception{
        try{
            if(mqm.isConnected()){
                mqm.disconnect();
            }
            if(mqm.isOpen()){
                mqm.close();
            }
        }catch (Exception ex){
            Log.error(MQConnectionPool.class.getName(),"returnQueueManager() error",ex);
            throw ex;
        }
    }
}
