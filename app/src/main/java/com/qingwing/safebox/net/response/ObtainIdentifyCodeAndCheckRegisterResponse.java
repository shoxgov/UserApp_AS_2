/**  
 * @Title: ObtainIdentifyCodeAndCheckRegisterResponse.java
 * @date: 2016-8-30 下午2:13:09
 * @Copyright: (c) 2016, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.response;

import com.qingwing.safebox.net.BaseResponse;

/**
 * @Class: ObtainIdentifyCodeAndCheckRegisterResponse
 * @Package: com.qingwing.net.respone
 * @Description: TODO(描述类作用)
 * @author: wsy@unibroad.com
 * @version: V1.0
 */
public class ObtainIdentifyCodeAndCheckRegisterResponse extends BaseResponse {
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
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
