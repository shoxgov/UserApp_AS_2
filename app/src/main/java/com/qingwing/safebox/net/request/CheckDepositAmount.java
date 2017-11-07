package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.CheckDepositAmountResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

public class CheckDepositAmount extends BaseCommReq {

    private String url = ServerAddress.DEPOSITAMOUNT;
    private int userId;
    private Map<String, String> postParams = new HashMap<String, String>();
    private CheckDepositAmountResponse mAmountResponse;

    @Override
    public String generUrl() {
        setTag("CheckDepositAmount");
        postParams.put("userId", userId + "");
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return CheckDepositAmountResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (mAmountResponse == null) {
            mAmountResponse = new CheckDepositAmountResponse();
        }
        return mAmountResponse;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
