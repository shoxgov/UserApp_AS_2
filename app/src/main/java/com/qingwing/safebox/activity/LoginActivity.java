package com.qingwing.safebox.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.bluetooth.BLECommandManager;
//import com.qingwing.safebox.bluetooth.BluetoothService;
import com.qingwing.safebox.dialog.LoadingDialog;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.UserLoginReq;
import com.qingwing.safebox.net.response.UserLoginResponse;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.MD5Utils;
import com.qingwing.safebox.utils.ToastTool;

public class LoginActivity extends Activity implements OnClickListener, NetCallBack {
    private TextView tv_registered;
    private EditText userId, et_passWord;
    private Button bt_login;
    private static final int REQUEST_ENABLE_BT = 1;
    private TextView forget_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        AcitivityCollector.addActivity(this);
        tv_registered = (TextView) findViewById(R.id.tv_registered);
        forget_password = (TextView) findViewById(R.id.forget_password);
        userId = (EditText) findViewById(R.id.et_userId);
        et_passWord = (EditText) findViewById(R.id.et_passWord);
        bt_login = (Button) findViewById(R.id.bt_login);
        bt_login.setOnClickListener(this);
        tv_registered.setOnClickListener(this);
        forget_password.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BLECommandManager.isSupportBLE(this)) {
            // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
            if (!BLECommandManager.isEnable(this)) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            ToastTool.showShortBigToast(this, "不支持ble蓝牙设备");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AcitivityCollector.removeActivity(this);
    }

    // 跳转回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_password:
                Intent chage_password = new Intent(LoginActivity.this, ModifyLoginPasswordActivity.class);
                startActivity(chage_password);
                break;
            case R.id.tv_registered:
                Intent registered = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registered);
                break;
            case R.id.bt_login:
                Login();
                break;
            default:
                break;
        }
    }


    private void Login() {
        if (judge()) {
            if (CommUtils.isNetworkAvailable(LoginActivity.this)) {
                LoadingDialog.showDialog(this, "正在登录中");
                UserLoginReq req = new UserLoginReq();
                req.setNetCallback(this);
                req.setMobile(userId.getText().toString());
                req.setPassword(et_passWord.getText().toString());
                req.setMobType(android.os.Build.MANUFACTURER);
                req.setMobModel(android.os.Build.MODEL);
                req.setRequestType(Request.Method.POST);
                req.addRequest();
                LogUtil.d("(MobileBrand:MobileMode)" + android.os.Build.MANUFACTURER + ":" + android.os.Build.MODEL);
            } else {
                ToastTool.showShortBigToast(this, "当前网络不可用");
            }
        }
    }

    private void task() {
        if (TextUtils.isEmpty(UserInfo.lock_style)) {
            UserInfo.lock_style = "c";
            QWApplication.mPreferences.edit().putString("lock_style", "c").commit();
        }
        QWApplication.mPreferences.edit().putBoolean("isLoginSuccess", true).commit();
        UserInfo.isLoginSuccess = true;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        Intent login = new Intent(LoginActivity.this, MainBoxActivity.class);
//        BluetoothService.isScanning = false;
        startActivity(login);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private boolean judge() {
        if ("".equals(userId.getText().toString())) {
            ToastTool.showShortBigToast(this, "手机号不能为空");
            return false;
        } else if ("".equals(et_passWord.getText().toString())) {
            ToastTool.showShortBigToast(this, "密码不能为空");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof UserLoginResponse) {
            UserLoginResponse url = (UserLoginResponse) baseRes;
            int s = url.getStatusCode();
            String message = url.getMessage();
            LogUtil.d("haha UserLoginResponse 登录是否成功：" + url.getDataMap().toString());
            if (s == 200) {
                LoadingDialog.dismissDialog();
                UserInfo.userId = 0;
                UserInfo.starttime = "";
                UserInfo.endstringtime = "";
                UserInfo.account_Id = "";
                UserInfo.pass = "";
                UserInfo.userLoginPassword = "";
                UserInfo.lock_style = "";
                QWApplication.mPreferences.edit().clear().commit();
                int user_id = url.getDataMap().getUserId();
                String account_Id = url.getDataMap().getID();
                String userStatus = url.getDataMap().getUserStatus();
                String blueId = url.getDataMap().getBlueId();
                String requestCode = url.getDataMap().getRequestCode();
                String mobile = url.getDataMap().getMobile();
                String OnlineCode = url.getDataMap().getOnlineCode();
                String endDate = url.getDataMap().getEndDate();
                String endstringtime = MD5Utils.change_start(endDate);
                boolean isOverDate = Boolean.parseBoolean(url.getDataMap().getIsOverDate());
                if (!TextUtils.isEmpty(userStatus) && userStatus.equals("bind")) {
                    String starttime = MD5Utils.change_start(url.getDataMap().getStartDate());
                    UserInfo.starttime = starttime;
                    String pwd = url.getDataMap().getOpenPassword();
                    UserInfo.endstringtime = endstringtime;
                    UserInfo.QrBtId = UserInfo.BlueId = blueId;
                    UserInfo.UserBindState = true;
                    UserInfo.UserOpenBoxPassowrd = pwd;
                    QWApplication.mPreferences.edit()
                            .putString("BlueId", blueId)
                            .putString("UserOpenBoxPassowrd", pwd)
                            .putString("starttime", starttime)
                            .putString("endstringtime", endstringtime)
                            .putBoolean("UserBindState", true).commit();
                } else {
                    String isPayment = url.getDataMap().getIsPayment();
                    if (isPayment.equals("1")) {
                        UserInfo.endstringtime = endstringtime;
                    } else {
                        //表示当前未缴费
                        UserInfo.endstringtime = "";
                    }
                    UserInfo.BlueId = "";
                    UserInfo.UserBindState = false;
                    QWApplication.mPreferences.edit()
                            .putBoolean("UserBindState", false)
                            .putString("endstringtime", endstringtime)
                            .putString("BlueId", "").commit();
                }
                UserInfo.userId = user_id;
                UserInfo.isOverDate = isOverDate;
                UserInfo.onlineCode = OnlineCode;
                UserInfo.account_Id = account_Id;
                UserInfo.requestCode = requestCode;
                UserInfo.mobile = mobile;
                UserInfo.userLoginPassword = et_passWord.getText().toString().trim();
                QWApplication.mPreferences.edit()
                        .putInt("userId", user_id)
                        .putString("account_Id", account_Id)
                        .putString("requestCode", requestCode)
                        .putString("mobile", mobile)
                        .putString("onlineCode", OnlineCode)
                        .putString("RecordList", "")
                        .putBoolean("isOverDate", isOverDate)
                        .putString("loginName", userId.getText().toString().trim())
                        .putString("LoginPassword", et_passWord.getText().toString().trim())
                        .commit();
                if (user_id > 0) {
//                    QWApplication.getInstance().loadUserImage();
                    task();
                } else {
                    LoadingDialog.dismissDialog();
                    ToastTool.showShortBigToast(this, "登录异常，请重试");
                }
            } else {
                LoadingDialog.dismissDialog();
                ToastTool.showShortBigToast(this, message);
            }
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        LoadingDialog.dismissDialog();
        ToastTool.showShortBigToast(this, "网络异常,请检查您的网络");
    }

}
