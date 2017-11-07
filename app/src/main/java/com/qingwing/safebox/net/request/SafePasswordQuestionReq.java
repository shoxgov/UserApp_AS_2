package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.SafePasswordQuestionResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * @Class: SafePasswordQuestionReq
 * @Package: com.qingwing.net.request
 * @Description: 请求后台得到密保问题和答案
 * @version: V1.0
 */
public class SafePasswordQuestionReq extends BaseCommReq {
    private String url = ServerAddress.REQUESTQUESTION;
    private String userId;
    private SafePasswordQuestionResponse safePasswordQuestionResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("SafePasswordQuestionReq");
        postParams.put("userId", userId);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return SafePasswordQuestionResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (safePasswordQuestionResponse == null) {
            safePasswordQuestionResponse = new SafePasswordQuestionResponse();
        }
        return safePasswordQuestionResponse;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
