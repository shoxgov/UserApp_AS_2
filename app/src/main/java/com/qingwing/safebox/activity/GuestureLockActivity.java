package com.qingwing.safebox.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qingwing.safebox.R;
import com.qingwing.safebox.fragment.ChangeGuestureTypeFragment;
import com.qingwing.safebox.fragment.SelectLockTypeFragment;
import com.qingwing.safebox.fragment.SettingGuesturePwdFragment;
import com.qingwing.safebox.utils.AcitivityCollector;

public class GuestureLockActivity extends FragmentActivity {
    private Fragment newContent = null;
    private TextView tv_title;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guesture_lock);
        AcitivityCollector.addActivity(this);
        ImageView calback = (ImageView) findViewById(R.id.calback);
        tv_title = (TextView) findViewById(R.id.tv_title);

        calback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.out);
            }
        });
        String styletype = getIntent().getStringExtra("style");
        if (!TextUtils.isEmpty(styletype)) {
            if (styletype.equals("2")) {
                if (newContent == null) {
                    newContent = new ChangeGuestureTypeFragment();
                    switchContent(newContent, "");
                }
            } else if (styletype.equals("4")) {
                if (newContent == null) {
                    newContent = new SelectLockTypeFragment();
                    switchContent(newContent, "选择手机解锁方式");
                    tv_title.setText("选择解锁方式");
                }
            } else if (styletype.equals("a")) {
                if (newContent == null) {
                    newContent = new SettingGuesturePwdFragment();
                    switchContent(newContent, "修改解锁手势");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AcitivityCollector.removeActivity(this);
    }

    // 切换碎片
    public void switchContent(Fragment fragment, String title) {
        Fragment mContent = fragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
        tv_title.setText(title);
    }

}
