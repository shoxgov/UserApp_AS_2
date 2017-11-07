/**
 * @Title: RegisterUserResponse.java
 * @date: 2016-8-30 涓嬪崍2:19:43
 * @Copyright: (c) 2016, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

/**
 * @Class: RegisterUserResponse
 * @Package: com.qingwing.net.respone
 * @author: wsy@unibroad.com
 * @version: V1.0
 */
public class RegisterUserResponse extends BaseResponse {

    /**
     */
    private static final long serialVersionUID = 1L;
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
        private int userId;
        private String ID;
        private String requestCode;
        private String pass;

        public String getRequestCode() {
            return requestCode;
        }

        public void setRequestCode(String requestCode) {
            this.requestCode = requestCode;
        }

        public String getPass() {
            return pass;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getID() {
            return ID;
        }

        public void setID(String iD) {
            ID = iD;
        }


    }
}
