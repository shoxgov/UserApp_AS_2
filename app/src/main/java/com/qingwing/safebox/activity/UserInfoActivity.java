package com.qingwing.safebox.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.BoxStatusBean;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.observable.ObservableBean;
import com.qingwing.safebox.observable.ObserverManager;
import com.qingwing.safebox.utils.AcitivityCollector;

import java.util.Observable;
import java.util.Observer;

public class UserInfoActivity extends Activity
        implements OnClickListener, Observer, NetCallBack {

    private ImageView userPhoto;
    private TextView accountStatus;
    private TextView userinfoId;
    private TextView serviceEndDate;
    private TextView leftVoltage;
    private ImageView calback;
    private TextView titleName;


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        AcitivityCollector.addActivity(this);
        intitview();
        ObserverManager.getObserver().addObserver(this);
    }

    @Override
    protected void onDestroy() {
        ObserverManager.getObserver().deleteObserver(this);
        AcitivityCollector.removeActivity(this);
        super.onDestroy();
    }

    private void intitview() {
        userPhoto = (ImageView) findViewById(R.id.userinfo_photo);
        userinfoId = (TextView) findViewById(R.id.userinfo_id);// 用户ID
        accountStatus = (TextView) findViewById(R.id.userinfo_account_status);// 账号状态
        serviceEndDate = (TextView) findViewById(R.id.userinfo_service_endtime);// 服务终止时间
        leftVoltage = (TextView) findViewById(R.id.userinfo_left_voltage);// 剩余电量
        calback = (ImageView) findViewById(R.id.title_bar_back);// 返回
        titleName = (TextView) findViewById(R.id.title_bar_title);
        titleName.setText("用户信息");
        if (UserInfo.UserBindState) {
            accountStatus.setText("已绑定保管箱");
        } else {
            accountStatus.setText("未绑定设备");
        }
        if (TextUtils.isEmpty(BoxStatusBean.electricValue)) {
            leftVoltage.setText("未知");
        } else {
            leftVoltage.setText(BoxStatusBean.electricValue + "%");
        }
        // 用户ID设置
        userinfoId.setText(UserInfo.account_Id);
        if (UserInfo.UserBindState) {
            if (UserInfo.isOverDate) {
                String BindTime = ParseTime("20" + UserInfo.endstringtime.substring(0, 6));
                serviceEndDate.setText("租赁服务到期(" + BindTime + ")");
                serviceEndDate.setTextColor(Color.RED);
            } else {
                String BindTime = ParseTime("20" + UserInfo.endstringtime.substring(0, 6));
                serviceEndDate.setText(BindTime);
                serviceEndDate.setTextColor(Color.parseColor("#ffaaaaaa"));
            }
        } else if (TextUtils.isEmpty(UserInfo.endstringtime)) {
            serviceEndDate.setText("未开通服务");
            serviceEndDate.setTextColor(Color.parseColor("#ffaaaaaa"));
        } else {
            if (UserInfo.isOverDate) {
                String BindTime = ParseTime("20" + UserInfo.endstringtime.substring(0, 6));
                serviceEndDate.setText("租赁服务到期(" + BindTime + ")");
                serviceEndDate.setTextColor(Color.RED);
            } else {
                String BindTime = ParseTime("20" + UserInfo.endstringtime.substring(0, 6));
                serviceEndDate.setText(BindTime);
                serviceEndDate.setTextColor(Color.parseColor("#ffaaaaaa"));
            }
        }
        userPhoto.setOnClickListener(this);
        calback.setOnClickListener(this);
    }

    private String ParseTime(String string) {
        return string.substring(0, 4) + "/" + string.substring(4, 6) + "/" + string.substring(6, 8);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_bar_back:
                finish();
                overridePendingTransition(0, R.anim.out);
                break;
            case R.id.userinfo_photo:
                Intent in = new Intent(UserInfoActivity.this, PictureUploadActivity.class);
                startActivity(in);
                break;

            default:
                break;
        }
    }


    @Override
    public void update(Observable arg0, Object obj) {
        ObservableBean ob = (ObservableBean) obj;
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
    }
}