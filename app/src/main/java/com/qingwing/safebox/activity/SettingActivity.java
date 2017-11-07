package com.qingwing.safebox.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.adapter.SettingAdapter;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.bluetooth.BluetoothService;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.ToastTool;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends Activity
        implements OnClickListener {
    private ImageView calback;
    private ListView setting_list;
    private SettingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        AcitivityCollector.addActivity(this);
        intitview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AcitivityCollector.removeActivity(this);
    }

    private void intitview() {
        setting_list = (ListView) findViewById(R.id.setting_list);
        adapter = new SettingAdapter(this);
        setting_list.setAdapter(adapter);
        setting_list.setOnItemClickListener(onItemClickListener);
        calback = (ImageView) findViewById(R.id.calback);// 返回
        calback.setOnClickListener(this);
        findViewById(R.id.logout_tv).setOnClickListener(this);
        adapter.setData(initData());
    }

    private List<String> initData() {
        List<String> data = new ArrayList<>();
        data.add("用户信息");
        data.add("设置解锁方式");
        if (UserInfo.lock_style.equals("a")) {//lock_style: 手势类型  a:手势   b:指纹  c:数字
            data.add("修改开箱手势");
        } else {
            data.add("修改开箱密码");
        }
        data.add("修改绑定手机");
        data.add("推荐有奖");
        data.add("关于我们");
        return data;
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            if (CommUtils.isFastClick()) {
                return;
            }
            switch (position) {
                case 0:
                    Intent userinfo = new Intent(SettingActivity.this, UserInfoActivity.class);
                    startActivity(userinfo);
                    break;
                case 1:// 设置解锁方式
                    if (!UserInfo.UserBindState) {
//                        ToastTool.showShortBigToast(SettingActivity.this, "请先绑定保管箱");
                        return;
                    }
                    Intent zw1 = new Intent(SettingActivity.this, SettingLockTypeActivity.class);
                    startActivityForResult(zw1, 1);
                    break;
                case 2:// 修改数字开箱密码
                    if (!UserInfo.UserBindState) {
//                        ToastTool.showShortBigToast(SettingActivity.this, "请先绑定保管箱");
                        return;
                    }
                    if (!CommUtils.isNetworkAvailable(SettingActivity.this)) {
                        ToastTool.showShortBigToast(SettingActivity.this, "网络异常，请检查您的网络");
                        return;
                    }
                    if (!BluetoothService.isConnected) {
                        ToastTool.showShortBigToast(SettingActivity.this, "当前设备未连接");
                        return;
                    }
                    if (UserInfo.lock_style.equals("a")) {//lock_style: 手势类型  a:手势   b:指纹  c:数字
//                        ToastTool.showShortBigToast(SettingActivity.this, "需要先验证数字开箱密码");
                        AlertDialog.Builder build = new AlertDialog.Builder(SettingActivity.this);
                        View verticalView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.zidingyi_alert2, null);
                        final EditText verticalOldPwdEdit = (EditText) verticalView.findViewById(R.id.old_password);
                        build.setView(verticalView);
                        build.setPositiveButton("确定", new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String oldPasswordString = verticalOldPwdEdit.getText().toString().trim();
                                LogUtil.d("填的密码:" + oldPasswordString + " 存的密码:" + UserInfo.UserOpenBoxPassowrd);
                                if (!TextUtils.isEmpty(oldPasswordString) && oldPasswordString.equals(UserInfo.UserOpenBoxPassowrd)) {
                                    Intent intent = new Intent(SettingActivity.this, GuestureLockPwdSettingActivity.class);
                                    startActivity(intent);
                                } else {
                                    ToastTool.showShortBigToast(SettingActivity.this, "密码错误");
                                    verticalOldPwdEdit.setText("");
                                }
                            }
                        });
                        build.setNegativeButton("取消", new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        build.show();
                    } else {
                        Intent number = new Intent(SettingActivity.this, ModifyOpenBoxPwdByNumberActivity.class);
                        startActivity(number);
                    }
                    break;
                case 3:// 修改绑定手机
                    Intent BindPhoneIntent = new Intent(SettingActivity.this, BindPhoneActivity.class);
                    startActivity(BindPhoneIntent);
                    break;
                case 4:
                    Intent share = new Intent(SettingActivity.this, ShareActivity.class);
                    startActivity(share);
                    break;
                case 5:
                    Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            adapter.setData(initData());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calback:
                LogUtil.d("haha   点击返回MainBoxActicity");
                finish();
                overridePendingTransition(0, R.anim.out);
                break;
            case R.id.logout_tv:
                logoutDialog();
                break;
            default:
                break;
        }
    }

    private void logoutDialog() {
        LogUtil.d("haha 显示退出登录二次确认对话框");
        Builder builder = new Builder(this);
        final AlertDialog dialogExit = builder.create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);
        TextView cancle = (TextView) view.findViewById(R.id.cancle);
        TextView exit = (TextView) view.findViewById(R.id.exit);
        cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogExit.dismiss();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                resetUser();
                dialogExit.dismiss();
            }
        });
        dialogExit.setView(view, 0, 0, 0, 0);
        dialogExit.show();
    }

    private void resetUser() {
        UserInfo.isLoginSuccess = false;
        UserInfo.userId = 0;
        UserInfo.starttime = "";
        UserInfo.endstringtime = "";
        UserInfo.account_Id = "";
        UserInfo.onlineCode = "";
        UserInfo.BlueId = "";
        LogUtil.d("resetUser 解绑方式:" + UserInfo.lock_style);
        QWApplication.mPreferences.edit()
                .putString("loginName", "")
                .putInt("userId", 0).putString("onlineCode", "")
                .putString("endstringtime", "").putString("starttime", "").putString("BlueId", "")
                .putString("account_Id", "").putBoolean("isLoginSuccess", false).commit();
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        AcitivityCollector.finishAll();
    }

}