package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

public class JudgeWeiXinPayResult extends BaseResponse {
    private static final long serialVersionUID = 1L;
    private DataMap dataMap;

    public DataMap getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMap dataMap) {
        this.dataMap = dataMap;
    }

    public class DataMap {
        private WeiXin weixin;

        public WeiXin getWeiXin() {
            return weixin;
        }

        public void setWeiXin(WeiXin weiXin) {
            weixin = weiXin;
        }
    }

    public class WeiXin {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
