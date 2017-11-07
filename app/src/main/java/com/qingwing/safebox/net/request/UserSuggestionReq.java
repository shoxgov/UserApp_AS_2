package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.UserSuggestionResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


public class UserSuggestionReq extends BaseCommReq {
    private String url = ServerAddress.USERSUGESTION;
    private UserSuggestionResponse mSuggestionResponse;
    private String userId;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("UserSuggestionReq");
        postParams.put("userId", userId);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return UserSuggestionResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (mSuggestionResponse == null) {
            mSuggestionResponse = new UserSuggestionResponse();
        }
        return mSuggestionResponse;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
