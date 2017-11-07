package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.RefundDepositResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

public class RefundDepositReq extends BaseCommReq {

    private String url = ServerAddress.ISREFUNDDEPOSIT;
    private int userId;
    private Map<String, String> postParams = new HashMap<String, String>();
    private RefundDepositResponse mRefundDepositResponse;

    @Override
    public String generUrl() {
        setTag("RefundDepositReq");
        postParams.put("userId", userId + "");
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return RefundDepositResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (mRefundDepositResponse == null) {
            mRefundDepositResponse = new RefundDepositResponse();
        }
        return mRefundDepositResponse;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
