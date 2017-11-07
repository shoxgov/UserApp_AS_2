package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.ChangeOpenPwdResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


public class ChangeOpenPwdReq extends BaseCommReq {
    private String url = ServerAddress.USER_SETOPENPASSWORD_URL;
    private int userId;
    private String password;
    private ChangeOpenPwdResponse changeOpenPwdResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("ChangeOpenPwdReq");
        postParams.put("userId", userId + "");
        postParams.put("password", password);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return ChangeOpenPwdResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (changeOpenPwdResponse == null) {
            changeOpenPwdResponse = new ChangeOpenPwdResponse();
        }
        return changeOpenPwdResponse;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
