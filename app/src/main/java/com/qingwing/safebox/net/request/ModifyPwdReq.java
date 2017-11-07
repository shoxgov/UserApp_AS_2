package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.ModifyPwdResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * @Package: com.qingwing.net.request
 * @Description: //用户修改登录密码
 */
public class ModifyPwdReq extends BaseCommReq {
    private String url = ServerAddress.USER_PASSWORD;
    private String mobile;
    private String password;
    private String code;
    private ModifyPwdResponse modifyPwdResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("ModifyPwdReq");
        postParams.put("mobile", mobile);
        postParams.put("password", password);
        postParams.put("code", code);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return ModifyPwdResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (modifyPwdResponse == null) {
            modifyPwdResponse = new ModifyPwdResponse();
        }
        return modifyPwdResponse;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
