package com.qingwing.safebox.net.request;

import android.text.TextUtils;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.SendPwdToTelephoneResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * @Package: com.qingwing.net.request
 * @version: V1.0
 */
public class SendPwdToTelephoneReq extends BaseCommReq {
    private String url = ServerAddress.SENDOPENPASSWORD;
    private int userId;
    private String password;
    private String code;//可选参数：code(重置开箱密码时需传)
    private SendPwdToTelephoneResponse response;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("SendPwdToTelephoneReq");
        postParams.put("userId", userId + "");
        postParams.put("password", password);
        if (!TextUtils.isEmpty(getCode())) {
            postParams.put("code", getCode());
        }
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return SendPwdToTelephoneResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (response == null) {
            response = new SendPwdToTelephoneResponse();
        }
        return response;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
