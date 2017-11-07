package com.qingwing.safebox.net.response;

import com.qingwing.safebox.net.BaseResponse;

import java.util.List;


/**
 * @Class: ObtainPaySetsInfoResponse
 * @Package: com.qingwing.net.respone
 * @author: wsy@unibroad.com
 * @version: V1.0
 */
public class ObtainPaySetsInfoResponse extends BaseResponse {
    private String status;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private DataMap dataMap;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DataMap getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMap dataMap) {
        this.dataMap = dataMap;
    }

    public class DataMap {
        private List<PackageSetInfo> packageList;
        private boolean isDeposit;//是否缴纳押金

        public List<PackageSetInfo> getPackageList() {
            return packageList;
        }

        public void setPackageList(List<PackageSetInfo> packageList) {
            this.packageList = packageList;
        }

        public boolean isDeposit() {
            return isDeposit;
        }

        public void setDeposit(boolean isDeposit) {
            this.isDeposit = isDeposit;
        }
    }

    public class PackageSetInfo {
        private float amount;//套餐的原价
        private float reallyAmount;//套餐优化之后的价格
        private int id;
        private String packageDesc;//套餐的描述 ，如一年
        private String packageName;//套餐的名字，如月度套餐
        private float depositAmount;//需要缴的押金
        private String packageNo;
        private int termCount;
        private int termType;
        private String typeDesc;


        public float getDepositAmount() {
            return depositAmount;
        }

        public void setDepositAmount(float depositAmount) {
            this.depositAmount = depositAmount;
        }

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }

        public float getReallyAmount() {
            return reallyAmount;
        }

        public void setReallyAmount(float reallyAmount) {
            this.reallyAmount = reallyAmount;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPackageDesc() {
            return packageDesc;
        }

        public void setPackageDesc(String packageDesc) {
            this.packageDesc = packageDesc;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getPackageNo() {
            return packageNo;
        }

        public void setPackageNo(String packageNo) {
            this.packageNo = packageNo;
        }

        public int getTermCount() {
            return termCount;
        }

        public void setTermCount(int termCount) {
            this.termCount = termCount;
        }

        public int getTermType() {
            return termType;
        }

        public void setTermType(int termType) {
            this.termType = termType;
        }

        public String getTypeDesc() {
            return typeDesc;
        }

        public void setTypeDesc(String typeDesc) {
            this.typeDesc = typeDesc;
        }

    }

}
