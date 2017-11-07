package com.qingwing.safebox.net;

import java.io.Serializable;

/**
 * "ReturnCode":"SUCESS",data:{}
 * "ReturnCode":"FAIL","error":"相关错误信息"
 */
public class BaseResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private int statusCode;// 200是成功。500是失败。

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
