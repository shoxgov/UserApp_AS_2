package com.qingwing.safebox.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;


public class WarnInfosDialog extends Dialog {


    private TextView infos;

    public WarnInfosDialog(Context context) {
        this(context, R.style.CustomDialog_discovery);
    }

    public WarnInfosDialog(Context context, int themeId) {
        super(context, themeId);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_warn_infos);
        infos = (TextView) findViewById(R.id.infos);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int width = (int) (QWApplication.screenWidthPixels * 0.8);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        getWindow().setLayout(width, LayoutParams.WRAP_CONTENT);
    }

    public void show(String infos) {
        this.infos.setText(infos);
        show();
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

}
