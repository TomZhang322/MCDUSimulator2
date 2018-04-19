package com.adcc.mcdu.simulator.ibmmq.entity;

import com.ibm.mq.constants.CMQC;

/**
 * 队列实体
 */
public class Queue {

    /**
     * 本地队列
     */
    public static final int CONSTANT_LOCAL = CMQC.MQQT_LOCAL;

    /**
     * 别名队列
     */
    public static final int CONSTANT_ALIAS = CMQC.MQQT_ALIAS;

    /**
     * 模型队列
     */
    public static final int CONSTANT_MODEL = CMQC.MQQT_MODEL;

    /**
     * 远程队列
     */
    public static final int CONSTANT_REMOTE = CMQC.MQQT_REMOTE;

    // 名称
    private String name;

    // 类型
    private int type = CONSTANT_LOCAL;

    // 队列优先级
    private int priority = 0;

    // 持久化
    private boolean persistent = false;

    // 最大队列深度
    private int depth = 5000;

    // 最大消息长度
    private int msgMaxLength = 4194304;

    // 保留时间间隔
    private int retainInterval = 999999999;

    /**
     * 构造函数
     */
    public Queue(){

    }

    /**
     * 构造函数
     */
    public Queue(String name){
        this.name = name;
    }

    /**
     * 构造函数
     * @param name
     * @param type
     */
    public Queue(String name,int type){
        this.name = name;
        this.type = type;
    }

    /**
     * 构造函数
     * @param name
     * @param type
     * @param priority
     */
    public Queue(String name,int type,int priority){
        this.name = name;
        this.type = type;
        this.priority = priority;
    }

    /**
     * 构造函数
     * @param name
     * @param type
     * @param priority
     * @param persistent
     */
    public Queue(String name,int type,int priority,boolean persistent){
        this.name = name;
        this.type = type;
        this.priority = priority;
        this.persistent = persistent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getMsgMaxLength() {
        return msgMaxLength;
    }

    public void setMsgMaxLength(int msgMaxLength) {
        this.msgMaxLength = msgMaxLength;
    }

    public int getRetainInterval() {
        return retainInterval;
    }

    public void setRetainInterval(int retainInterval) {
        this.retainInterval = retainInterval;
    }
}
