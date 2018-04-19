package com.adcc.mcdu.simulator.ibmmq.exception;

import com.google.common.base.Strings;

/**
 * Created by zyy on 2016/7/15.
 */
public class MQCommonException {

    // MQ异常结果
    private MQExceptionResult result = MQExceptionResult.SUCCESS;

    // MQ异常代码
    private int code;

    // MQ异常原因
    private String reason = Strings.nullToEmpty("");

    /**
     * 构造方法
     * */
    public MQCommonException() {}

    /**
     * 构造方法
     * */
    public MQCommonException(int code, String reason) {
        this.reason = reason;
        setCode(code);
    }

    public MQExceptionResult getResult() {
        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
        this.result = checkMQResultState(code);
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    private MQExceptionResult checkMQResultState(int code) {
        MQExceptionResult temp;
        switch (code) {
            case 0:
                temp = MQExceptionResult.SUCCESS;
                break;
            // 2033: MQRC_NO_MSG_AVAILABLE
            case 2033:
                temp = MQExceptionResult.SUCCESS;
                break;
            // 2085: MQRC_Q_MGR_NAME_ERROR
            case 2058:
                temp = MQExceptionResult.FAILURE;
                break;
            // 2085: MQRC_UNKNOWN_OBJECT_NAME
            case 2085:
                temp = MQExceptionResult.FAILURE;
                break;
            // 2538: MQRC_HOST_NOT_AVAILABLE
            case 2538:
                temp = MQExceptionResult.FAILURE;
                break;
            // 2540: MQRC_UNKNOWN_CHANNEL_NAME
            case 2540:
                temp = MQExceptionResult.FAILURE;
                break;
            default:
                temp = MQExceptionResult.SUCCESS;
                break;
        }
        return temp;
    }
}
