package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.ObtainIdentifyCodeByUseridResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


/**  
 * @Class: ObtainIdentifyCodeReq  
 * @Package: com.qingwing.net.request
 * @Description: 通过用户ID获取手机验证码
 * @version: V1.0
 */
public class ObtainIdentifyCodeByUseridReq extends BaseCommReq {
    private String url = ServerAddress.GETCODEBYID;
    private String userId;
    private ObtainIdentifyCodeByUseridResponse obtainIdentifyCodeByUseridResponse;
    private Map<String, String> postParams = new HashMap<String, String>();
    @Override
    public String generUrl() {
        setTag("ObtainIdentifyCodeByUseridReq");
        postParams.put("userId", userId);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return ObtainIdentifyCodeByUseridResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (obtainIdentifyCodeByUseridResponse == null) {
            obtainIdentifyCodeByUseridResponse = new ObtainIdentifyCodeByUseridResponse();
        }
        return obtainIdentifyCodeByUseridResponse;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
