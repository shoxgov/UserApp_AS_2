/**
 * @Title: IdentifyCodeResponse.java
 * @date: 2016-8-23 下午3:42:26
 * @Copyright: (c) 2016, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.utils.MD5Utils;

/**
 */
public class NewUserBindResponse extends BaseResponse {
    /*
     * {"data":null,"dataMap":{"code":"704272"},"message":"短信发送成功！","status":"success","statusCode":200
     * }
     */
    private DataMap dataMap;
    private String message;
    private String status;

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

    public String getStartTime() {
        return MD5Utils.change_start(getDataMap().getDate());
    }

    public String getEndTime() {
        return MD5Utils.change_start(getDataMap().getEndDate());
    }

    public class DataMap {
        private String date;
        private String endDate;
        private String bindRecord;

        public String getBindRecord() {
            return bindRecord;
        }

        public void setBindRecord(String bindRecord) {
            this.bindRecord = bindRecord;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }
}
