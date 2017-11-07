package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

public class GetWebTimeResponse extends BaseResponse {

	private String message;
	private String status;
	private DataMap dataMap;
	
	
	
	public String getMessage() {
		return message;
	}



	public void setMessage(String message) {
		this.message = message;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public DataMap getDataMap() {
		return dataMap;
	}



	public void setDataMap(DataMap dataMap) {
		this.dataMap = dataMap;
	}


	public class DataMap{
		private String severTime;

		public String getSeverTime() {
			return severTime;
		}

		public void setSeverTime(String severTime) {
			this.severTime = severTime;
		}
	}
}
