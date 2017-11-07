/**  
 * @Title: AddFriendReq.java
 * @date: 2015-3-25 上午10:02:05
 * @Copyright: (c) 2015, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.ObtainIdentifyCodeAndCheckRegisterResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * @Package: com.qingwing.net.request
 * @Description: 获取验证码同事判断手机号码是否被注册。手机号码必须没有被使用
 * @version: V1.0
 */
public class ObtainIdentifyCodeAndCheckRegisterReq extends BaseCommReq {
    private String url = ServerAddress.REGSITER_PHONE;
    private String mobile;
    private ObtainIdentifyCodeAndCheckRegisterResponse obtainIdentifyCodeAndCheckRegisterResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("ObtainIdentifyCodeAndCheckRegisterReq");
        postParams.put("mobile", mobile);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return ObtainIdentifyCodeAndCheckRegisterResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (obtainIdentifyCodeAndCheckRegisterResponse == null) {
            obtainIdentifyCodeAndCheckRegisterResponse = new ObtainIdentifyCodeAndCheckRegisterResponse();
        }
        return obtainIdentifyCodeAndCheckRegisterResponse;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
