package com.adcc.mcdu.simulator.ibmmq.transfer;

import com.adcc.mcdu.simulator.ibmmq.configuration.MQConfiguration;
import com.adcc.mcdu.simulator.ibmmq.entity.Message;
import com.adcc.utility.log.Log;
import com.google.common.base.Optional;
import com.ibm.mq.*;
import com.ibm.mq.MQMessage;
import com.ibm.mq.constants.CMQC;

/**
 * MQTransfer
 */
public class MQTransferImpl extends MQTransfer {

    /**
     * 构造器函数
     */
    public MQTransferImpl() {
        super();
    }

    /**
     * 构造函数
     * @param configuration
     */
    public MQTransferImpl(MQConfiguration configuration) {
        super(configuration);
    }

    /**
     * 构造函数
     * @param configuration
     * @param connectionPool
     */
    public MQTransferImpl(MQConfiguration configuration,MQConnectionPool connectionPool){
        super(configuration,connectionPool);
    }

    @Override
    public MQQueueManager getQueueManager() throws Exception{
        MQQueueManager mqm = null;
        if(connectionPool == null){
            mqm = new MQQueueManager(configuration.getQueueManager(),configuration.getParams());
        }else{
            mqm = connectionPool.getQueueManager(configuration.getQueueManager(),configuration.getParams());
        }
        return mqm;
    }

    @Override
    public void returnQueueManager(MQQueueManager mqm) throws Exception{
        if(mqm != null){
            if(connectionPool == null){
                if(mqm.isConnected()){
                    mqm.disconnect();
                }
                if(mqm.isOpen()){
                    mqm.close();
                }
            }else{
                connectionPool.returnQueueManager(mqm);
            }
        }
    }

    @Override
    public boolean isConnectQM(){
        MQQueueManager mqm = null;
        try{
            mqm = getQueueManager();
            if(mqm.isOpen() && mqm.isConnected() ){
                return true;
            }else{
                return false;
            }
        }catch (Exception ex){
            Log.error(MQTransfer.class.getName(), "isConnectQM() error", ex);
            return false;
        }finally {
            try {
                returnQueueManager(mqm);
            } catch (Exception ex) {
                Log.error("close mq error",ex);
            }
        }
    }

    @Override
    public Optional<Message> receiveQueue(String queue) throws Exception{
        MQQueueManager mqm = null;
        try{
            mqm = getQueueManager();
            MQGetMessageOptions gmo = new MQGetMessageOptions();
            int options = CMQC.MQOO_INPUT_AS_Q_DEF | CMQC.MQOO_INQUIRE | CMQC.MQOO_FAIL_IF_QUIESCING;
            gmo.options = CMQC.MQGMO_NO_WAIT | CMQC.MQGMO_CONVERT | CMQC.MQGMO_FAIL_IF_QUIESCING;
            MQQueue mqQueue = mqm.accessQueue(queue,options);
            if(mqQueue.isOpen() && mqQueue.getCurrentDepth() > 0){
                MQMessage mqMessage = new MQMessage();
                mqMessage.characterSet = 1208;
                mqQueue.get(mqMessage,gmo);
                int msgLength = mqMessage.getTotalMessageLength();
                byte[] buffer = new byte[msgLength];
                mqMessage.readFully(buffer,0,buffer.length);
                Message message = new Message(buffer);
                return Optional.of(message);
            }else {
                return Optional.absent();
            }
        }catch (Exception ex){
            Log.error(MQTransfer.class.getName(),"receive() error",ex);
            throw ex;
        }finally {
            returnQueueManager(mqm);
        }
    }

    @Override
    public Optional<Message> receiveQueue(String queue,String headKey) throws Exception {
        MQQueueManager mqm = null;
        try{
            mqm = getQueueManager();
            MQGetMessageOptions gmo = new MQGetMessageOptions();
            int options = CMQC.MQOO_INPUT_AS_Q_DEF | CMQC.MQOO_INQUIRE | CMQC.MQOO_OUTPUT;
            gmo.options = CMQC.MQGMO_NO_WAIT | CMQC.MQGMO_CONVERT | CMQC.MQGMO_FAIL_IF_QUIESCING;
            MQQueue mqQueue = mqm.accessQueue(queue,options);
            if(mqQueue.isOpen() && mqQueue.getCurrentDepth() > 0){
                MQMessage mqMessage = new MQMessage();
                mqMessage.characterSet = 1208;
                mqQueue.get(mqMessage,gmo);
                int msgLength = mqMessage.getMessageLength();
                String headValue = mqMessage.getStringProperty(headKey);
                byte[] buffer = new byte[msgLength];
                mqMessage.readFully(buffer,0,buffer.length);
                Message message = new Message(headValue,buffer);
                return Optional.of(message);
            }else{
                return Optional.absent();
            }
        }catch (Exception ex){
            Log.error(MQTransfer.class.getName(),"receive() error",ex);
            throw ex;
        }finally {
            returnQueueManager(mqm);
        }
    }

    @Override
    public void sendQueue(String queue, byte[] msg) throws Exception{
        MQQueueManager mqm = null;
        try{
            mqm =  getQueueManager();
            MQPutMessageOptions pmo = new MQPutMessageOptions();
            int options = CMQC.MQOO_OUTPUT | CMQC.MQOO_INQUIRE | CMQC.MQOO_FAIL_IF_QUIESCING;
            pmo.options = CMQC.MQPMO_FAIL_IF_QUIESCING | CMQC.MQPMO_ASYNC_RESPONSE;
            MQQueue mqQueue = mqm.accessQueue(queue,options);
            if(mqQueue.isOpen()){
                MQMessage mqMessage = new MQMessage();
                mqMessage.characterSet = 1208;
                mqMessage.write(msg,0,msg.length);
                mqQueue.put(mqMessage,pmo);
            }
        }catch (Exception ex){
            Log.error(MQTransfer.class.getName(),"send() error",ex);
            throw ex;
        }finally {
            returnQueueManager(mqm);
        }
    }

    @Override
    public void sendQueue(String queue, byte[] msg,String headKey,String headValue) throws Exception{
        MQQueueManager mqm = null;
        try{
            mqm =  getQueueManager();
            MQPutMessageOptions pmo = new MQPutMessageOptions();
            int options = CMQC.MQOO_OUTPUT | CMQC.MQOO_INQUIRE | CMQC.MQOO_FAIL_IF_QUIESCING;
            pmo.options = CMQC.MQPMO_FAIL_IF_QUIESCING | CMQC.MQPMO_ASYNC_RESPONSE;
            MQQueue mqQueue = mqm.accessQueue(queue,options);
            if(mqQueue.isOpen()){
                MQMessage mqMessage = new MQMessage();
                mqMessage.characterSet = 1208;
                mqMessage.write(msg,0,msg.length);
                mqMessage.setStringProperty(headKey,headValue);
                mqQueue.put(mqMessage,pmo);
            }
        }catch (Exception ex){
            Log.error(MQTransfer.class.getName(),"send() error",ex);
            throw ex;
        }finally {
            returnQueueManager(mqm);
        }
    }

    @Override
    public int getDepth(String queue) throws Exception {
        MQQueueManager mqm = null;
        try{
            mqm = getQueueManager();
            int options = CMQC.MQOO_INQUIRE | CMQC.MQOO_FAIL_IF_QUIESCING;
            MQQueue mqQueue = mqm.accessQueue(queue,options);
            int depth = mqQueue.getCurrentDepth();
            return depth;
        }catch (Exception ex){
            Log.error(MQTransfer.class.getName(),"send() error",ex);
            throw ex;
        }finally {
            returnQueueManager(mqm);
        }
    }

    @Override
    public void sendToTopic(String topic, byte[] message) throws Exception {
        MQQueueManager mqm = null;
        try{
            mqm = getQueueManager();
            MQPutMessageOptions pmo = new MQPutMessageOptions();
            int options = CMQC.MQOO_OUTPUT;
            pmo.options = CMQC.MQPMO_FAIL_IF_QUIESCING | CMQC.MQPMO_ASYNC_RESPONSE;
            MQTopic mqTopic = mqm.accessTopic(topic,topic,CMQC.MQTOPIC_OPEN_AS_PUBLICATION,options);
            if(mqTopic.isOpen()){
                MQMessage mqMessage = new MQMessage();
                mqMessage.characterSet = 1208;
                mqMessage.write(message,0,message.length);
                mqTopic.put(mqMessage,pmo);
            }
        }catch (Exception ex){
            Log.error(MQTransfer.class.getName(),"sendToTopic() error",ex);
            throw ex;
        }finally {
            returnQueueManager(mqm);
        }
    }

    @Override
    public byte[] receiveByTopic(String topic) throws Exception {
        MQQueueManager mqm = null;
        byte[] buffer = null;
        try{
            mqm = getQueueManager();
            MQGetMessageOptions gmo = new MQGetMessageOptions();
            int options = CMQC.MQSO_CREATE ;
            MQTopic mqTopic = mqm.accessTopic(topic,topic,CMQC.MQTOPIC_OPEN_AS_SUBSCRIPTION,options);
            if(mqTopic.isOpen()){
                MQMessage msg = new MQMessage();
                msg.characterSet = 1208;
                mqTopic.get(msg,gmo);
                int msgLength = msg.getMessageLength();
                buffer = new byte[msgLength];
                msg.readFully(buffer, 0, msgLength);
            }
        }finally {
            if(mqm != null){
                returnQueueManager(mqm);
            }
        }
        return buffer;
    }
}
