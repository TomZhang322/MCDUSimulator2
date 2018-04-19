package com.adcc.mcdu.simulator.entity;

public class ACARS620Exception extends Exception {
    private String msg;
    private Throwable cause;

    public ACARS620Exception(String msg) {
        super(msg);
        this.msg = msg;
    }

    public ACARS620Exception(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
        this.cause = cause;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }
}
