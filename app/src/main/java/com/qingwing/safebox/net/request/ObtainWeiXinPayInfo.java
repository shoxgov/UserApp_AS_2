package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.ObtainWeinResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

public class ObtainWeiXinPayInfo extends BaseCommReq {

    private String url = ServerAddress.PAY_WEIXIN;
    private String body;
    private String price;
    private ObtainWeinResponse mResponse;
    private Map<String, String> postParmas = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("ObtainWeiXinPayInfo");
        postParmas.put("body", body);
        postParmas.put("price", price);
        setPostParam(postParmas);
        return url;
    }

    @Override
    public Class getResClass() {
        return ObtainWeinResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (mResponse == null) {
            mResponse = new ObtainWeinResponse();
        }
        return mResponse;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


}
