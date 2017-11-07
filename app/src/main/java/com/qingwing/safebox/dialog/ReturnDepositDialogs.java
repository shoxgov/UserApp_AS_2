package com.qingwing.safebox.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;

import com.android.volley.Request;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.imp.DialogCallBack;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.RefundDepositReq;
import com.qingwing.safebox.net.request.ReturnDepositReq;
import com.qingwing.safebox.net.response.RefundDepositResponse;
import com.qingwing.safebox.net.response.ReturnDepositResponse;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.ToastTool;
import com.qingwing.safebox.utils.WaitTool;


public class ReturnDepositDialogs extends Dialog implements OnClickListener, NetCallBack {

    private Context context;
    private DialogCallBack dialogcallback;
    private DisplayMetrics dm;
    private EditText truenameEdit;
    private EditText accountEdit;


    public ReturnDepositDialogs(Context context) {
        super(context, R.style.CustomDialog_discovery);
        this.context = context;
    }

    public ReturnDepositDialogs(Context context, int themeId) {
        super(context, themeId);
        this.context = context;
        dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_return_deposit);
        Button canceBtn = (Button) findViewById(R.id.refuseBtn);
        Button submitBtn = (Button) findViewById(R.id.agreeBtn);
        truenameEdit = (EditText) findViewById(R.id.return_deposit_truename);
        accountEdit = (EditText) findViewById(R.id.return_deposit_account);
        submitBtn.setOnClickListener(this);
        canceBtn.setOnClickListener(this);
        int width = (int) (QWApplication.screenWidthPixels * 0.8);
        if (width <= 0) {
            width = (int) (dm.widthPixels * 0.8);
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
                dismiss();
                break;
            case R.id.agreeBtn:
                if (CommUtils.isFastClick()) {
                    return;
                }
                String useraccount = accountEdit.getText().toString();
                String username = truenameEdit.getText().toString();
                if (!judgeDeposit(username, useraccount, 3)) {
                    return;
                }
                //请求是否可以申请退款 可以了后再调用实际退款接口
                if (CommUtils.isNetworkAvailable(context)) {
                    WaitTool.showDialog(context, "正在申请退款，请稍等...");
                    RefundDepositReq refundDepositReq = new RefundDepositReq();
                    refundDepositReq.setNetCallback(this);
                    refundDepositReq.setUserId(UserInfo.userId);
                    refundDepositReq.setRequestType(Request.Method.POST);
                    refundDepositReq.addRequest();
                } else {
                    ToastTool.showShortBigToast(context, "网络异常,请检查您的网络");
                }
                break;
        }
    }

    private boolean judgeDeposit(String et_username,
                                 String et_useraccount, int typeDeposit) {
        if (TextUtils.isEmpty(et_username)) {
            ToastTool.showShortBigToast(context, "请填写完您的用户名");
            return false;
        } else if (TextUtils.isEmpty(et_useraccount)) {
            ToastTool.showShortBigToast(context, "请填写完您的退款账号");
            return false;
        } else if (typeDeposit == 0) {
            ToastTool.showShortBigToast(context, "请选择您的退款账号类型");
            return false;
        } else if (typeDeposit == 3) {
            if (et_useraccount.length() < 15 || et_useraccount.length() > 19) {
                ToastTool.showShortBigToast(context, "银行卡格式错误，请检查后再重新输入");
                return false;
            }
            return true;
        } else {
            return true;
        }
    }

    /**
     * 实际申请退款接口
     */
    protected void applicationForDrawback(String et_username,
                                          String et_useraccount, int typeDeposit) {
        ReturnDepositReq depositReq = new ReturnDepositReq();
        depositReq.setNetCallback(this);
        depositReq.setRequestType(Request.Method.POST);
        depositReq.setName(et_username);
        depositReq.setType(typeDeposit + "");
        depositReq.setAccount(et_useraccount);
        depositReq.setUserId(UserInfo.userId + "");
        depositReq.addRequest();
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof ReturnDepositResponse) {
            //申请退还押金返回的消息  g onResponse----------:{"data":null,"dataMap":{},"message":"请确认已经解绑保管箱！","status":"error","statusCode":500}
            WaitTool.dismissDialog();
            ReturnDepositResponse depositResponse = (ReturnDepositResponse) baseRes;
            String message = depositResponse.getMessage();
            LogUtil.d(message);
            ToastTool.showShortBigToast(context, message);
            dismiss();
        } else if (baseRes instanceof RefundDepositResponse) {//请求是否可以申请退款押金
            WaitTool.dismissDialog();
            RefundDepositResponse amountResponse = (RefundDepositResponse) baseRes;
            int status = amountResponse.getStatusCode();
            String message = amountResponse.getMessage();
            if (status == 200) {
                applicationForDrawback(truenameEdit.getText().toString(), accountEdit.getText().toString(), 3);
            } else {
                ToastTool.showShortBigToast(context, message);
            }
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        WaitTool.dismissDialog();
        ToastTool.showShortBigToast(context, error.toString());
    }
}
