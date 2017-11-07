package com.qingwing.safebox.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.qingwing.safebox.utils.LogUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI mIwxapi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIwxapi = WXAPIFactory.createWXAPI(this, null);
        mIwxapi.handleIntent(getIntent(), this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mIwxapi.handleIntent(getIntent(), this);
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX && BaseResp.ErrCode.ERR_OK == resp.errCode) {
            LogUtil.d("收到微信支付返回的结果 resp:"+resp.toString());
//            Packager_Money.wxPayResult = 1
        } else {
            LogUtil.d("收到微信支付返回的结果 errCode="+resp.errCode + " str="+resp.errStr);
//            Packager_Money.wxPayResult = 2;
        }
        finish();
    }

    @Override
    public void onReq(BaseReq arg0) {

    }
}
