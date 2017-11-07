package com.qingwing.safebox.utils;

import android.util.Log;

public class LogUtil {
    private static final String TAG = "qingWing";

    public static void d(String log) {
        Log.d(TAG, log);
    }

    public static void d(String tag, String log) {
        Log.d(tag, log);
    }
}
