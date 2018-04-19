package com.adcc.mcdu.simulator.ibmmq.client;

import com.adcc.mcdu.simulator.ibmmq.IBMMQFactory;
import com.adcc.mcdu.simulator.ibmmq.configuration.MQConfiguration;
import com.adcc.mcdu.simulator.ibmmq.entity.Message;
import com.adcc.mcdu.simulator.ibmmq.transfer.MQConnectionPool;
import com.adcc.mcdu.simulator.ibmmq.transfer.MQConnectionPoolFactory;
import com.adcc.mcdu.simulator.ibmmq.transfer.MQTransfer;
import com.adcc.mcdu.simulator.ibmmq.transfer.MQTransferImpl;
import com.adcc.utility.log.Log;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

/**
 * Created by ZHANG on 2016/7/22.
 */
public class IBMMQClient {
    private static IBMMQClient instance;
    private MQConnectionPool mqConnectionPool = null;
    private static final String CONF_AMQS = "conf";
    private static final String POOL_AMQS = "pool";

    private MQState state = MQState.DISCONNECTED;

    private String receiveQueueName ;

    private String sendQueueName;

    // 下行处理线程
    private DownlinkProcessRunnable dpr;

    private MQMessageListener mqMessageListener;

    private IBMMQClient(){}

    /**
     * 单例方法
     * */
    public synchronized static IBMMQClient getInstance() {
        if (instance == null) {
            instance = new IBMMQClient();
        }
        return instance;
    }

    // MQTransfer
    private MQTransfer transfer = new MQTransferImpl();

    public void build(IBMMQFactory ibmmqFactory,MQConfiguration mqConfiguration){
        mqConnectionPool = MQConnectionPoolFactory.getInstance().getConnectionPool(POOL_AMQS);
        mqConnectionPool.setMaxConnections(ibmmqFactory.getMaxConnections());
        mqConnectionPool.setActiveMode(ibmmqFactory.getActiveMode());
        mqConnectionPool.setMaxIdelConnections(ibmmqFactory.getMaxIdelConnections());
        mqConnectionPool.setTimeout(ibmmqFactory.getTimeout());
        transfer.setConfiguration(mqConfiguration);
    }

    public boolean connectAMQS(){
        transfer.setConnectionPool(mqConnectionPool);
        if(transfer.isConnectQM()){
            state = MQState.CONNECTED;
            return true;
        }
        return false;
    }

    public boolean isConnected(){
        if(state == MQState.CONNECTED){
            return true;
        }else{
            return false;
        }
    }

    public void start() throws Exception {
        try {
            dpr = new DownlinkProcessRunnable();
            new Thread(dpr).start();
        } catch (Exception ex) {
            Log.error(IBMMQClient.class.getName(), "start() error", ex);
        }
    }

    public void stop() throws Exception {
        try {
            if (dpr != null) {
                dpr.close();
            }
        } catch (Exception ex) {
            Log.error(IBMMQClient.class.getName(), "stop() error", ex);
        }
    }

    public boolean send(String message,String key,String val){
        boolean flag = false;
        try {
            if(Strings.isNullOrEmpty(key) || Strings.isNullOrEmpty(val)){
                transfer.sendQueue(sendQueueName,message.getBytes());
            }else{
                transfer.sendQueue(sendQueueName,message.getBytes(),key,val);
            }
            flag = true;
        }catch (Exception e){
            state = MQState.DISCONNECTED;
            Log.error("send to "+sendQueueName +" error",e);
        }
        return flag;
    }

    public void setReceiveQueueName(String receiveQueueName) {
        this.receiveQueueName = receiveQueueName;
    }

    public void setSendQueueName(String sendQueueName) {
        this.sendQueueName = sendQueueName;
    }

    public void setMqMessageListener(MQMessageListener mqMessageListener) {
        this.mqMessageListener = mqMessageListener;
    }

    /**
     * 下行处理线程
     */
    class DownlinkProcessRunnable implements Runnable {

        // 线程启动标识
        private boolean isStarted;

        /**
         * 关闭线程
         */
        public void close() {
            state =MQState.DISCONNECTED;
            isStarted = false;
        }

        @Override
        public void run() {
            try {
                isStarted = true;
                while (isStarted) {
                    try {
                        if(state == MQState.CONNECTED && null!= receiveQueueName){
                            System.out.println("DownlinkProcessRunnable start...");
                            Optional<Message> optional = transfer.receiveQueue(receiveQueueName);
                            if(mqMessageListener!=null){
                                mqMessageListener.onMessage(optional.get().toString());
                            }
                        }
                    }catch (Exception e){
                        Log.error(DownlinkProcessRunnable.class.getName(), "run() error", e);
                    }
                    Thread.sleep(50);
                }
            } catch (Exception ex) {
                Log.error(DownlinkProcessRunnable.class.getName(), "run() error", ex);
            }
        }
    }
}
