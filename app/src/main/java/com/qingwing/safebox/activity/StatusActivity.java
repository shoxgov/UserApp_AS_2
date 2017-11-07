package com.qingwing.safebox.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.qingwing.safebox.R;
import com.qingwing.safebox.utils.AcitivityCollector;

public class StatusActivity extends Activity {
    private TextView txt_name, txt_name2, txt_name3;
    private ImageView calback;
    public ArrayList<String> stringContent;
    private List<String> stringName = Arrays.asList("箱门状态：", "电池电量：", "到期时间：");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        AcitivityCollector.addActivity(this);
        initView();
    }

    private void initView() {
        stringContent = new ArrayList<String>();
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_name2 = (TextView) findViewById(R.id.txt_name2);
        txt_name3 = (TextView) findViewById(R.id.txt_name3);
        calback = (ImageView) findViewById(R.id.calback);
        calback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                StatusActivity.this.finish();
            }
        });

        //得到保管箱状态信息
//        stringContent = BoxActivity.stringContent;
        if (stringName.size() != 0 && stringContent.size() != 0) {
            txt_name.setText(stringName.get(0) + stringContent.get(0));
            txt_name2.setText(stringName.get(1) + stringContent.get(1));
            txt_name3.setText(stringName.get(2) + stringContent.get(2));
        } else {
            txt_name.setText(stringName.get(0) + "无");
            txt_name2.setText(stringName.get(1) + "无");
            txt_name3.setText(stringName.get(2) + "无");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AcitivityCollector.removeActivity(this);
    }
}
