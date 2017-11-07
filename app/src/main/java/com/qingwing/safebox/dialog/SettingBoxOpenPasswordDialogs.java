package com.qingwing.safebox.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;

import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.imp.DialogCallBack;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.ToastTool;


public class SettingBoxOpenPasswordDialogs extends Dialog implements OnClickListener {

    private Context context;
    private DialogCallBack dialogcallback;
    private EditText pwdEdit, pwd2Edit;


    public SettingBoxOpenPasswordDialogs(Context context, DialogCallBack dialogcallback) {
        super(context, R.style.CustomDialog_discovery);
        this.context = context;
        this.dialogcallback = dialogcallback;
    }

    public SettingBoxOpenPasswordDialogs(Context context, int themeId) {
        super(context, themeId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_setting_openbox_password);
        pwdEdit = (EditText) findViewById(R.id.setting_openbox_pwd_edt);
        pwd2Edit = (EditText) findViewById(R.id.setting_openbox_pwd2_edt);
        Button canceBtn = (Button) findViewById(R.id.refuseBtn);
        Button submitBtn = (Button) findViewById(R.id.agreeBtn);
        submitBtn.setOnClickListener(this);
        canceBtn.setOnClickListener(this);
        int width = (int) (QWApplication.screenWidthPixels * 0.8);
        if (width <= 0) {
            width = LayoutParams.MATCH_PARENT;
        }
        getWindow().setLayout(width, LayoutParams.WRAP_CONTENT);
    }


    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onBackPressed() {
        dismiss();
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refuseBtn:
                if (dialogcallback != null) {
                    dialogcallback.CancleDown();
                }
                dismiss();
                break;
            case R.id.agreeBtn:
                if (CommUtils.isFastClick()) {
                    return;
                }
                String pwd = pwdEdit.getText().toString();
                String pwd2 = pwd2Edit.getText().toString();
                if (pwd.length() != 6) {
                    ToastTool.showShortBigToast(context, "请输入正确的密码");
                    return;
                }
                if (!pwd.equals(pwd2)) {
                    ToastTool.showShortBigToast(context, "二次输入密码不一致");
                    return;
                }
                hidenKeyword();
                if (dialogcallback != null) {
                    dialogcallback.OkDown(pwd);
                }
                dismiss();
                break;
        }
    }

    private void hidenKeyword() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(SettingBoxOpenPasswordDialogs.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
