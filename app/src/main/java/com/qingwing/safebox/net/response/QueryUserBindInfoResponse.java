package com.qingwing.safebox.net.response;


import com.qingwing.safebox.net.BaseResponse;

/**
 * @Class: QueryUserBindInfoResponse
 * @Package: com.qingwing.net.respone
 * @version: V1.0
 */
public class QueryUserBindInfoResponse extends BaseResponse {
    private DataMap dataMap;

    public DataMap getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMap dataMap) {
        this.dataMap = dataMap;
    }

    public class DataMap {
        private String cardId;

        public String getCardId() {
            return cardId;
        }

        public void setCardId(String cardId) {
            this.cardId = cardId;
        }
    }
}
