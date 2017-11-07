package com.qingwing.safebox.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.BindNewTelephoneReq;
import com.qingwing.safebox.net.request.ObtainBindphoneIdentifyCodeReq;
import com.qingwing.safebox.net.response.BindNewTelephoneResponse;
import com.qingwing.safebox.net.response.IdentifyCodeResponse;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.MD5Utils;
import com.qingwing.safebox.utils.ToastTool;
import com.qingwing.safebox.utils.WaitTool;

public class BindPhoneActivity extends Activity implements OnClickListener, NetCallBack {
    private EditText et_password;
    private EditText bindPhone_et;
    private TextView et_oldphone;
    private TextView et_newphone;
    private TimeCount time = null;
    private String codeinga;
    private Button btn_ok, verticalcode;
    private ImageView calback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        AcitivityCollector.addActivity(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (time != null) {
            time.cancel();
            time.onFinish();
        }
        AcitivityCollector.removeActivity(this);
    }

    private void initView() {
        et_password = (EditText) findViewById(R.id.et_password);
        et_oldphone = (TextView) findViewById(R.id.et_oldphone);
        bindPhone_et = (EditText) findViewById(R.id.BindPhone_et);
        verticalcode = (Button) findViewById(R.id.obtain_verticalcode);
        et_newphone = (TextView) findViewById(R.id.et_newphone);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        calback = (ImageView) findViewById(R.id.calback);
        String phone = UserInfo.mobile;
        if (!phone.isEmpty()) {
            StringBuffer sb = new StringBuffer(phone);
            sb.replace(3, 7, "****");
            et_oldphone.setText("绑定手机号:" + sb);
        }
        long starttime = QWApplication.mPreferences.getLong("BindVertifyCodeStartTime", 0L);
        int spanTime = (int) (System.currentTimeMillis() - starttime);
        int runtime = 60000 - spanTime;
        if (spanTime > 0 && spanTime < 60000 && runtime > 1000) {
            codeinga = QWApplication.mPreferences.getString("BindPhoneVertifyCode", "000000");
            time = new TimeCount(runtime, 1000);
            time.start();
            verticalcode.setEnabled(false);
        } else if (spanTime > 0 && spanTime < 600000) {//10分钟内有效
            codeinga = "000000";
            time = new TimeCount(60000, 1000);
        } else {
            time = new TimeCount(60000, 1000);
        }
        calback.setOnClickListener(this);
        verticalcode.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (CommUtils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_ok:
                if (CommUtils.isNetworkAvailable(this)) {
                    // 验证码是否正确
                    if (TextUtils.isEmpty(et_password.getText().toString())) {
                        ToastTool.showShortBigToast(this, "请输入您的登录密码");
                        return;
                    }
                    if (!et_password.getText().toString().trim().equals(UserInfo.userLoginPassword)) {
                        ToastTool.showShortBigToast(this, "您的登录密码输入有误");
                        return;
                    }
                    // 判断两次手机输入是否正确
                    if (TextUtils.isEmpty(et_newphone.getText().toString())) {
                        ToastTool.showShortBigToast(this, "请输入新手机号码");
                        return;
                    }
                    if ((et_newphone.getText().toString().length() != 11)) {
                        ToastTool.showShortBigToast(this, "手机号码错误");
                        return;
                    }
                    if (TextUtils.isEmpty(bindPhone_et.getText().toString().trim())) {
                        ToastTool.showShortBigToast(this, "您还未填写验证码");
                        return;
                    }
                    if (TextUtils.isEmpty(codeinga)) {
                        ToastTool.showShortBigToast(this, "请先获取您的验证码");
                        return;
                    }
//                    if (!codeinga.equals(bindPhone_et.getText().toString())) {
//                        ToastTool.showShortBigToast(this, "验证码不正确");
//                        return;
//                    }
                    bindNewPhone(bindPhone_et.getText().toString());
                } else {
                    ToastTool.showShortBigToast(this, "网络异常，请检查您的网络");
                }
                break;
            case R.id.calback:
                // 返回键
                finish();
                break;
            case R.id.obtain_verticalcode:
                sendCode();
            default:
                break;
        }
    }


    private void sendCode() {
        String phoneNumber = et_newphone.getText().toString().trim();
        if (!MD5Utils.matchPhone(phoneNumber)) {
            ToastTool.showShortBigToast(this, "手机号码输入有误");
            return;
        }
        time.start();
        QWApplication.mPreferences.edit().putLong("BindVertifyCodeStartTime", System.currentTimeMillis()).commit();
        verticalcode.setEnabled(false);
        ObtainBindphoneIdentifyCodeReq req = new ObtainBindphoneIdentifyCodeReq();
        req.setNetCallback(this);
        req.setRequestType(Request.Method.POST);
        req.setMobile(phoneNumber);
        req.setUserId(UserInfo.userId + "");
        req.addRequest();

    }


    // 让后台修改绑定手机
    private void bindNewPhone(String code) {
        // 得到新手机号码
        String newPhone = et_newphone.getText().toString();
        String loginPassword = et_password.getText().toString();
        BindNewTelephoneReq req = new BindNewTelephoneReq();
        req.setNetCallback(this);
        req.setRequestType(Request.Method.POST);
        req.setMobile(newPhone);
        req.setLoginPassword(loginPassword);
        req.setCode(code);
        req.setUserId(UserInfo.userId);
        req.addRequest();
    }

    // 验证码再次获取的倒计时
    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            verticalcode.setText("获取验证码");
            verticalcode.setEnabled(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            verticalcode.setText(millisUntilFinished / 1000 + "s");
            QWApplication.mPreferences.edit().putLong("VertifyCodeTime", System.currentTimeMillis()).commit();
        }

    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof IdentifyCodeResponse) {
            IdentifyCodeResponse resp = (IdentifyCodeResponse) baseRes;
            try {
                String status = resp.getStatus();
                String message = resp.getMessage();
                if (!TextUtils.isEmpty(status) && status.equals("success")) {
                    codeinga = resp.getDataMap().getCode();
                    QWApplication.mPreferences.edit().putString("BindPhoneVertifyCode", codeinga).commit();
                } else {
                    time.onFinish();
                    time.cancel();
                    ToastTool.showShortBigToast(this, message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (baseRes instanceof BindNewTelephoneResponse) {
            BindNewTelephoneResponse btr = (BindNewTelephoneResponse) baseRes;
            WaitTool.dismissDialog();
            String status = btr.getStatus();
            String message = btr.getMessage();
            if (!TextUtils.isEmpty(status) && status.equals("success")) {
                LogUtil.d("haha BindNewTelephoneResponse 成功");
                // 如果成功则提示并跳转到登录页面
                ToastTool.showShortBigToast(this, message);
                // 清空手机保存的个人信息
                UserInfo.userId = 0;
                UserInfo.starttime = "";
                UserInfo.endstringtime = "";
                UserInfo.account_Id = "";
                UserInfo.onlineCode = "";
                UserInfo.isLoginSuccess = false;
                QWApplication.mPreferences.edit().putInt("userId", 0)
                        .putString("endstringtime", "").putString("starttime", "")
                        .putString("account_Id", "").putString("onlineCode", "").putBoolean("isLoginSuccess", false).commit();
                AcitivityCollector.finishAll();
                //开启活动
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                ToastTool.showShortBigToast(this, message);
            }
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        ToastTool.showShortBigToast(this, "网络异常，请检查您的网络");
    }
}
