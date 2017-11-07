package com.qingwing.safebox.imp;

import android.os.Handler;
import android.text.TextUtils;

import com.android.volley.Request;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.bluetooth.BleObserverConstance;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.OnLineCodeReq;
import com.qingwing.safebox.net.response.OnLineCodeResponse;
import com.qingwing.safebox.observable.ObservableBean;
import com.qingwing.safebox.observable.ObserverManager;
import com.qingwing.safebox.utils.MD5Utils;

public class SingleLoginThread implements NetCallBack {
    private static boolean flag = true;
    private static SingleLoginThread sLongThread;


    public static SingleLoginThread getInstance() {
        if (sLongThread == null) {
            sLongThread = new SingleLoginThread();
        }
        return sLongThread;
    }

    private Handler mHandler = new Handler();

    public void startThread() {
        flag = true;
        query();
    }

    private void query() {
        String onlineCode = UserInfo.onlineCode;
        int userId = UserInfo.userId;
        if (flag && !TextUtils.isEmpty(onlineCode) && userId > 0) {
            OnLineCodeReq lineCodeReq = new OnLineCodeReq();
            lineCodeReq.setNetCallback(this);
            lineCodeReq.setUserId(userId + "");
            lineCodeReq.setOnlineCode(onlineCode);
            lineCodeReq.setRequestType(Request.Method.POST);
            lineCodeReq.addRequest();
        } else {
        }
    }

    public void stopThread() {
        flag = false;
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        OnLineCodeResponse codeResponse = (OnLineCodeResponse) baseRes;
        String endDate = codeResponse.getDataMap().getEndDate();
        int amount = (int) codeResponse.getDataMap().getDepositAmount();
        String endstringtime = MD5Utils.change_start(endDate);
        int useStatus = codeResponse.getDataMap().getUseStatus();
        if (codeResponse.getStatusCode() == 200) {
            if (useStatus == 0) {
                //表示保管箱已经空闲
                UserInfo.UserBindState = false;
                if (amount > 0) {
                    //表示未绑定保管箱，但是有费用
                    UserInfo.endstringtime = endstringtime;
                } else {
                    //表示未绑定保管箱，未开通服务
                    UserInfo.endstringtime = "";
                }
                QWApplication.mPreferences.edit()
                        .putBoolean("UserBindState", false)
                        .putString("endstringtime", endstringtime).commit();
            } else if (useStatus == 1) {
                //表示保管箱可以正常使用
                UserInfo.UserBindState = true;
                UserInfo.isOverDate = false;
                UserInfo.endstringtime = endstringtime;
                QWApplication.mPreferences.edit()
                        .putBoolean("UserBindState", true)
                        .putBoolean("isOverDate", false)
                        .putString("endstringtime", endstringtime).commit();
            } else if (useStatus == 2) {
                //表示保管箱已经欠费了
                UserInfo.isOverDate = true;
                UserInfo.endstringtime = endstringtime;
                QWApplication.mPreferences.edit()
                        .putBoolean("isOverDate", true)
                        .putString("endstringtime", endstringtime).commit();
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    query();
                }
            }, 1000 * 12);
        } else {
            stopThread();
            ObservableBean on = new ObservableBean();
            on.setWhat(BleObserverConstance.LOGIN_SINGLE_STOP_ACTION);
            ObserverManager.getObserver().setMessage(on);
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                query();
            }
        }, 1000 * 6);
    }

}
