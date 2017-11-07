package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.RechargeByCardNumberResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * @Package: com.qingwing.net.request
 */
public class RechargeByCardNumberReq extends BaseCommReq {
    private String url = ServerAddress.PAY_CARD;
    private int userId;
    private String barcode;
    private String cardNo;
    private RechargeByCardNumberResponse response;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("RechargeByCardNumberReq");
        postParams.put("userId", userId + "");
//        postParams.put("barcode", barcode);
        postParams.put("cardNo", cardNo);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return RechargeByCardNumberResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (response == null) {
            response = new RechargeByCardNumberResponse();
        }
        return response;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

}
