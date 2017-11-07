/**
 * @Title: AddFriendReq.java
 * @date: 2015-3-25 上午10:02:05
 * @Copyright: (c) 2015, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.QueryUserBindStatusRespone;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * @Class: QueryUserBindStatusReq
 * @Package: com.qingwing.net.request
 * @Description: 请求服务器是否可以绑定
 */
public class QueryUserBindStatusReq extends BaseCommReq {
    private String url = ServerAddress.USER_BINDREQUEST;
    private String userId;
    private String barcode;
    private QueryUserBindStatusRespone queryUserBindRespone;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("QueryUserBindStatusReq");
        postParams.put("userId", userId);
        postParams.put("barcode", barcode);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return QueryUserBindStatusRespone.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (queryUserBindRespone == null) {
            queryUserBindRespone = new QueryUserBindStatusRespone();
        }
        return queryUserBindRespone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

}
