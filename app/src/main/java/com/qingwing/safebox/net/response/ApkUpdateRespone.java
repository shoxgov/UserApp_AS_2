package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

public class ApkUpdateRespone extends BaseResponse {
	private String status;
	private String message;
	private DataMap dataMap;
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public  DataMap getDataMap() {
		return dataMap;
	}

	public void setDataMap(DataMap dataMap) {
		this.dataMap = dataMap;
	}

	public class DataMap {
		private String bei;
		private String versions;
		private int isforce;
		private int code;
		private int type;
		private String url;
		public String getBei() {
			return bei;
		}
		public void setBei(String bei) {
			this.bei = bei;
		}
		public String getVersions() {
			return versions;
		}
		public void setVersions(String versions) {
			this.versions = versions;
		}
		public int getIsforce() {
			return isforce;
		}
		public void setIsforce(int isforce) {
			this.isforce = isforce;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
	}
}
