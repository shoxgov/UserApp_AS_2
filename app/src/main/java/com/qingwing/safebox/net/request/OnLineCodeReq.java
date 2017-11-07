package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.OnLineCodeResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

public class OnLineCodeReq extends BaseCommReq {

    private String url = ServerAddress.ONLINECODE;
    private String userId;
    private String onlineCode;
    private OnLineCodeResponse mCodeResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("OnLineCodeReq");
        postParams.put("userId", userId);
        postParams.put("onlineCode", onlineCode);
        setPostParam(postParams);
        return url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOnlineCode() {
        return onlineCode;
    }

    public void setOnlineCode(String onlineCode) {
        this.onlineCode = onlineCode;
    }

    @Override
    public Class getResClass() {
        return OnLineCodeResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (mCodeResponse == null) {
            mCodeResponse = new OnLineCodeResponse();
        }
        return mCodeResponse;
    }
}
