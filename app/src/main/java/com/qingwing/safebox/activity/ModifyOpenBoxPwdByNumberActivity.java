package com.qingwing.safebox.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.bluetooth.BLECommandManager;
import com.qingwing.safebox.bluetooth.BleObserverConstance;
import com.qingwing.safebox.bluetooth.BluetoothService;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.ChangeOpenPwdReq;
import com.qingwing.safebox.net.response.ChangeOpenPwdResponse;
import com.qingwing.safebox.observable.ObservableBean;
import com.qingwing.safebox.observable.ObserverManager;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.ToastTool;
import com.qingwing.safebox.utils.WaitTool;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by wangshengyin on 2017-06-08.
 * email:shoxgov@126.com
 */

public class ModifyOpenBoxPwdByNumberActivity extends Activity implements View.OnClickListener, Observer, NetCallBack {
    private String oldPasswordString;
    private String newPasswordString;
    private EditText oldPassword;
    private EditText newPassword;
    private EditText querenPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_openbox_pwd_bynumber);
        ObserverManager.getObserver().addObserver(this);
        AcitivityCollector.addActivity(this);
        initViews();
    }

    @Override
    protected void onDestroy() {
        ObserverManager.getObserver().deleteObserver(this);
        AcitivityCollector.removeActivity(this);
        super.onDestroy();
    }

    private void initViews() {
        oldPassword = (EditText) findViewById(R.id.old_password);
        newPassword = (EditText) findViewById(R.id.new_password);
        querenPassword = (EditText) findViewById(R.id.queren_password);

        findViewById(R.id.calback).setOnClickListener(this);
        findViewById(R.id.ok).setOnClickListener(this);
        findViewById(R.id.forget_openpwd).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (CommUtils.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.ok:
                if (!BluetoothService.isConnected) {
                    ToastTool.showShortBigToast(this, "当前设备未连接");
                    return;
                }
                // 如果没联网则要求先联网后才能修改密码
                if (!CommUtils.isNetworkAvailable(this)) {
                    ToastTool.showShortBigToast(this, "网络异常，请检查您的网络");
                    return;
                }
                oldPasswordString = oldPassword.getText().toString().trim();
                newPasswordString = newPassword.getText().toString().trim();
                String QuerenPassWordString = querenPassword.getText().toString().trim();
                LogUtil.d("在UserAlterPassword旧密码:" + UserInfo.UserOpenBoxPassowrd);
                if (!TextUtils.isEmpty(oldPasswordString) && oldPasswordString.equals(UserInfo.UserOpenBoxPassowrd)) {
                    if (newPasswordString.length() == 6 && newPasswordString.equals(QuerenPassWordString)) {
                        if (BluetoothService.isConnected) {
                            WaitTool.showDialog(this, "正在修改密码");
                            BLECommandManager.UserSetPassword(this, UserInfo.BlueId, oldPasswordString, newPasswordString);
                        } else {
                            ToastTool.showShortBigToast(this, "当前设备未连接");
                        }
                    } else {
                        ToastTool.showShortBigToast(this, "新密码输入有误");
                        newPassword.setText("");
                        querenPassword.setText("");
                        oldPassword.setText("");
                    }
                } else {
                    ToastTool.showShortBigToast(this, "原密码输入有误");
                    newPassword.setText("");
                    querenPassword.setText("");
                    oldPassword.setText("");
                }
                break;
            case R.id.forget_openpwd:
                Intent forgetOpenPwd = new Intent();
                forgetOpenPwd.setClass(this, ModifyOpenBoxPasswordActivity.class);
                forgetOpenPwd.putExtra("setOpenPassword", true);
                startActivity(forgetOpenPwd);
                break;
            case R.id.calback:
                finish();
                break;
        }
    }

    private void changeOpenBoxPwd() {
        System.out.println("发送修改开箱密码请求到后台");
        ChangeOpenPwdReq req = new ChangeOpenPwdReq();
        req.setNetCallback(this);
        req.setUserId(UserInfo.userId);
        req.setPassword(newPasswordString);
        req.setRequestType(Request.Method.POST);
        req.addRequest();
    }

    @Override
    public void update(Observable observable, Object o) {
        ObservableBean ob = (ObservableBean) o;
        switch (ob.getWhat()) {
            case BleObserverConstance.BOX_USER_MODIFY_PASSWORD_RESULT:
                if ((boolean) ob.getObject()) {//修改数字开箱密码成功
                    WaitTool.showDialog(this,"正在同步到后台");
                    changeOpenBoxPwd();
                } else {
                    WaitTool.dismissDialog();
                    ToastTool.showShortBigToast(this, "修改开箱密码失败");
                }
                break;
        }
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof ChangeOpenPwdResponse) {
            ChangeOpenPwdResponse copr = (ChangeOpenPwdResponse) baseRes;
            WaitTool.dismissDialog();
            String status = copr.getStatus();
            ToastTool.showShortBigToast(this, copr.getMessage());
            if (status.equals("success")) {
                // 关闭对话框
                UserInfo.UserOpenBoxPassowrd = newPasswordString;
                QWApplication.mPreferences.edit().putString("UserOpenBoxPassowrd", newPasswordString)
                        .commit();
                finish();
            } else {
                //密码修改失败，重新改回来
                LogUtil.d("修改密码失败,重新改回来");
                BLECommandManager.UserSetPassword(this,
                        UserInfo.BlueId, newPasswordString, oldPasswordString);
            }
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {

    }
}
