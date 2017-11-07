package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.RechargeResultResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


/**
 * @Package: com.qingwing.net.request
 * @Description: 未绑定则调用缴费到用户的接口
 * @author: wsy@unibroad.com
 * @version: V1.0
 */
public class RechargeByUnbindedBoxReq extends BaseCommReq {
    private String url = ServerAddress.PAY_BEFOREBIND;
    private int userId;
    private int payType;// 支付宝支付1 ，充值卡2
    private String packageId;
    private RechargeResultResponse response;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("RechargeByUnbindedBoxReq");
        postParams.put("userId", userId + "");
        postParams.put("payType", payType + "");
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

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

}
