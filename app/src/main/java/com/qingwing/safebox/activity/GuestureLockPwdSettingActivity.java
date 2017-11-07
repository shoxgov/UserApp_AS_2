package com.qingwing.safebox.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.ToastTool;
import com.qingwing.safebox.view.LocusPassWordView;


public class GuestureLockPwdSettingActivity extends Activity implements OnClickListener {

    private String password;
    private String passwordb;
    private LocusPassWordView mLocusPassWordView;
    private int setPassword = 1;
    private TextView Inscreen_state;
    private Button states_a;
    private Button states_b;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting_guesture_password);
        AcitivityCollector.addActivity(this);
        findViewById(R.id.calback).setOnClickListener(this);
        Inscreen_state = (TextView) findViewById(R.id.quereng);
        mLocusPassWordView = (LocusPassWordView) findViewById(R.id.mLocusPassWordView);
        states_a = (Button) findViewById(R.id.states_a);
        states_b = (Button) findViewById(R.id.states_b);
        states_b.setOnClickListener(this);
        states_a.setOnClickListener(this);
        states_b.setEnabled(false);
        Inscreen_state.setText("绘制新解锁图案,请至少连接四个点");
        Inscreen_state.setTextColor(Color.BLACK);
        Inscreen_state.setTextSize(20);
        mLocusPassWordView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Inscreen_state.setText("完成后松开手指");
                Inscreen_state.setTextColor(Color.BLACK);
                return false;
            }
        });
        mLocusPassWordView.setOnCompleteListener(new LocusPassWordView.OnCompleteListener() {

            @Override
            public void onComplete(String mpassword) {
                states_a.setVisibility(View.VISIBLE);
                states_b.setVisibility(View.VISIBLE);
                Inscreen_state.setText("完成后请松开手指");
                Inscreen_state.setTextColor(Color.BLACK);
                if (setPassword == 1) {
                    password = mpassword;
                    Inscreen_state.setVisibility(View.VISIBLE);
                    Inscreen_state.setText("请再次绘制解锁图案");
                    Inscreen_state.setTextColor(Color.BLACK);
                    mLocusPassWordView.clearPassword();
                    setPassword++;
                } else {
                    //两次图案一样
                    if (password.equals(mpassword)) {
                        passwordb = password;
                        Inscreen_state.setText("您的新解锁图案");
                        Inscreen_state.setTextColor(Color.BLACK);
                        states_b.setEnabled(true);
                        setPassword = 1;
                    } else {
                        mLocusPassWordView.markError();
                        mLocusPassWordView.clearPassword();
                        ToastTool.showShortBigToast(GuestureLockPwdSettingActivity.this, "两次图案绘制不一致，请重试");
                        Inscreen_state.setTextColor(Color.RED);
                        /* 复位 */
                        Inscreen_state.setText("绘制解锁图案,请至少连接四个点");
                        Inscreen_state.setTextColor(Color.BLACK);
                        password = null;
                        setPassword = 1;
                        mLocusPassWordView.clearPassword();
                    }
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AcitivityCollector.removeActivity(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calback:
                finish();
                break;
            case R.id.states_b:
                if (!TextUtils.isEmpty(password) && passwordb.equals(password)) {
                    //设置密码
                    Toast.makeText(this, "密码设置成功 ", Toast.LENGTH_LONG).show();
                    UserInfo.lock_style = "a";
                    UserInfo.lock_password = password;
                    QWApplication.mPreferences.edit().putString("lock_password", password)
                            .putString("lock_style", "a").commit();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "密码设置错误，请重绘", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.states_a:
                Inscreen_state.setText("绘制解锁图案,请至少连接四个点");
                Inscreen_state.setTextColor(Color.BLACK);
                password = null;
                setPassword = 1;
                mLocusPassWordView.clearPassword();
                break;
            default:
                break;
        }
    }
}
