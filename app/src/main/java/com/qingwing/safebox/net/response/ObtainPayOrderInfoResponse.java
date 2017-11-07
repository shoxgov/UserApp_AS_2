/**  
 * @Title: ObtainPayOrderInfoResponse.java
 * @date: 2016-9-1 下午4:30:34
 * @Copyright: (c) 2016, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

/**
 * @Class: ObtainPayOrderInfoResponse
 * @Package: com.qingwing.net.respone
 */
public class ObtainPayOrderInfoResponse extends BaseResponse {
    private DataMap dataMap;

    public DataMap getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMap dataMap) {
        this.dataMap = dataMap;
    }

    public class DataMap {
        private String allStr;

        public String getAllStr() {
            return allStr;
        }

        public void setAllStr(String allStr) {
            this.allStr = allStr;
        }
    }
}
