/**  
 * @Title: SavePasswordProtectResponse.java
 * @date: 2016-8-29 上午9:18:05
 * @Copyright: (c) 2016, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

/**
 * @Class: SavePasswordProtectResponse
 * @Package: com.qingwing.net.respone
 * @version: V1.0
 */
public class SavePasswordProtectResponse extends BaseResponse {

    /**
     * serialVersionUID: TODO(描述变量)
     */
    private static final long serialVersionUID = 1L;
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
