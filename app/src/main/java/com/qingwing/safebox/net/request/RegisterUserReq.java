/**  
 * @Title: AddFriendReq.java
 * @date: 2015-3-25 上午10:02:05
 * @Copyright: (c) 2015, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.RegisterUserResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * @Package: com.qingwing.net.request
 * @Description: /个人注册
 * @author: wsy@unibroad.com
 * @version: V1.0
 */
public class RegisterUserReq extends BaseCommReq {
    private String url = ServerAddress.REGISTER;
    private String mobile;
    private String password;
    private String code,grade;
    private RegisterUserResponse registerUserResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("RegisterUserReq");
        postParams.put("mobile", mobile);
        postParams.put("password", password);
        postParams.put("code", code);
        postParams.put("grade", grade);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return RegisterUserResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (registerUserResponse == null) {
            registerUserResponse = new RegisterUserResponse();
        }
        return registerUserResponse;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}
	
}
