package com.adcc.mcdu.simulator.ibmmq.entity;

import com.ibm.mq.constants.CMQC;

/**
 * Created by zhangdawei on 2016/7/8.
 */
public class Topic {

    /**
     * 本地主题类型
     */
    public static final int LOCAL_TOPIC = CMQC.MQTOPT_LOCAL;

    /**
     * 所有主题类型
     */
    public static final int ALL_TOPIC = CMQC.MQTOPT_ALL;

    /**
     * 集群主题类型
     */
    public static final int CLUSTER_TOPIC = CMQC.MQTOPT_CLUSTER;

    //Topic name
    private String name;

    //优先级
    private int priority;

    // 持久化
    private boolean persistent = false;

    //主题类型
    private int type;

    /**
     * 构造函数
     */
    public Topic(){
    }

    public Topic(String name){
        this.name = name;
    }

    public Topic(String name,int type,int priority,boolean persistent){
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
