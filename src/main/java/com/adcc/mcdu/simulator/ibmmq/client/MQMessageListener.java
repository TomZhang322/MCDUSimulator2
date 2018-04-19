package com.adcc.mcdu.simulator.ibmmq.client;

/**
 * Created by ZHANG on 2016/7/25.
 */
public interface MQMessageListener {
    public void onMessage(String message);
}
