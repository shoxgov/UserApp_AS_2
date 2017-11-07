package com.qingwing.safebox.net.response;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


/**
 * @Class: CheckOpenBoxPwdReq
 * @Package: com.qingwing.net.request
 * @Description:得到用户输入的密码是否正确
 */
public class CheckOpenBoxPwdReq extends BaseCommReq {
    private String url = ServerAddress.USER_OPENPASSWORDCHECK_URL;
    private int userId;
    private String password;
    private CheckOpenBoxPwdResponse checkOpenBoxPwdResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("CheckOpenBoxPwdReq");
        postParams.put("userId", userId + "");
        postParams.put("password", password);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return CheckOpenBoxPwdResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (checkOpenBoxPwdResponse == null) {
            checkOpenBoxPwdResponse = new CheckOpenBoxPwdResponse();
        }
        return checkOpenBoxPwdResponse;
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
