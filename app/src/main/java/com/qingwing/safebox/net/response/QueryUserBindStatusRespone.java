package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.utils.MD5Utils;

public class QueryUserBindStatusRespone extends BaseResponse {
    private static final long serialVersionUID = 1L;
    private DataMap dataMap;
    private String status;
    private String message;

    private DataMap getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMap dataMap) {
        this.dataMap = dataMap;
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

    public String getStartTime() {
        return MD5Utils.change_start(getDataMap().getDate());
    }

    public String getEndTime() {
        return MD5Utils.change_start(getDataMap().getEndDate());
    }

    public class DataMap {
        private String date;
        private String endDate;

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
