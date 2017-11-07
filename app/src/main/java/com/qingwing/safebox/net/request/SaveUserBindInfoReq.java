package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.SaveUserBindInfoResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


/**
 * @Package: com.qingwing.net.request
 * @Description: 保存用户填写的身份证号码
 * @version: V1.0
 */
public class SaveUserBindInfoReq extends BaseCommReq {
    private String url = ServerAddress.SAVECARDID;
    private int userId;
    private String cardId, name;
    private SaveUserBindInfoResponse saveUserBindInfoResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("SaveUserBindInfoReq");
        postParams.put("userId", userId + "");
        postParams.put("cardId", cardId);
        postParams.put("name", name);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return SaveUserBindInfoResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (saveUserBindInfoResponse == null) {
            saveUserBindInfoResponse = new SaveUserBindInfoResponse();
        }
        return saveUserBindInfoResponse;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
