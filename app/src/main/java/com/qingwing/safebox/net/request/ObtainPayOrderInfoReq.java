package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.ObtainPayOrderInfoResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * @Package: com.qingwing.net.request
 * @Description: 得到支付宝的完整订单信息
 */
public class ObtainPayOrderInfoReq extends BaseCommReq {
    private String url = ServerAddress.PAY;
    private String subject;
    private String body;
    private String price;
    private ObtainPayOrderInfoResponse response;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("ObtainPayOrderInfoReq");
        postParams.put("subject", subject);
        postParams.put("body", body);
        postParams.put("price", price);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return ObtainPayOrderInfoResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (response == null) {
            response = new ObtainPayOrderInfoResponse();
        }
        return response;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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
