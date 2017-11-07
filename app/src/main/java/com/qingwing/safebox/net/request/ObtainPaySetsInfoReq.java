package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.ObtainPaySetsInfoResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * @Class: ObtainPaySetsInfoReq
 * @Package: com.qingwing.net.request
 */
public class ObtainPaySetsInfoReq extends BaseCommReq {
    private String url = ServerAddress.SChOOL_ID;
    private String barcode;

    private String userId;
    private ObtainPaySetsInfoResponse response;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("ObtainPaySetsInfoReq");
        postParams.put("barcode", barcode);
        postParams.put("userId", userId);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return ObtainPaySetsInfoResponse.class;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public BaseResponse getResBean() {
        if (response == null) {
            response = new ObtainPaySetsInfoResponse();
        }
        return response;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

}
