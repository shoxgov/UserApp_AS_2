/**  
 * @Title: BindNewTelephoneResponse.java
 * @date: 2016-8-31 下午2:04:12
 * @Copyright: (c) 2016, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.response;

import com.qingwing.safebox.net.BaseResponse;

import java.util.List;


/**
 * @Package: com.qingwing.net.respone
 */
public class CheckStudentGradeResponse extends BaseResponse {
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
    
    public DataMap getDataMap() {
		return dataMap;
	}

	public void setDataMap(DataMap dataMap) {
		this.dataMap = dataMap;
	}

	public class DataMap {
    	private List<String> dateList;

		public List<String> getDateList() {
			return dateList;
		}

		public void setDateList(List<String> dateList) {
			this.dateList = dateList;
		}
    }
}
