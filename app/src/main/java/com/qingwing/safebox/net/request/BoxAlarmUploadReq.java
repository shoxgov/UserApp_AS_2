package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.BoxAlarmUploadResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

public class BoxAlarmUploadReq extends BaseCommReq {

    public String url = ServerAddress.BOXWARNBOXUPLOAD;
    private String barcode;
    private String actionType;
    private String date;
    private BoxAlarmUploadResponse warnUploadResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("BoxAlarmUploadReq");
        postParams.put("barcode", barcode);
        postParams.put("actionType", actionType);
        postParams.put("date", date);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return BoxAlarmUploadResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (warnUploadResponse == null) {
            warnUploadResponse = new BoxAlarmUploadResponse();
        }
        return warnUploadResponse;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
