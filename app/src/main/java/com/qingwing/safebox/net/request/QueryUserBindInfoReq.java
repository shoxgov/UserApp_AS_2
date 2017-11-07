package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.QueryUserBindInfoResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * @Package: com.qingwing.net.request
 * @Description: //得到用户的身份证号码
 * @author: wsy@unibroad.com
 * @version: V1.0
 */
public class QueryUserBindInfoReq extends BaseCommReq {
    private String url = ServerAddress.USERCARDID;
    private int userId;
    private QueryUserBindInfoResponse queryUserBindInfoResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("QueryUserBindInfoReq");
        postParams.put("userId", userId + "");
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return QueryUserBindInfoResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (queryUserBindInfoResponse == null) {
            queryUserBindInfoResponse = new QueryUserBindInfoResponse();
        }
        return queryUserBindInfoResponse;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
