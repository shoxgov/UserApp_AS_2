package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.PayRecordResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


/**
 * @Package: com.qingwing.net.request
 */
public class PayRecordReq extends BaseCommReq {
    private String url = ServerAddress.PAYRECORD;
    private int userId;
    private PayRecordResponse payRecordResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("PayRecordReq");
        postParams.put("userId", userId + "");
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return PayRecordResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (payRecordResponse == null) {
            payRecordResponse = new PayRecordResponse();
        }
        return payRecordResponse;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
