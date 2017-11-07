package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.ObtainPayResultResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * @Package: com.qingwing.net.request
 * @Description: 支付宝支付结果
 * @author: wsy@unibroad.com
 * @version: V1.0
 */
public class ObtainPayResultReq extends BaseCommReq {
    private String url = ServerAddress.PAYRESULT;
    private String resultStr;
    private ObtainPayResultResponse response;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("ObtainPayResultReq");
        postParams.put("resultStr", resultStr);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return ObtainPayResultResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (response == null) {
            response = new ObtainPayResultResponse();
        }
        return response;
    }

    public String getResultStr() {
        return resultStr;
    }

    public void setResultStr(String resultStr) {
        this.resultStr = resultStr;
    }

}
