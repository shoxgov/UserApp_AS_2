package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.GetWebTimeResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


public class GetWebTimeReq extends BaseCommReq {
	
	private String url = ServerAddress.USER_GETTIME;
	private GetWebTimeResponse getWebTimeResponse;
	private Map<String, String> postParams = new HashMap<String, String>();
	@Override
	public String generUrl() {
		setTag("GetWebTimeReq");
		setPostParam(postParams);
		return url;
	}

	@Override
	public Class getResClass() {
		return GetWebTimeResponse.class;
	}

	@Override
	public BaseResponse getResBean() {
		if (getWebTimeResponse==null) {
			getWebTimeResponse = new GetWebTimeResponse();
		}
		return getWebTimeResponse;
	}

}
