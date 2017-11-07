package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.UserUnbindResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


/**
 * @Package: com.qingwing.net.request
 * @Description: 用户解绑定
 */
public class UserUnbindReq extends BaseCommReq {
    private String url = ServerAddress.USER_UNBINDBOX;
    private String userId;
    private String barcode;
    private UserUnbindResponse userUnbindResponse;
    private Map<String, String> postParams = new HashMap<String, String>();
    @Override
    public String generUrl() {
        setTag("UserUnbindReq");
        postParams.put("userId", userId);
        postParams.put("barcode", barcode);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return UserUnbindResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (userUnbindResponse == null) {
            userUnbindResponse = new UserUnbindResponse();
        }
        return userUnbindResponse;
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

}
