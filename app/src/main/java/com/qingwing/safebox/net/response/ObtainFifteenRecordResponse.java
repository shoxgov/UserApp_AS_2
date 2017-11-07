/**  
 * @Title: ObtainRecordInfoResponse.java
 * @date: 2016-8-31 下午3:50:01
 * @Copyright: (c) 2016, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.response;

import com.qingwing.safebox.net.BaseResponse;

import java.util.List;


/**
 * @Class: ObtainRecordInfoResponse
 * @Package: com.qingwing.net.respone
 * @version: V1.0
 */
public class ObtainFifteenRecordResponse extends BaseResponse {
    private String status;
    private DataMap dataMap;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DataMap getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMap dataMap) {
        this.dataMap = dataMap;
    }

    public class DataMap {
        private List<String> useRecordList;

        public List<String> getUseRecordList() {
            return useRecordList;
        }

        public void setUseRecordList(List<String> useRecordList) {
            this.useRecordList = useRecordList;
        }

    }
}
