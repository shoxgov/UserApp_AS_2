package com.qingwing.safebox.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.qingwing.safebox.R;
import com.qingwing.safebox.activity.RechargeHistoryActivity;
import com.qingwing.safebox.imp.DialogCallBack;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.ToastTool;

public class PayMoneyPopWindows extends PopupWindow implements OnClickListener {
    private Context context;
    private DialogCallBack callBack;

    public PayMoneyPopWindows(Context context, DialogCallBack callBack) {
        this.context = context;
        this.callBack = callBack;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View PayMoneyPopWindows_View = inflater.inflate(R.layout.paymoneywindows_add, null);
        // 设置PayMoneyPopWindows的View
        this.setContentView(PayMoneyPopWindows_View);
        // 设置PayMoneyPopWindows弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        // 设置PayMoneyPopWindows弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置PayMoneyPopWindows弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        this.setAnimationStyle(R.style.AnimationPreview_pop);
        RelativeLayout re_payment = (RelativeLayout) PayMoneyPopWindows_View.findViewById(R.id.re_payment);
        RelativeLayout re_deposit = (RelativeLayout) PayMoneyPopWindows_View.findViewById(R.id.re_deposit);
        re_payment.setOnClickListener(this);
        re_deposit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_payment:
                if (CommUtils.isNetworkAvailable(context)) {
                    Intent intent = new Intent(context, RechargeHistoryActivity.class);
                    context.startActivity(intent);
                    dismiss();
                } else {
                    ToastTool.showShortBigToast(context, "网络异常");
                }
                break;
            case R.id.re_deposit:
                if (CommUtils.isNetworkAvailable(context)) {
                    if(callBack != null){
                        callBack.OkDown(null);
                    }
                    dismiss();
                } else {
                    ToastTool.showShortBigToast(context, "网络异常");
                }
                break;
            default:
                break;
        }
    }

}
