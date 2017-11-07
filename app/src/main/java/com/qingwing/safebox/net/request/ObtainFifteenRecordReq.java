package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.ObtainFifteenRecordResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

public class ObtainFifteenRecordReq extends BaseCommReq {
    private String url = ServerAddress.GET_MESSAGE1;
    private String userId;
    private String barcode;
    private String thisDate;
    private ObtainFifteenRecordResponse obtainFifteenRecordResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("ObtainRecordInfoReq");
        postParams.put("userId", userId);
        postParams.put("barcode", barcode);
        postParams.put("thisDate", thisDate);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return ObtainFifteenRecordResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (obtainFifteenRecordResponse == null) {
            obtainFifteenRecordResponse = new ObtainFifteenRecordResponse();
        }
        return obtainFifteenRecordResponse;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getThisDate() {
        return thisDate;
    }

    public void setThisDate(String thisDate) {
        this.thisDate = thisDate;
    }


}