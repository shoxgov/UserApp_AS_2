package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.ObtainThisTimeRecordResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

public class ObtainThisTimeRecordReq extends BaseCommReq {
    private String url = ServerAddress.GET_MESSAGE1;
    private String userId;
    private String barcode;
    private String startDate;
    private String endDate;

    private ObtainThisTimeRecordResponse obtainThisTimeRecordResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("ObtainThisTimeRecordReq");
        postParams.put("userId", userId);
        postParams.put("barcode", barcode);
        postParams.put("startDate", startDate);
        postParams.put("endDate", endDate);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return ObtainThisTimeRecordResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (obtainThisTimeRecordResponse == null) {
            obtainThisTimeRecordResponse = new ObtainThisTimeRecordResponse();
        }
        return obtainThisTimeRecordResponse;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}