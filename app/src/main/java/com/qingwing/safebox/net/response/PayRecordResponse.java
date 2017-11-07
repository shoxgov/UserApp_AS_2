package com.qingwing.safebox.net.response;

import com.qingwing.safebox.net.BaseResponse;

import java.util.List;


/**
 */
public class PayRecordResponse extends BaseResponse {
    private String status;
    private String message;
    private DataMap dataMap;

    public DataMap getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMap dataMap) {
        this.dataMap = dataMap;
    }

    public class DataMap {
    	private List<String> paymentList;

		public List<String> getPaymentList() {
			return paymentList;
		}

		public void setPaymentList(List<String> paymentList) {
			this.paymentList = paymentList;
		}

    }
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

}
