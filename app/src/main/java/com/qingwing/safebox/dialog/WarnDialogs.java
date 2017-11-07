package com.qingwing.safebox.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.LinearLayout.LayoutParams;

import com.android.volley.Request;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.request.ReturnDepositReq;
import com.qingwing.safebox.net.response.RefundDepositResponse;
import com.qingwing.safebox.net.response.ReturnDepositResponse;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.ToastTool;
import com.qingwing.safebox.utils.WaitTool;


public class WarnDialogs extends Dialog {


    public WarnDialogs(Context context) {
        super(context, R.style.CustomDialog_discovery);
    }

    public WarnDialogs(Context context, int themeId) {
        super(context, themeId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_warn);
        int width = (int) (QWApplication.screenWidthPixels * 0.8);
        setOnKeyListener(keylistener);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        getWindow().setLayout(width, LayoutParams.WRAP_CONTENT);
    }

    private OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return true;
            } else {
                return false;
            }
        }
    };

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

}
