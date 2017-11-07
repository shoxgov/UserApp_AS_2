package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

/**
 * @Class: UserUnbindResponse
 * @Package: com.qingwing.net.respone
 */
public class UserUnbindResponse extends BaseResponse {
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
        private String unbindRecord;

		public String getUnbindRecord() {
			return unbindRecord;
		}

		public void setUnbindRecord(String unbindRecord) {
			this.unbindRecord = unbindRecord;
		}
    }
}
