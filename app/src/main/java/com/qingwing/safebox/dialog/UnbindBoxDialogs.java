package com.qingwing.safebox.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;

import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.imp.DialogCallBack;
import com.qingwing.safebox.utils.CommUtils;


public class UnbindBoxDialogs extends Dialog implements OnClickListener {


    private Context context;
    private DialogCallBack dialogcallback;


    public UnbindBoxDialogs(Context context, DialogCallBack dialogcallback) {
        super(context, R.style.CustomDialog_discovery);
        this.context = context;
        this.dialogcallback = dialogcallback;
    }

    public UnbindBoxDialogs(Context context, int themeId) {
        super(context, themeId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_unbindbox);
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
    public void onBackPressed() {
        dismiss();
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refuseBtn:
                dismiss();
                break;
            case R.id.agreeBtn:
                if (CommUtils.isFastClick()) {
                    return;
                }
                if (dialogcallback != null) {
                    dialogcallback.OkDown(null);
                }
                dismiss();
                break;
        }
    }

}
