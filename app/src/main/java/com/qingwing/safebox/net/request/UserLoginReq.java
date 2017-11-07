package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.UserLoginResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


/**  
 * @Package: com.qingwing.net.request
 * @Description: 用户登录
 * @author: wsy@unibroad.com
 * @version: V1.0   
 */
public class UserLoginReq extends BaseCommReq {
	private String url = ServerAddress.USER_LOGIN_URL;
	private String password;
	private String mobile;
	private String mobType;
	private String mobModel;
	public String getMobType() {
		return mobType;
	}

	public void setMobType(String mobType) {
		this.mobType = mobType;
	}


	public String getMobModel() {
		return mobModel;
	}

	public void setMobModel(String mobModel) {
		this.mobModel = mobModel;
	}


	private UserLoginResponse userLoginResponse;
	private Map<String, String> postParams = new HashMap<String, String>();
	
	@Override
	public String generUrl() {
		setTag("UserLoginReq");
		postParams.put("password", password);
		postParams.put("mobile", mobile);
		postParams.put("mobType", mobType);
		postParams.put("mobModel", mobModel);
		setPostParam(postParams);
		return url;
	}

	@Override
	public Class getResClass() {
		return UserLoginResponse.class;
	}

	@Override
	public BaseResponse getResBean() {
		if (userLoginResponse==null) {
		    userLoginResponse = new UserLoginResponse();
		}
		return userLoginResponse;
	}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
