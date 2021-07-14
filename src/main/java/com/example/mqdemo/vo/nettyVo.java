package com.example.mqdemo.vo;

import java.io.Serializable;

public class nettyVo implements Serializable {
    private String userId;
    private String msg;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
