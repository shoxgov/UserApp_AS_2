package com.qingwing.safebox.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * @Class: LoadingDialog
 * @Package: com.example.utills
 * @Description: 加载对话框显示
 * @author: wsy@unibroad.com
 * @version: V1.0
 */
public class LoadingDialog {
    private static ProgressDialog loadingDialog;

    private static Dialog createLoadingDialog(Context context, String msg) {
        loadingDialog=new ProgressDialog(context);
        loadingDialog.setMessage(msg);
        return loadingDialog;
    }
    public static void showDialog(Context cont){
        if (loadingDialog != null) {
            loadingDialog = null;
        }
        loadingDialog = (ProgressDialog) createLoadingDialog(cont, "");
        loadingDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        loadingDialog.show();
    }

    public static void showDialog(Context cont, String msg) {
        try {
            msg = msg == null ? "正在加载中..." : msg;
            if (loadingDialog != null) {
                loadingDialog = null;
            }
            loadingDialog = (ProgressDialog) createLoadingDialog(cont, msg);
            loadingDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
            loadingDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
            loadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void dismissDialog() {
        try {
            if (loadingDialog != null) {
                loadingDialog.setCancelable(true);// 设置是否可以通过点击Back键取消
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadingDialog = null;
        }
    }
}
