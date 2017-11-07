package com.qingwing.safebox.net.response;

import com.qingwing.safebox.net.BaseResponse;

public class OnLineCodeResponse extends BaseResponse {
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
        private String status, endDate;
        private int useStatus;
        private double depositAmount;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public int getUseStatus() {
            return useStatus;
        }

        public void setUseStatus(int useStatus) {
            this.useStatus = useStatus;
        }

        public double getDepositAmount() {
            return depositAmount;
        }

        public void setDepositAmount(double depositAmount) {
            this.depositAmount = depositAmount;
        }
    }
}
