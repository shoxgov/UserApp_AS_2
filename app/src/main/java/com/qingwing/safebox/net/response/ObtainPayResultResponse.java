/**  
 * @Title: ObtainPayResultResponse.java
 * @date: 2016-9-1 下午4:50:32
 * @Copyright: (c) 2016, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

/**
 * @Class: ObtainPayResultResponse
 * @Package: com.qingwing.net.respone
 */
public class ObtainPayResultResponse extends BaseResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
