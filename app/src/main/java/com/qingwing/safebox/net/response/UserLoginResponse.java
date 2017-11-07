/**
 * @Title: UserLoginResponse.java
 * @date: 2016-8-31 涓嬪崍2:26:43
 * @Copyright: (c) 2016, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.response;

import com.qingwing.safebox.net.BaseResponse;

/**
 * @Class: UserLoginResponse
 * @Package: com.qingwing.net.respone
 * @author: wsy@unibroad.com
 * @version: V1.0
 */
public class UserLoginResponse extends BaseResponse {
    private DataMap dataMap;
    private String message;

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

    public class DataMap {
        private String mobile;
        private int userId;// 用户ID,登录时实际id
        private String ID;// 假用户id，等同于昵称，显示在app头像右侧
        private String userStatus;// bind为已经绑定保管箱，unbind反之
        private String blueId;// 保管箱二维码，unbind为未绑定保管箱
        private String openPassword;//开箱密码，unbind为未绑定
        private String pass;
        private String onlineCode;//心跳码
        private String isPayment;//1为到期时间大于当前时间，0为当前时间大于到期时间
        private String isOverDate;//true为用户已欠费，false为服务时间内
        private String startDate;// 绑定时间，unbind为未绑定
        private String endDate;// 到期时间，未绑定则返回时间等同于注册时间
        private String requestCode;//邀请码

        public String getIsOverDate() {
            return isOverDate;
        }

        public void setIsOverDate(String isOverDate) {
            this.isOverDate = isOverDate;
        }

        public String getIsPayment() {
            return isPayment;
        }

        public void setIsPayment(String isPayment) {
            this.isPayment = isPayment;
        }

        public String getOnlineCode() {
            return onlineCode;
        }

        public void setOnlineCode(String onlineCode) {
            this.onlineCode = onlineCode;
        }

        public String getPass() {
            return pass;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }

        public String getRequestCode() {
            return requestCode;
        }

        public void setRequestCode(String requestCode) {
            this.requestCode = requestCode;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
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

        public String getUserStatus() {
            return userStatus;
        }

        public void setUserStatus(String userStatus) {
            this.userStatus = userStatus;
        }

        public String getBlueId() {
            return blueId;
        }

        public void setBlueId(String blueId) {
            this.blueId = blueId;
        }

        public String getOpenPassword() {
            return openPassword;
        }

        public void setOpenPassword(String openPassword) {
            this.openPassword = openPassword;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        @Override
        public String toString() {
            return "DataMap [mobile=" + mobile + ", userId=" + userId + ", ID=" + ID + ", userStatus=" + userStatus
                    + ", blueId=" + blueId + ", openPassword=" + openPassword + ", startDate=" + startDate
                    + ", endDate=" + endDate + ", requestCode=" + requestCode + "]";
        }

    }
}