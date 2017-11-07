package com.qingwing.safebox.utils;

import android.content.Context;
import android.text.TextUtils;

import com.qingwing.safebox.dialog.CustomProgressDialog;


public class WaitTool {
    private static CustomProgressDialog waitingDialog;

    public static void showDialog(Context cont) {
        try {
            if (waitingDialog == null) {
                waitingDialog = CustomProgressDialog.createDialog(cont);
            }
            waitingDialog.setCanceledOnTouchOutside(false);
            waitingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDialog(Context cont, String msg) {
        try {
            msg = msg == null ? "正在加载中..." : msg;
            if (waitingDialog == null) {
                waitingDialog = CustomProgressDialog.createDialog(cont);
            }
            waitingDialog.setCanceledOnTouchOutside(false);
            waitingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDialog(Context cont, String msg, boolean isShow) {
        try {
            if (TextUtils.isEmpty(msg))
                msg = "正在加载中...";

            if (waitingDialog == null) {
                waitingDialog = CustomProgressDialog.createDialog(cont);
                if (isShow)
                    waitingDialog.setMessage(msg);
                waitingDialog.setCanceledOnTouchOutside(false);
            }
            waitingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDialogNotcancel(Context cont, String msg,
                                           Boolean cancelflag) {
        try {
            msg = msg == null ? "正在加载中..." : msg;
            if (waitingDialog == null) {
                waitingDialog = CustomProgressDialog.createDialog(cont,
                        cancelflag);
            }
            waitingDialog.setCanceledOnTouchOutside(false);
            waitingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissDialog() {
        try {
            if (waitingDialog != null) {
                waitingDialog.cancelDismiss();
                waitingDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setCanceHandler(CustomProgressDialog.IWaitParent parent) {
        waitingDialog.setCanceHandler(parent);
    }

}