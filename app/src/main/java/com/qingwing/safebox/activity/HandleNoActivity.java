package com.qingwing.safebox.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qingwing.safebox.R;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.ToastTool;

/**
 * Created by wangshengyin on 2017-04-25.
 * email:shoxgov@126.com
 */

public class HandleNoActivity extends Activity implements View.OnClickListener {

    private EditText no_edit;
    private Button no_ok;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_handle_no);
        AcitivityCollector.addActivity(this);
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AcitivityCollector.removeActivity(this);
    }

    private void initViews() {
        no_edit = (EditText) findViewById(R.id.handle_no_edit);
        no_ok = (Button) findViewById(R.id.handle_no_ok);
        findViewById(R.id.title_bar_back).setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title_bar_title);
        title.setText("手动输入编号");
        no_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_bar_back:
                finish();
                break;
            case R.id.handle_no_ok:
                if (CommUtils.isFastClick()) {
                    return;
                }
                String noResult = no_edit.getText().toString();
                if (TextUtils.isEmpty(noResult)) {
                    ToastTool.showShortBigToast(HandleNoActivity.this, "请输入保管箱编号");
                    return;
                }
                hidenKeyword();
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("result", noResult);
                resultIntent.putExtras(bundle);
                setResult(RESULT_OK, resultIntent);
                finish();
                break;
        }
    }

    private void hidenKeyword() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(HandleNoActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
