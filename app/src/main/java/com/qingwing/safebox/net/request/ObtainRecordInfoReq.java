package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.ObtainRecordInfoResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

public class ObtainRecordInfoReq extends BaseCommReq {
    private String url = ServerAddress.GET_MESSAGE1;
    private String year;
    private String month;
    private int day;
    private String barcode;
    private String startDate = "";
    private String endDate = "";
    private String pageNo;
    private String selectType;
    private String thisDate = "";


    private String userId;
    private ObtainRecordInfoResponse obtainRecordInfoResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        postParams.put("userId", userId);
        postParams.put("barcode", barcode);
        postParams.put("selectType", selectType);
        if (selectType.equals("1")) {
            //查看一段时间的30条记录（分页查看）
            postParams.put("startDate", startDate);
            postParams.put("endDate", endDate);
            postParams.put("pageNo", pageNo);
            setTag("ObtainThirtyRecordInfoReq");
        } else if (selectType.equals("2")) {
            //查看一个时间点之前的15条记录
            setTag("ObtainFifteenRecordInfoReq");
            postParams.put("thisDate", thisDate);
        }
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return ObtainRecordInfoResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (obtainRecordInfoResponse == null) {
            obtainRecordInfoResponse = new ObtainRecordInfoResponse();
        }
        return obtainRecordInfoResponse;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getThisDate() {
        return thisDate;
    }

    public void setThisDate(String thisDate) {
        this.thisDate = thisDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getSelectType() {
        return selectType;
    }

    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}