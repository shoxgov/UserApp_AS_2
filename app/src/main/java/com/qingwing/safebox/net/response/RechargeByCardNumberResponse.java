/**  
 * @Title: RechargeByCardNumberResponse.java
 * @date: 2016-9-1 下午5:19:00
 * @Copyright: (c) 2016, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.response;

import com.qingwing.safebox.net.BaseResponse;

/**
 * @Class: RechargeByCardNumberResponse
 * @Package: com.qingwing.net.respone
 */
public class RechargeByCardNumberResponse extends BaseResponse {
    private String message;
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private DataMap dataMap;
    public DataMap getDataMap() {
		return dataMap;
	}

	public void setDataMap(DataMap dataMap) {
		this.dataMap = dataMap;
	}


	public class DataMap {
        private String endDate;
        public String getEndDate() {
            return endDate;
        }
        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }
}
