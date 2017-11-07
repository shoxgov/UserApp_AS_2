package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.CodeIsDueResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


/**
 * @Class: BindNewTelephoneReq
 */
public class CodeIsDueReq extends BaseCommReq {
    private String url = ServerAddress.CODEISDUE;
    private String mobile;
    private CodeIsDueResponse codeIsDueResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("CodeIsDueReq");
        postParams.put("mobile", mobile);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return CodeIsDueResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (codeIsDueResponse == null) {
            codeIsDueResponse = new CodeIsDueResponse();
        }
        return codeIsDueResponse;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
