package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

/**
 * @Class: QueryUserBindRespone
 * @Package: com.qingwing.net.respone
 */
public class QueryIsCanUnbindRespone extends BaseResponse {
    private static final long serialVersionUID = 1L;
    private String status;
    private String message;
    private DataMap dataMap;

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

    public DataMap getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMap dataMap) {
        this.dataMap = dataMap;
    }

    public class DataMap {
        private String isOverDate;//1为超过到期时间，0为服务时间内

        public String getIsOverDate() {
            return isOverDate;
        }

        public void setIsOverDate(String isOverDate) {
            this.isOverDate = isOverDate;
        }
    }
}
