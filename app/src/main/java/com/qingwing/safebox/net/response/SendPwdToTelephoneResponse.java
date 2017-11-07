/**
 * @Title: SendPwdToTelephoneResponse.java
 * @date: 2016-9-1 涓嬪崍3:19:39
 * @Copyright: (c) 2016, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.response;

import com.qingwing.safebox.net.BaseResponse;

/**
 * @Class: SendPwdToTelephoneResponse
 * @version: V1.0
 */
public class SendPwdToTelephoneResponse extends BaseResponse {
    private String status;
    private String message;
    private DataMap dataMap;

    public DataMap getDataMap() {
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

    public class DataMap {
        private String password, newPassword, systemTime, endDate;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getSystemTime() {
            return systemTime;
        }

        public void setSystemTime(String systemTime) {
            this.systemTime = systemTime;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

    }
}
