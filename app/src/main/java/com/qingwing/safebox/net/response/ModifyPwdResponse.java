/**  
 * @Title: ModifyPwdResponse.java
 * @date: 2016-8-30 下午1:57:58
 * @Copyright: (c) 2016, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

/**
 * @Class: ModifyPwdResponse
 * @Package: com.qingwing.net.request
 * @version: V1.0
 */
public class ModifyPwdResponse extends BaseResponse {

    /**
     * serialVersionUID: TODO(描述变量)
     */
    private static final long serialVersionUID = 1L;
    private String message;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    
}
