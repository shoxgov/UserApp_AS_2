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
import com.qingwing.safebox.dialog.LoadingDialog;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.ModifyPwdReq;
import com.qingwing.safebox.net.request.ObtainLoginIdentifyCodeReq;
import com.qingwing.safebox.net.response.IdentifyCodeResponse;
import com.qingwing.safebox.net.response.ModifyPwdResponse;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.ToastTool;
import com.qingwing.safebox.utils.WaitTool;

public class ModifyLoginPasswordActivity extends Activity implements OnClickListener, NetCallBack {
    private EditText pwdEditPwd1, pwdEditPwd2, telphoneEdit;
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
    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_login_password);
        AcitivityCollector.addActivity(this);
        mobile = getIntent().getStringExtra("phone");
        calback = (ImageView) findViewById(R.id.calback);
        verticalCodeEdit = (EditText) findViewById(R.id.tel_verticalCode);
        telphoneEdit = (EditText) findViewById(R.id.modify_loginpwd_telphone);// 输入十一位的手机号
        pwdEditPwd1 = (EditText) findViewById(R.id.modify_loginpwd_pwd1);
        pwdEditPwd2 = (EditText) findViewById(R.id.modify_loginpwd_pwd2);
        Button mButton = (Button) findViewById(R.id.bt_login);
        mButton.setOnClickListener(this);
        vertical_code = (Button) findViewById(R.id.obtain_vertical_code);
        vertical_code.setOnClickListener(this);
        calback.setOnClickListener(this);
        long starttime = QWApplication.mPreferences.getLong("ModifyLoginPwdVertifyCodeStartTime", 0L);
        int spanTime = (int) (System.currentTimeMillis() - starttime);
        int runtime = 60000 - spanTime;
        if (starttime > 0 && spanTime < 60000 && runtime > 1000) {
            codeinga = "000000";
            timer = new TimeCount(runtime, 1000);
            timer.start();
            vertical_code.setEnabled(false);
        } else if (spanTime > 0 && spanTime < 600000) {//10分钟内有效
            codeinga = "000000";
        }
    }

    // 获取验证码同时判断手机号码是否被注册。手机号码必须没有被使用
    private void obtainVerticalCode() {
        codeinga = "";
        vertical_code.setEnabled(false);
        timer = new TimeCount(60000, 1000);
        timer.start();
        QWApplication.mPreferences.edit().putLong("ModifyLoginPwdVertifyCodeStartTime", System.currentTimeMillis()).commit();
        WaitTool.showDialog(this);
        ObtainLoginIdentifyCodeReq req = new ObtainLoginIdentifyCodeReq();
        req.setNetCallback(this);
        req.setRequestType(Request.Method.POST);
        req.setMobile(mobile);
        req.addRequest();
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer.onFinish();
        }
        AcitivityCollector.removeActivity(this);
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        if (CommUtils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.bt_login:
                if (CommUtils.isNetworkAvailable(ModifyLoginPasswordActivity.this)) {
                    //修改登录密码
                    String code = verticalCodeEdit.getText().toString();
                    if (TextUtils.isEmpty(code)) {
                        ToastTool.showShortBigToast(this, "请输入验证码");
                        return;
                    }
                    modifyLoginPassWord(code);
                } else {
                    ToastTool.showShortBigToast(this, "当前无网络连接");
                }
                break;
            case R.id.calback:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                overridePendingTransition(0, R.anim.out);
                finish();
                break;

            case R.id.obtain_vertical_code:
                if (TextUtils.isEmpty(telphoneEdit.getText().toString())) {
                    ToastTool.showShortBigToast(this, "手机号为空");
                    return;
                }
                mobile = telphoneEdit.getText().toString();
                obtainVerticalCode();
                break;
            default:
                break;
        }
    }


    private void modifyLoginPassWord(String code) {
        String pwd1 = pwdEditPwd1.getText().toString().trim();
        String pwd2 = pwdEditPwd2.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastTool.showShortBigToast(this, "请获取验证码");
            return;
        }
        if (TextUtils.isEmpty(pwd1)) {
            ToastTool.showShortBigToast(this, "请输入密码");
            return;
        }
        if (TextUtils.isEmpty(pwd2)) {
            ToastTool.showShortBigToast(this, "请确认密码");
            return;
        }
        if (pwd1.length() < 6 || pwd1.length() > 16) {
            ToastTool.showShortBigToast(this, "请输入6-16位密码");
            return;
        }
        if (!pwd1.equals(pwd2)) {
            ToastTool.showShortBigToast(this, "两次密码不一致，请重新输入");
            return;
        }
        LoadingDialog.showDialog(this, "请稍等...");
        ModifyPwdReq req = new ModifyPwdReq();
        req.setNetCallback(this);
        req.setRequestType(Request.Method.POST);
        req.setMobile(mobile);
        req.setPassword(pwd1);
        req.setCode(code);
        req.addRequest();
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
            QWApplication.mPreferences.edit().putLong("ModifyLoginPwdVertifyCodeTime", System.currentTimeMillis()).commit();
        }
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof ModifyPwdResponse) {//修改登录密码
            LoadingDialog.dismissDialog();
            ModifyPwdResponse mpr = (ModifyPwdResponse) baseRes;
            try {
                int a = mpr.getStatusCode();
                String message = mpr.getMessage();
                if (a == 500) {//失败
                    ToastTool.showShortBigToast(this, message);
                } else {
                    ToastTool.showShortBigToast(this, message);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (baseRes instanceof IdentifyCodeResponse) {
            IdentifyCodeResponse resp = (IdentifyCodeResponse) baseRes;
            WaitTool.dismissDialog();
            try {
                String status = resp.getStatus();
                String message = resp.getMessage();
                if (!TextUtils.isEmpty(status) && status.equals("success")) {
                    codeinga = resp.getDataMap().getCode();
                } else {
                    vertical_code.setText("获取验证码");
                }
                ToastTool.showShortBigToast(this, message);
                vertical_code.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        LoadingDialog.dismissDialog();
        ToastTool.showShortBigToast(this, "无网络连接，请检查您的网络配置");
    }

}
