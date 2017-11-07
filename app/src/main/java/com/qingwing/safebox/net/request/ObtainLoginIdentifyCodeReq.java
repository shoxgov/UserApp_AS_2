package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.IdentifyCodeResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取修改登录密码验证码
 */
public class ObtainLoginIdentifyCodeReq extends BaseCommReq {
    private String url = ServerAddress.SERVER_URL + "user-loginPassCode.action";
    private String mobile;
    private IdentifyCodeResponse identifyCodeResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("ObtainLoginIdentifyCodeReq");
        postParams.put("mobile", mobile);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return IdentifyCodeResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (identifyCodeResponse == null) {
            identifyCodeResponse = new IdentifyCodeResponse();
        }
        return identifyCodeResponse;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
