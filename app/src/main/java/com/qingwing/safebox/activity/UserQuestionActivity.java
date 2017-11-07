package com.qingwing.safebox.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qingwing.safebox.R;
import com.qingwing.safebox.bluetooth.BluetoothService;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.ToastTool;

public class UserQuestionActivity extends Activity implements OnClickListener, NetCallBack {
    private TextView tv_qustion1, tv_qustion2, tv_qustion3;
    private EditText et_answer1, et_answer2, et_answer3;
    private Button bt_modifypwd;
    private ImageView dialog_callback;
    private String answer1;
    private String answer2;
    private String answer3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_question);
        AcitivityCollector.addActivity(this);
        String question1 = getIntent().getStringExtra("question1");
        String question2 = getIntent().getStringExtra("question2");
        String question3 = getIntent().getStringExtra("question3");
        answer1 = getIntent().getStringExtra("answer1");
        answer2 = getIntent().getStringExtra("answer2");
        answer3 = getIntent().getStringExtra("answer3");

        tv_qustion1 = (TextView) findViewById(R.id.tv_qustion1);
        tv_qustion2 = (TextView) findViewById(R.id.tv_qustion2);
        tv_qustion3 = (TextView) findViewById(R.id.tv_qustion3);
        et_answer1 = (EditText) findViewById(R.id.et_answer1);
        et_answer2 = (EditText) findViewById(R.id.et_answer2);
        et_answer3 = (EditText) findViewById(R.id.et_answer3);
        bt_modifypwd = (Button) findViewById(R.id.bt_modifypwd);
        dialog_callback = (ImageView) findViewById(R.id.dialog_callback);

        tv_qustion1.setText(question1);
        tv_qustion2.setText(question2);
        tv_qustion3.setText(question3);
        dialog_callback.setOnClickListener(this);
        bt_modifypwd.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AcitivityCollector.removeActivity(this);
    }

    private boolean sureMibao(String answer1, String answer2, String answer3) {
        String as1 = et_answer1.getText().toString().trim();
        String as2 = et_answer2.getText().toString().trim();
        String as3 = et_answer3.getText().toString().trim();

        if (as1.equals(answer1) && as2.equals(answer2) && as3.equals(answer3)) {
            return true;
        } else {
            return false;
        }
    }

    private void showMessage(String message) {
        ToastTool.showShortBigToast(this, message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_callback:
                finish();
                overridePendingTransition(0, R.anim.out);
                break;
            case R.id.bt_modifypwd:
                if (CommUtils.isNetworkAvailable(UserQuestionActivity.this)) {
                    if (BluetoothService.isConnected) {
                        ToastTool.showShortBigToast(this, "请先连接您的保管箱再进行操作！");
                        return;
                    }
                    boolean isSuccess = sureMibao(answer1, answer2, answer3);
                    if (isSuccess) {
                        Intent intent = new Intent(UserQuestionActivity.this, ModifyOpenBoxPasswordActivity.class);
                        intent.putExtra("setOpenPassword", true);
                        startActivity(intent);
                        finish();
                    } else {
                        showMessage("回答错误！");
                    }
                } else {
                    showMessage("网络异常");
                }
                break;
        }
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {

    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
    }
}
