package com.qingwing.safebox.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.qingwing.safebox.R;
import com.qingwing.safebox.adapter.RechargeHistoryAdapter;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.dialog.ReturnDepositDialogs;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.CheckDepositAmount;
import com.qingwing.safebox.net.request.PayRecordReq;
import com.qingwing.safebox.net.response.CheckDepositAmountResponse;
import com.qingwing.safebox.net.response.PayRecordResponse;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.ToastTool;
import com.qingwing.safebox.utils.WaitTool;
import com.qingwing.safebox.view.XListView;

import java.util.ArrayList;

public class RechargeHistoryActivity extends Activity implements NetCallBack, OnClickListener {
    private XListView mPullRefreshListView;
    private RechargeHistoryAdapter mAdapter;
    private ImageView calback;
    private ArrayList<String> list = new ArrayList<String>();
    private TextView mydespositNum;
    /**
     * 我的押金额
     */
    private int mydesposit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_history);
        AcitivityCollector.addActivity(this);
        WaitTool.showDialog(this, "正在读取缴费记录请稍后...");
        initview();
        requestPayRecord();
        queryCheckDepositAmountReq();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AcitivityCollector.removeActivity(this);
    }

    private void initview() {
        mydespositNum = (TextView) findViewById(R.id.mydesposit_num);
        calback = (ImageView) findViewById(R.id.calback);
        calback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.out);
            }
        });
        mPullRefreshListView = (XListView) findViewById(R.id.pull_refresh_list2);
        mAdapter = new RechargeHistoryAdapter(this);
        mPullRefreshListView.setAdapter(mAdapter);
        mPullRefreshListView.setHeaderPullRefresh(false);// 取消下拉刷新
        mPullRefreshListView.setFooterPullRefresh(false);// 取消上拉刷新
        findViewById(R.id.mydesposit_layout).setOnClickListener(this);
    }

    private void requestPayRecord() {
        PayRecordReq req = new PayRecordReq();
        req.setNetCallback(this);
        req.setRequestType(Request.Method.POST);
        req.setUserId(UserInfo.userId);
        req.addRequest();
    }

    /**
     * 查询押金
     */
    private void queryCheckDepositAmountReq() {
        WaitTool.showDialog(this, "正在查询，请稍等...");
        CheckDepositAmount checkDepositAmount = new CheckDepositAmount();
        checkDepositAmount.setNetCallback(this);
        checkDepositAmount.setUserId(UserInfo.userId);
        checkDepositAmount.setRequestType(Request.Method.POST);
        checkDepositAmount.addRequest();
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof PayRecordResponse) {
            WaitTool.dismissDialog();
            PayRecordResponse prr = (PayRecordResponse) baseRes;
            String message = prr.getMessage();
            String status = prr.getStatus();
            if (status.equals("error")) {
                ToastTool.showShortBigToast(this, message);
                return;
            }
            list = (ArrayList<String>) prr.getDataMap().getPaymentList();
            mAdapter.setData(list);
            mPullRefreshListView.stopRefresh();
        } else if (baseRes instanceof CheckDepositAmountResponse) {
            WaitTool.dismissDialog();
            CheckDepositAmountResponse amountResponse = (CheckDepositAmountResponse) baseRes;
            int status = amountResponse.getStatusCode();
            String message = amountResponse.getMessage();
            if (status == 200) {
                if (amountResponse.getDataMap().getDepositAmount() != 0) {
                    LogUtil.d("押金数额:" + amountResponse.getDataMap().getDepositAmount());
                    mydesposit = amountResponse.getDataMap().getDepositAmount();
                    mydespositNum.setText(mydesposit + "元，押金退款");
                } else {
                    ToastTool.showShortBigToast(this, "您的账号当前无押金");
                }
            } else {
                ToastTool.showShortBigToast(this, message);
            }
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        ToastTool.showShortBigToast(this, "网络异常");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mydesposit_layout:
                if (CommUtils.isFastClick()) {
                    return;
                }
                if (UserInfo.UserBindState) {
                    ToastTool.showShortBigToast(this, "请先解除绑定才能退还押金");
                    return;
                }
                if (mydesposit > 0) {
                    ReturnDepositDialogs dialogs = new ReturnDepositDialogs(this);
                    dialogs.show();
                }
                break;
        }
    }
}
