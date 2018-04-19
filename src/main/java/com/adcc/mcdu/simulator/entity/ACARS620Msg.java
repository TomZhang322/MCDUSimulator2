package com.adcc.mcdu.simulator.entity;

import com.google.common.base.Strings;

public class ACARS620Msg {
    protected String priority = "QU";
    protected String[] recvAddress = new String[0];
    protected String sendAddress = Strings.nullToEmpty("");
    protected String sendTime = Strings.nullToEmpty("");
    protected String smi = Strings.nullToEmpty("");
    protected String fi = Strings.nullToEmpty("");
    protected String an = Strings.nullToEmpty("");
    protected String ma = Strings.nullToEmpty("");
    private String dsp = Strings.nullToEmpty("");
    private String rgs = Strings.nullToEmpty("");
    private String rgsTime = Strings.nullToEmpty("");
    private String msn = Strings.nullToEmpty("");
    private String freeText = Strings.nullToEmpty("");

    public ACARS620Msg() {
    }

    public String getPriority() {
        return this.priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String[] getRecvAddress() {
        return this.recvAddress;
    }

    public void setRecvAddress(String[] recvAddress) {
        this.recvAddress = recvAddress;
    }

    public String getSendAddress() {
        return this.sendAddress;
    }

    public void setSendAddress(String sendAddress) {
        this.sendAddress = sendAddress;
    }

    public String getSendTime() {
        return this.sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getSmi() {
        return this.smi;
    }

    public void setSmi(String smi) {
        this.smi = smi;
    }

    public String getFi() {
        return this.fi;
    }

    public void setFi(String fi) {
        this.fi = fi;
    }

    public String getAn() {
        return this.an;
    }

    public void setAn(String an) {
        this.an = an;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getDsp() {
        return this.dsp;
    }

    public void setDsp(String dsp) {
        this.dsp = dsp;
    }

    public String getRgs() {
        return this.rgs;
    }

    public void setRgs(String rgs) {
        this.rgs = rgs;
    }

    public String getRgsTime() {
        return this.rgsTime;
    }

    public void setRgsTime(String rgsTime) {
        this.rgsTime = rgsTime;
    }

    public String getMsn() {
        return this.msn;
    }

    public void setMsn(String msn) {
        this.msn = msn;
    }

    public String getFreeText() {
        return this.freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

}
