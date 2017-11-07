package com.qingwing.safebox.bean;

public class MsgBoxRecord {

    private String opration;//操作
    private String time;

    public String getOpration() {
        return opration;
    }

    public void setOpration(String opration) {
        this.opration = opration;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "BoxRecord [opration=" + opration + ", time=" + time + "]";
    }
}
