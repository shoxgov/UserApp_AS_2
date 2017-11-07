package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.RechargeResultResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * @Package: com.qingwing.net.request
 * @Description: 已经绑定了则调用保管箱续费的接口
 * @author: wsy@unibroad.com
 * @version: V1.0
 */
public class RechargeByBindedBoxReq extends BaseCommReq {
    private String url = ServerAddress.PAY_AFTERBIND;
    private int userId;
    private int payType;// 1为支付宝，2为充值卡，3为微信
    private String barcode;
    private String packageId;
    private RechargeResultResponse response;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("RechargeByBindedBoxReq");
        postParams.put("userId", userId + "");
        postParams.put("payType", payType + "");
        postParams.put("barcode", barcode);
        postParams.put("packageId", packageId);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return RechargeResultResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (response == null) {
            response = new RechargeResultResponse();
        }
        return response;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

}
