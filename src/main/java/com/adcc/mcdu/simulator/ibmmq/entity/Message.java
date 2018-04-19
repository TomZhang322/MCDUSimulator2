package com.adcc.mcdu.simulator.ibmmq.entity;

import com.google.common.base.Strings;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 消息实体
 */
public class Message {

    // 消息头
    private String head;

    // 消息体
    private byte[] content;

    // 时间戳
    private String timestamp;

    /**
     * 构造函数
     */
    public Message(){

    }

    /**
     * 构造函数
     */
    public Message(byte[] content){
        this.content = content;
        timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * 构造函数
     * @param head
     * @param content
     */
    public Message(String head,byte[] content){
        this.head = head;
        this.content = content;
        timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        String result = Strings.nullToEmpty("");
        if(content != null){
            if(!Strings.isNullOrEmpty(head)){
                result += head + "\r\n";
            }
            result += new String(content,0,content.length);
        }
        return result;
    }
}
