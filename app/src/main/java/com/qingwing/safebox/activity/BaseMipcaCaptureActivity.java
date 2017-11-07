package com.qingwing.safebox.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.Result;
import com.qingwing.safebox.zxing.view.ViewfinderView;

public abstract class BaseMipcaCaptureActivity extends Activity {

    public abstract void handleDecode(Result result, Bitmap barcode);

    public abstract ViewfinderView getViewfinderView();

    public abstract Handler getHandler();

    public abstract void drawViewfinder();


}