package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

/**
 * @Class: SaveUserBindInfoResponse
 * @Package: com.qingwing.net.respone
 * @version: V1.0
 */
public class SaveUserBindInfoResponse extends BaseResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
