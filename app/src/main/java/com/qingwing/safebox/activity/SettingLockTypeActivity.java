package com.qingwing.safebox.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.ToastTool;


public class SettingLockTypeActivity extends FragmentActivity implements OnClickListener {

    private TextView shoushi;
    private TextView number;
    private EditText oldPassword;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_select_locktype);
        AcitivityCollector.addActivity(this);
        initViews();

    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AcitivityCollector.removeActivity(this);
    }

    private void initViews() {
        shoushi = (TextView) findViewById(R.id.shousi);
        number = (TextView) findViewById(R.id.number);
        //得到当前解锁方式并提示用户当前所用解锁方式，a:手势   b:指纹  c:数字
        if (TextUtils.isEmpty(UserInfo.lock_style)) {
            Toast.makeText(this, "查无此开锁方式", Toast.LENGTH_LONG).show();
        } else if (UserInfo.lock_style.equals("a")) {
            shoushi.setTextColor(Color.RED);
            shoushi.setText("手势密码(当前选中)");
            number.setTextColor(Color.BLACK);
            number.setText("数字密码");
        } else if (UserInfo.lock_style.equals("c")) {
            shoushi.setTextColor(Color.BLACK);
            shoushi.setText("手势密码");
            number.setTextColor(Color.RED);
            number.setText("数字密码(当前选中)");
        }
        findViewById(R.id.calback).setOnClickListener(this);
        number.setOnClickListener(this);
        shoushi.setOnClickListener(this);
    }

    private void UserAlterPassword() {
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.zidingyi_alert2, null);
        oldPassword = (EditText) view.findViewById(R.id.old_password);
        build.setView(view);
        build.setPositiveButton("确定", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String oldPasswordString = oldPassword.getText().toString().trim();
                LogUtil.d("填的密码:" + oldPasswordString + " 存的密码:" + UserInfo.UserOpenBoxPassowrd);
                if (!TextUtils.isEmpty(oldPasswordString) && oldPasswordString.equals(UserInfo.UserOpenBoxPassowrd)) {
                    System.out.println("成功可以继续设置");
                    Intent intent = new Intent();
                    intent.setClass(SettingLockTypeActivity.this, GuestureLockPwdSettingActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    ToastTool.showShortBigToast(SettingLockTypeActivity.this, "密码错误");
                    oldPassword.setText("");
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            ToastTool.showShortBigToast(this, "解锁方式设置为手势解锁");
            // 将当前选中信息修改
            shoushi.setTextColor(Color.RED);
            shoushi.setText("手势密码(当前选中)");
            number.setTextColor(Color.BLACK);
            number.setText("数字密码");
            UserInfo.lock_style = "a";
            QWApplication.mPreferences.edit().putString("lock_style", "a")
                    .commit();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.calback:
                finish();
                overridePendingTransition(0, R.anim.out);
                break;
            case R.id.shousi:
                //已经是手势解锁
                //得到手势密码字符串
                LogUtil.d("得到的手势密码是" + UserInfo.lock_password);
                //判断是否有用过。没用过则跳转设置手势密码。用过则提示已经切换成手势解锁
                if (!TextUtils.isEmpty(UserInfo.lock_password)) {
                    if (UserInfo.lock_style.equals("a")) {
                        ToastTool.showShortBigToast(this, "您当前设置的已经是手势解锁");
                        return;
                    }
                    ToastTool.showShortBigToast(this, "解锁方式设置为手势解锁");
                    // 将当前选中信息修改
                    shoushi.setTextColor(Color.RED);
                    shoushi.setText("手势密码(当前选中)");
                    number.setTextColor(Color.BLACK);
                    number.setText("数字密码");
                    UserInfo.lock_style = "a";
                    QWApplication.mPreferences.edit().putString("lock_style", "a")
                            .commit();
                } else {
                    UserAlterPassword();
                }
                break;
            case R.id.number:
                if (UserInfo.lock_style.equals("a")) {
                    //进行手势验证
                    shoushi.setTextColor(Color.BLACK);
                    shoushi.setText("手势密码");
                    number.setTextColor(Color.RED);
                    number.setText("数字密码(当前选中)");
                    ToastTool.showShortBigToast(this, "解锁方式设置为数字解锁");
                    //将数字解锁style放入shareperference
                    UserInfo.lock_style = "c";
                    QWApplication.mPreferences.edit().putString("lock_style", "c").commit();
                } else if (UserInfo.lock_style.equals("c")) {
                    ToastTool.showShortBigToast(this, "您当前设置的是数字密码");
                    //将数字解锁style放入shareperference
                    UserInfo.lock_style = "c";
                    QWApplication.mPreferences.edit().putString("lock_style", "c").commit();
                }
                break;
        }
    }
}
