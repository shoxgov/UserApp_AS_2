package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

public class FeedBackResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;
    private String message;
    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
