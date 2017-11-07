package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.UserBindResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


/**
 * @Package: com.qingwing.net.request
 * @Description: 用户绑定
 */
public class UserBindReq extends BaseCommReq {
    private String url = ServerAddress.USER_BIND;
    private String userId;
    private String barcode;
    private String password;
    private UserBindResponse userBindResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("UserBindReq");
        postParams.put("userId", userId);
        postParams.put("barcode", barcode);
        postParams.put("password", password);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return UserBindResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (userBindResponse == null) {
            userBindResponse = new UserBindResponse();
        }
        return userBindResponse;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
