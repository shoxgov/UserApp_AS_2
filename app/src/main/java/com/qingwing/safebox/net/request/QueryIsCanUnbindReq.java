package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.QueryIsCanUnbindRespone;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


/**
 * @Class: QueryIsCanUnbindReq
 * @Package: com.qingwing.net.request
 * @Description: 是否可以解除绑定
 */
public class QueryIsCanUnbindReq extends BaseCommReq {
    private String url = ServerAddress.USER_ISCAN_UNBIND;
    private String userId;
    private String barcode;
    private QueryIsCanUnbindRespone queryIsCanUnbindRespone;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("QueryIsCanUnbindReq");
        postParams.put("userId", userId);
        postParams.put("barcode", barcode);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return QueryIsCanUnbindRespone.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (queryIsCanUnbindRespone == null) {
            queryIsCanUnbindRespone = new QueryIsCanUnbindRespone();
        }
        return queryIsCanUnbindRespone;
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
