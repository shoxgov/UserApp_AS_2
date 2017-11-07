package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

public class ObtainWeinResponse extends BaseResponse {
    private DataMap dataMap;
    private String status;
    private String message;

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

    public class WeiXin {
        private String sign;
        private String timeStamp;
        private String partnerId;
        private String appid;
        private String nonceStr;
        private String prepayId;
        private String retmsg;
        private String out_trade_no;

        public String getOut_trade_no() {
            return out_trade_no;
        }

        public void setOut_trade_no(String out_trade_no) {
            this.out_trade_no = out_trade_no;
        }

        public String getRetmsg() {
            return retmsg;
        }

        public void setRetmsg(String retmsg) {
            this.retmsg = retmsg;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }

        public String getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(String partnerId) {
            this.partnerId = partnerId;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getNonceStr() {
            return nonceStr;
        }

        public void setNonceStr(String nonceStr) {
            this.nonceStr = nonceStr;
        }

        public String getPrepayId() {
            return prepayId;
        }

        public void setPrepayId(String prepayId) {
            this.prepayId = prepayId;
        }

        @Override
        public String toString() {
            return "WeiXin [sign=" + sign + ", timeStamp=" + timeStamp
                    + ", partnerId=" + partnerId + ", appid=" + appid
                    + ", nonceStr=" + nonceStr + ", prepayId=" + prepayId
                    + ", retmsg=" + retmsg + ", out_trade_no=" + out_trade_no
                    + "]";
        }

    }

    public class DataMap {
        private WeiXin weixin;

        public WeiXin getWeixin() {
            return weixin;
        }

        public void setWeixin(WeiXin weixin) {
            this.weixin = weixin;
        }
    }
}
