package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

public class CheckDepositAmountResponse extends BaseResponse {

	private static final long serialVersionUID = 1L;
	private String message;
	private String status;
	private DataMap dataMap;

	public DataMap getDataMap() {
		return dataMap;
	}

	public void setDataMap(DataMap dataMap) {
		this.dataMap = dataMap;
	}

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

	public class DataMap {
		private int depositAmount;

		public int getDepositAmount() {
			return depositAmount;
		}

		public void setDepositAmount(int depositAmount) {
			this.depositAmount = depositAmount;
		}
	}
}
