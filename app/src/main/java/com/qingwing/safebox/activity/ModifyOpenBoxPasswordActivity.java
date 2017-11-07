package com.qingwing.safebox.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.bluetooth.BLECommandManager;
import com.qingwing.safebox.bluetooth.BleObserverConstance;
import com.qingwing.safebox.bluetooth.BluetoothService;
import com.qingwing.safebox.dialog.LoadingDialog;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.ObtainIdentifyCodeByUseridReq;
import com.qingwing.safebox.net.request.SendPwdToTelephoneReq;
import com.qingwing.safebox.net.response.ObtainIdentifyCodeByUseridResponse;
import com.qingwing.safebox.net.response.SendPwdToTelephoneResponse;
import com.qingwing.safebox.observable.ObservableBean;
import com.qingwing.safebox.observable.ObserverManager;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.MD5Utils;
import com.qingwing.safebox.utils.ToastTool;
import com.qingwing.safebox.utils.WaitTool;

import java.util.Observable;
import java.util.Observer;

public class ModifyOpenBoxPasswordActivity extends Activity implements OnClickListener, Observer, NetCallBack {
    private EditText pwdEdita;
    private EditText pwdEditb;
    private ImageView calback;
    private Button vertical_code;
    /**
     * 手机验证码
     */
    private String codeinga = "";
    private EditText verticalCodeEdit;
    /**
     * 获取验证码计时器
     */
    private TimeCount timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_openbox_password);
        AcitivityCollector.addActivity(this);
        calback = (ImageView) findViewById(R.id.calback);
        verticalCodeEdit = (EditText) findViewById(R.id.tel_verticalCode);
        pwdEdita = (EditText) findViewById(R.id.questiona);
        pwdEditb = (EditText) findViewById(R.id.questionb);
        Button mButton = (Button) findViewById(R.id.bt_login);
        mButton.setOnClickListener(this);
        vertical_code = (Button) findViewById(R.id.obtain_vertical_code);
        vertical_code.setOnClickListener(this);
        calback.setOnClickListener(this);
        ObserverManager.getObserver().addObserver(this);
        long starttime = QWApplication.mPreferences.getLong("ModifyPwdVertifyCodeTime", 0L);
        int spanTime = (int) (System.currentTimeMillis() - starttime);
        int runtime = 60000 - spanTime;
        if (spanTime > 0 && spanTime < 60000 && runtime > 1000) {
            codeinga = "000000";
            timer = new TimeCount(runtime, 1000);
            timer.start();
            vertical_code.setEnabled(false);
        } else if (spanTime > 0 && spanTime < 600000) {//10分钟内有效
            codeinga = "000000";
        }
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer.onFinish();
        }
        ObserverManager.getObserver().deleteObserver(this);
        AcitivityCollector.removeActivity(this);
        super.onDestroy();
    }

    private void modifyOpenPassword(String openPassword) {
        if (!BluetoothService.isConnected) {
            ToastTool.showShortBigToast(this, "蓝牙未连接，请连接后再操作");
            return;
        }
        if (TextUtils.isEmpty(codeinga)) {
            ToastTool.showShortBigToast(this, "请获取验证码");
            return;
        }
        SendPwdToTelephoneReq req = new SendPwdToTelephoneReq();//发送密码到手机上
        req.setNetCallback(this);
        req.setUserId(UserInfo.userId);
        req.setPassword(openPassword);
        req.setCode(codeinga);
        req.setRequestType(Request.Method.POST);
        req.addRequest();
        LoadingDialog.showDialog(this, "正在更新，请稍等...");
    }

    @Override
    public void onClick(View v) {
        if (CommUtils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.bt_login:
                if (CommUtils.isNetworkAvailable(ModifyOpenBoxPasswordActivity.this)) {
                    String code = verticalCodeEdit.getText().toString();
                    if (TextUtils.isEmpty(code)) {
                        ToastTool.showShortBigToast(this, "请输入验证码");
                        return;
                    }
                    //修改开箱密码
                    modifyOpenPassWord();
                } else {
                    ToastTool.showShortBigToast(this, "当前无网络连接");
                }
                break;
            case R.id.calback:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pwdEdita.getWindowToken(), 0);
                finish();
                overridePendingTransition(0, R.anim.out);
                break;

            case R.id.obtain_vertical_code:
                if (TextUtils.isEmpty(UserInfo.mobile)) {
                    ToastTool.showShortBigToast(this, "手机号为空");
                    return;
                }
                WaitTool.showDialog(this);
                ObtainIdentifyCodeByUseridReq byUseridReq = new ObtainIdentifyCodeByUseridReq();
                byUseridReq.setRequestType(Request.Method.POST);
                byUseridReq.setUserId(UserInfo.userId + "");
                byUseridReq.setNetCallback(this);
                byUseridReq.addRequest();
                codeinga = "";
                vertical_code.setEnabled(false);
                timer = new TimeCount(60000, 1000);
                timer.start();
                QWApplication.mPreferences.edit().putLong("ModifyPwdVertifyCodeStartTime", System.currentTimeMillis()).commit();
                break;
            default:
                break;
        }
    }

    private void modifyOpenPassWord() {
        String a;
        String b;
        a = pwdEdita.getText().toString().trim();
        b = pwdEditb.getText().toString().trim();
        if (TextUtils.isEmpty(a)) {
            ToastTool.showShortBigToast(this, "请输入密码");
            return;
        }
        if (TextUtils.isEmpty(b)) {
            ToastTool.showShortBigToast(this, "请确认密码");
            return;
        }
        if (a.length() != 6 || b.length() != 6) {
            ToastTool.showShortBigToast(this, "请输入6位的数字密码");
            pwdEdita.setText("");
            pwdEditb.setText("");
            pwdEdita.setSelected(true);
            return;
        }
        if (!a.matches("^[1-9]\\d{5}$") || !b.matches("^[1-9]\\d{5}$")) {
            ToastTool.showShortBigToast(this, "当前只支持数字密码");
            pwdEdita.setText("");
            pwdEditb.setText("");
            pwdEdita.setSelected(true);
            return;
        }
        if (!a.equals(b)) {
            ToastTool.showShortBigToast(this, "两次密码不一致，请重新输入");
            return;
        }
        modifyOpenPassword(a);
    }


    // 验证码再次获取的倒计时
    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            vertical_code.setText("获取验证码");
            vertical_code.setEnabled(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            vertical_code.setEnabled(false);
            vertical_code.setText(millisUntilFinished / 1000 + "s");
            QWApplication.mPreferences.edit().putLong("ModifyPwdVertifyCodeTime", System.currentTimeMillis()).commit();
        }
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof SendPwdToTelephoneResponse) {
            LoadingDialog.dismissDialog();
            SendPwdToTelephoneResponse sptr = (SendPwdToTelephoneResponse) baseRes;
            String status = sptr.getStatus();
            String message = sptr.getMessage();
            String userpassword = sptr.getDataMap().getNewPassword();
            String startTime = MD5Utils.change_start(sptr.getDataMap().getSystemTime()).substring(0, 6);
            String ServiceStopTime = MD5Utils.change_start(sptr.getDataMap().getEndDate()).substring(0, 6);
            String BlueId = UserInfo.BlueId;
            if (!TextUtils.isEmpty(status) && status.equals("success")) {
                if (!TextUtils.isEmpty(userpassword) && !TextUtils.isEmpty(BlueId)
                        && !TextUtils.isEmpty(ServiceStopTime)) {
                    WaitTool.showDialog(this, "正在更新保管箱，请稍等...", true);
                    BLECommandManager.userBind(startTime, ServiceStopTime, BlueId, userpassword, ModifyOpenBoxPasswordActivity.this, "00");
                } else {
                    ToastTool.showShortBigToast(this, "获取参数失败，请重发");
                }
            } else {
                ToastTool.showShortBigToast(this, message);
            }
        } else if (baseRes instanceof ObtainIdentifyCodeByUseridResponse) {
            WaitTool.dismissDialog();
            ObtainIdentifyCodeByUseridResponse byUseridResponse = (ObtainIdentifyCodeByUseridResponse) baseRes;
            String status = byUseridResponse.getStatus();
            String message = byUseridResponse.getMessage();
            String code = byUseridResponse.getDataMap().getCode();
            if (status.equals("success")) {
                //用户输入验证码
                codeinga = code;
            } else {
                ToastTool.showLongBigToast(this, message);
            }
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        LoadingDialog.dismissDialog();
        ToastTool.showShortBigToast(this, "无网络连接，请检查您的网络配置");
    }

    @Override
    public void update(Observable arg0, Object obj) {
        ObservableBean ob = (ObservableBean) obj;
        switch (ob.getWhat()) {
            case BleObserverConstance.BOX_USER_BIND_RESULT:
                WaitTool.dismissDialog();
                if ((boolean) ob.getObject()) {
                    String newPassword = pwdEditb.getText().toString().trim();
                    LogUtil.d("ModifyOpenBoxPasswordActivity newPassword:" + newPassword);
                    UserInfo.UserOpenBoxPassowrd = newPassword;
                    QWApplication.mPreferences.edit().putString("UserOpenBoxPassowrd", newPassword).commit();
                    ToastTool.showShortBigToast(this, "修改开箱密码成功");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(pwdEdita.getWindowToken(), 0);
                    finish();
                } else {
                    ToastTool.showShortBigToast(this, "修改开箱密码失败,请重试");
                }
                break;
        }
    }
}
