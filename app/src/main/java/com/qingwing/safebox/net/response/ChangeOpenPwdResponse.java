/**  
 * @Title: QueryUserBindRespone.java
 * @date: 2016-8-23 下午5:32:35
 * @Copyright: (c) 2016, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

/**
 * @Class: QueryOpenPwdRespone
 * @Package: com.qingwing.net.respone
 * @version: V1.0
 */
public class ChangeOpenPwdResponse extends BaseResponse {
    /**
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
