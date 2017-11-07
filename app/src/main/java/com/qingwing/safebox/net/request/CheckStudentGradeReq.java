package com.qingwing.safebox.net.request;


import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.CheckStudentGradeResponse;
import com.qingwing.safebox.network.ServerAddress;

public class CheckStudentGradeReq extends BaseCommReq {
	private String url = ServerAddress.GRADE_YEAR;
	private CheckStudentGradeResponse mCheckStudentNumberResponse;
//	private Map<String, String> postParams = new HashMap<String, String>();
	@Override
	public String generUrl() {
		setTag("CheckStudentGradeReq");
//		postParams.put("userId", userId+"");
//		postParams.put("studentNo", studentNo);
//		setPostParam(postParams);
		return url;
	}

	@Override
	public Class getResClass() {
		return CheckStudentGradeResponse.class;
	}

	@Override
	public BaseResponse getResBean() {
		if (mCheckStudentNumberResponse==null) {
			mCheckStudentNumberResponse = new CheckStudentGradeResponse();
		}
		return mCheckStudentNumberResponse;
	}
}
