/**
 * @Title: AddFriendReq.java
 * @date: 2015-3-25 上午10:02:05
 * @Copyright: (c) 2015, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.NewUserBindResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


/**
 * @Class: NewUserBindReq
 * @Description: 老用户未解绑新用户绑定
 */
public class NewUserBindReq extends BaseCommReq {
    private String url = ServerAddress.NEW_USER_BIND;
    private String userId;
    private String barcode;
    private String password;
    private NewUserBindResponse newUserBindResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("NewUserBindReq");
        postParams.put("userId", userId);
        postParams.put("barcode", barcode);
        postParams.put("password", password);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return NewUserBindResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (newUserBindResponse == null) {
            newUserBindResponse = new NewUserBindResponse();
        }
        return newUserBindResponse;
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
