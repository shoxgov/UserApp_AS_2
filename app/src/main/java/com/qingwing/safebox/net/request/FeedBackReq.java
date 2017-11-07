package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.FeedBackResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;


public class FeedBackReq extends BaseCommReq {
	private String url = ServerAddress.FEEDBACK;
	private String suggestion,userId;
	private FeedBackResponse mFeedBackResponse;
	private Map<String, String> postParams = new HashMap<String, String>();
	
	@Override
	public String generUrl() {
		setTag("FeedBackReq");
		postParams.put("suggestion", suggestion);
		postParams.put("userId", userId);
		setPostParam(postParams);
		return url;
	}

	@Override
	public Class getResClass() {
		return FeedBackResponse.class;
	}

	@Override
	public BaseResponse getResBean() {
		if (mFeedBackResponse==null) {
			mFeedBackResponse=new FeedBackResponse();
		}
		return mFeedBackResponse;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
