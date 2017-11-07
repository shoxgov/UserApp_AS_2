package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.ApkUpdateRespone;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


public class ApkUpdateReq extends BaseCommReq {
	private String url = ServerAddress.APK_UPDATE;
	private String type ;
	private ApkUpdateRespone mApkUpdateRespone;
	private Map<String, String> postParams = new HashMap<String, String>();

	@Override
	public String generUrl() {
		setTag("ApkUpdateReq");
		postParams.put("type", type);
		setPostParam(postParams);
		return url;
	}

	@Override
	public Class getResClass() {
		return ApkUpdateRespone.class;
	}

	@Override
	public BaseResponse getResBean() {
		if (mApkUpdateRespone==null) {
			mApkUpdateRespone = new ApkUpdateRespone();
		}
		return mApkUpdateRespone;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
