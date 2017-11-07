package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.JudgeWeiXinPayResult;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

public class WeiXinPayResult extends BaseCommReq {

	private String url = ServerAddress.PAY_WEIXIN_RESULT;
	private String subject;

	private JudgeWeiXinPayResult mJudgeWeiXinPayResult;
	private Map<String, String> postParams = new HashMap<String, String>();

	@Override
	public String generUrl() {
		setTag("WeiXinPayResult");
		postParams.put("subject", subject);
		setPostParam(postParams);
		return url;
	}

	@Override
	public Class getResClass() {
		return JudgeWeiXinPayResult.class;
	}

	@Override
	public BaseResponse getResBean() {
		if (mJudgeWeiXinPayResult!=null) {
			mJudgeWeiXinPayResult=new JudgeWeiXinPayResult();
		}
		return mJudgeWeiXinPayResult;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
