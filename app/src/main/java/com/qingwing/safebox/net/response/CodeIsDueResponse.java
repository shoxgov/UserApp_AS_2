/**  
 * @Title: BindNewTelephoneResponse.java
 * @date: 2016-8-31 下午2:04:12
 * @Copyright: (c) 2016, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

/**
 * @Class: BindNewTelephoneResponse
 * @version: V1.0
 */
public class CodeIsDueResponse extends BaseResponse {
    private String status;
    private String message;

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

}
