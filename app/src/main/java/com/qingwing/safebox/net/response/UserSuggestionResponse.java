package com.qingwing.safebox.net.response;

import com.qingwing.safebox.net.BaseResponse;

import java.util.List;


public class UserSuggestionResponse extends BaseResponse {

	private static final long serialVersionUID = 1L;
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
	
	public class DataMap {
		private List<String> suggestions;

		public List<String> getSuggestions() {
			return suggestions;
		}

		public void setSuggestions(List<String> suggestions) {
			this.suggestions = suggestions;
		}
	}
	
	
}
