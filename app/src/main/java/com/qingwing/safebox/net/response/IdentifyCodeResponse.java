/**
 * @Title: IdentifyCodeResponse.java
 * @date: 2016-8-23 下午3:42:26
 * @Copyright: (c) 2016, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

/**
 * @Class: IdentifyCodeResponse
 * @author: wsy@unibroad.com
 * @version: V1.0
 */
public class IdentifyCodeResponse extends BaseResponse {
    /*
     * {"data":null,"dataMap":{"code":"704272"},"message":"短信发送成功！","status":"success","statusCode":200
     * }
     */
    private DataMap dataMap;
    private String message;
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
