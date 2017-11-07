package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.BindNewTelephoneResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


/**
 * @Class: BindNewTelephoneReq
 * @Package: com.qingwing.net.request
 * @Description: 修改绑定手机
 */
public class BindNewTelephoneReq extends BaseCommReq {
    private String url = ServerAddress.BINDNEWPHONE;
    private int userId;
    private String mobile;
    private String loginPassword;

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    private String code;

    private BindNewTelephoneResponse bindTelephoneResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("BindNewTelephoneReq");
        postParams.put("userId", userId + "");
        postParams.put("mobile", mobile);
        postParams.put("password", loginPassword);
        postParams.put("code", code);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return BindNewTelephoneResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (bindTelephoneResponse == null) {
            bindTelephoneResponse = new BindNewTelephoneResponse();
        }
        return bindTelephoneResponse;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
