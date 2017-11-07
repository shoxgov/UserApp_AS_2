package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.ReturnDepositResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

/**
 *name（真实姓名）
 type（账户类型，1为支付宝，3为银行卡）
 */
public class ReturnDepositReq extends BaseCommReq {
    private String url = ServerAddress.RETURNDEPOSIT;
    private String name, type, account, userId;//用户真实姓名，退还账号类型，账号
    private ReturnDepositResponse mDepositResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("ReturnDepositReq");
        postParams.put("name", name);
        postParams.put("type", type);
        postParams.put("account", account);
        postParams.put("userId", userId);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return ReturnDepositResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (mDepositResponse == null) {
            mDepositResponse = new ReturnDepositResponse();
        }
        return mDepositResponse;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
