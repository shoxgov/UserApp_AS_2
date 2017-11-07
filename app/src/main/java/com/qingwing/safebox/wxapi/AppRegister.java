package com.qingwing.safebox.wxapi;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppRegister extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        IWXAPI createWXAPI = WXAPIFactory.createWXAPI(context, null);
        createWXAPI.registerApp(Constants.APP_ID);
    }

}
