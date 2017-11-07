package com.qingwing.safebox.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Request;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.bluetooth.BLECommandManager;
import com.qingwing.safebox.bluetooth.BluetoothService;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.ObtainPayOrderInfoReq;
import com.qingwing.safebox.net.request.ObtainPayResultReq;
import com.qingwing.safebox.net.request.ObtainPaySetsInfoReq;
import com.qingwing.safebox.net.request.ObtainWeiXinPayInfo;
import com.qingwing.safebox.net.request.RechargeByBindedBoxReq;
import com.qingwing.safebox.net.request.RechargeByCardNumberReq;
import com.qingwing.safebox.net.request.RechargeByUnbindedBoxReq;
import com.qingwing.safebox.net.request.WeiXinPayResult;
import com.qingwing.safebox.net.response.JudgeWeiXinPayResult;
import com.qingwing.safebox.net.response.ObtainPayOrderInfoResponse;
import com.qingwing.safebox.net.response.ObtainPayResultResponse;
import com.qingwing.safebox.net.response.ObtainPaySetsInfoResponse;
import com.qingwing.safebox.net.response.ObtainWeinResponse;
import com.qingwing.safebox.net.response.RechargeByCardNumberResponse;
import com.qingwing.safebox.net.response.RechargeResultResponse;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.ToastTool;
import com.qingwing.safebox.utils.WaitTool;
import com.qingwing.safebox.wxapi.Constants;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageMoneyActivity extends Activity implements OnClickListener, NetCallBack {
    public static final int CARDNUMBER_PAY_SUCCESS = 18;
    public static final int WEIXIN_ALIPAY_PAY_SUCCESS = 19;
    // 得到后台的套餐
    private List<ObtainPaySetsInfoResponse.PackageSetInfo> packageSetsList = new ArrayList<ObtainPaySetsInfoResponse.PackageSetInfo>();
    private List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
    private String boxID_a;
    private GridView gView;
    private boolean deposit;
    private int selectTaocan;
    private EditText et_cardNo;
    private ImageView callback;
    private TextView payrecord;
    // 适配器
    private SimpleAdapter adapter;
    // 从后台得到的完整的订单信息
    private String payInfo;
    // 得不到套餐时的提示语
    private TextView txt_tip;
    private int position;
    private IWXAPI api;
    private int wxPayResult = -1;
    /**
     * 支付方式的选择
     */
    private int typeDeposit;
    /**
     * 充值方式
     */
    private int RecordWhich;
    /**
     * 获取微信支付交易号  微信支付订单
     */
    private String weiXinPayTrade = "";
    /**
     * 是否是来续费的
     */
    private boolean isPayMoneyBind = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_package);
        AcitivityCollector.addActivity(this);
        if (getIntent().hasExtra("isPayMoneyBind")) {
            isPayMoneyBind = getIntent().getBooleanExtra("isPayMoneyBind", false);
        }
        et_cardNo = (EditText) findViewById(R.id.et_cardNo);
        boxID_a = getIntent().getStringExtra("boxID_a");
        LogUtil.d("PackageMoneyActivity  得到跳转传递过来的蓝牙boxid=" + boxID_a);
        callback = (ImageView) findViewById(R.id.image_callback);
        txt_tip = (TextView) findViewById(R.id.txt_tip);
        payrecord = (TextView) findViewById(R.id.payrecord);
        payrecord.setOnClickListener(this);
        gView = (GridView) findViewById(R.id.gridview);
        gView.setOnItemClickListener(listener);
        findViewById(R.id.confirm).setOnClickListener(this);
        callback.setOnClickListener(this);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        api.registerApp(Constants.APP_ID);
        if (CommUtils.isNetworkAvailable(this)) {
            // 访问后台取得所有套餐
            obtainPackages();
        } else {
            ToastTool.showShortBigToast(this, "网络异常,请先检查您的网络");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (wxPayResult) {
            case 2:
                WaitTool.dismissDialog();
                Toast.makeText(PackageMoneyActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                wxPayResult = -1;
                weiXinPayTrade = "";
                break;
            case 1:
            default:
                if (!TextUtils.isEmpty(weiXinPayTrade)) {
                    WeiXinPayResult ReqPayResult = new WeiXinPayResult();
                    ReqPayResult.setNetCallback(PackageMoneyActivity.this);
                    ReqPayResult.setRequestType(Request.Method.POST);
                    ReqPayResult.setSubject(weiXinPayTrade);
                    ReqPayResult.addRequest();
                }
                wxPayResult = -1;
                weiXinPayTrade = "";
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                ToastTool.showShortBigToast(this, "获取支付方式错误");
            }
            String style = bundle.getString("style");
            if (style.equals("zhifubao")) {
                ZhifubaoPay(position);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AcitivityCollector.removeActivity(this);
    }

    /**
     * 获取后台套餐信息
     * postParams={barcode=20170419, userId=418}
     */
    private void obtainPackages() {
        if (!TextUtils.isEmpty(boxID_a)) {
            WaitTool.showDialog(this, "正在读取套餐信息请稍后...");
            ObtainPaySetsInfoReq req = new ObtainPaySetsInfoReq();
            req.setNetCallback(this);
            req.setRequestType(Request.Method.POST);
            req.setBarcode(boxID_a);
            req.setUserId(UserInfo.userId + "");
            req.addRequest();
        } else {
            txt_tip.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 点击每个item时触发的回调函数 jine[i] = info.amount+"";// 套餐金额 taocan[i] = info.packageName;// 套餐类型
     * taocanNo[i] = info.packageNo;//套餐No taocanId[i] = info.id;//套餐Id
     */

    private void showPayWayDialog(final int position) {
        PackageMoneyActivity.this.position = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(PackageMoneyActivity.this);
        builder.setTitle("充值方式");
        RecordWhich = 0;
        final String[] array = getResources().getStringArray(R.array.Type);
        builder.setSingleChoiceItems(array, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                RecordWhich = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String type = array[RecordWhich];
                if (type.equals("支付宝")) {
                    ZhifubaoPay(position);
                } else if (type.equals("微信")) {
                    if (WXAPIFactory.createWXAPI(PackageMoneyActivity.this, Constants.APP_ID).getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
                        weixinPay(position);
                    } else {
                        ToastTool.showShortBigToast(PackageMoneyActivity.this, "当前不支持微信支付，请您检查是否有安装微信");
                    }
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }


    private void ZhifubaoPay(int position) {
        ObtainPayOrderInfoReq req = new ObtainPayOrderInfoReq();
        req.setNetCallback(PackageMoneyActivity.this);
        req.setRequestType(Request.Method.POST);
        req.setBody(packageSetsList.get(position).getPackageName());
        req.setSubject(packageSetsList.get(position).getPackageName());
        if (deposit) {
            req.setPrice((packageSetsList.get(position).getReallyAmount() + packageSetsList.get(position).getDepositAmount()) + "");
        } else {
            req.setPrice(packageSetsList.get(position).getReallyAmount() + "");
        }
        req.addRequest();
        selectTaocan = position;
    }

    private void weixinPay(int position) {
        WaitTool.showDialog(this, "请稍等...");
        ObtainWeiXinPayInfo info = new ObtainWeiXinPayInfo();
        info.setNetCallback(this);
        info.setRequestType(Request.Method.POST);
        info.setBody(packageSetsList.get(position).getPackageName());
        if (deposit) {
            info.setPrice((int) ((packageSetsList.get(position).getReallyAmount()
                    + packageSetsList.get(position).getDepositAmount()) * 100) + "");
        } else {
            info.setPrice((int) (packageSetsList.get(position).getReallyAmount() * 100) + "");
        }
        info.addRequest();
        selectTaocan = position;
    }

    OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, final int position, long arg3) {
            LogUtil.d("OnItemClickListener->>" + position);
            if (CommUtils.isFastClick()) {
                return;
            }
            if (CommUtils.isNetworkAvailable(PackageMoneyActivity.this)) {
                if (packageSetsList.get(position).getPackageName().equals("试用套餐")) {
                    //如果是试用套餐直接发送服务器。
                    selectTaocan = position;
                    RechargeByBoxReq(5);
                } else {
                    showPayWayDialog(position);
                }
            } else {
                ToastTool.showShortBigToast(PackageMoneyActivity.this, "网络异常，请检查您的网络");
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                //不可连续点击按钮
                if (CommUtils.isFastClick()) {
                    return;
                }
                if (CommUtils.isNetworkAvailable(this)) {
                    // 获取用户ID 得到输入框的信息
                    String cardNo = et_cardNo.getText().toString();
                    if (!TextUtils.isEmpty(cardNo)) {
                        // 请求后台该充值卡是否可用可充值
                        WaitTool.showDialog(this, "正在充值请稍后...");
                        RechargeByCardNumberReq req = new RechargeByCardNumberReq();
                        req.setNetCallback(this);
                        req.setRequestType(Request.Method.POST);
                        req.setCardNo(cardNo);
                        req.setUserId(UserInfo.userId);
                        req.addRequest();
                    } else {
                        ToastTool.showShortBigToast(this, "请输入充值卡账号");
                    }
                } else {
                    ToastTool.showShortBigToast(this, "网络异常，请检查您的网络");
                }

                break;
            case R.id.image_callback:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_cardNo.getWindowToken(), 0);
                PackageMoneyActivity.this.finish();
//                isPayMoneyBind = false;////是否是续费绑定
                overridePendingTransition(0, R.anim.out);
                break;
            case R.id.payrecord:
                if (CommUtils.isFastClick()) {
                    return;
                }
                Intent intent = new Intent(this, RechargeHistoryActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void rebuildPackages() {
        items.clear();
        for (ObtainPaySetsInfoResponse.PackageSetInfo psi : packageSetsList) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("taocaname", psi.getPackageName());// 套餐名字
            //						item.put("taocan", psi.getPackageDesc());// 套餐类型
            item.put("jine", "￥" + psi.getAmount());// 套餐原始金额
            item.put("reallyamount", "现售：" + "￥" + psi.getReallyAmount());// 套餐优惠之后的金额
            if (deposit) {
                item.put("isDeposit", "押金：" + "￥" + psi.getDepositAmount());// 套餐名字
            }
            items.add(item);
        }
        //为true需要缴纳押金
        if (deposit) {
            String from[] = {"taocaname", "jine", "reallyamount", "isDeposit"};
            int to[] = {R.id.tv_taocan_name, R.id.tv_jine, R.id.tv_reallyamount, R.id.tv_depositamount};
            adapter = new SimpleAdapter(this, items, R.layout.package_gridview_item, from, to);
        } else {
            String from[] = {"taocaname", "jine", "reallyamount"};
            int to[] = {R.id.tv_taocan_name, R.id.tv_jine, R.id.tv_reallyamount};
            adapter = new SimpleAdapter(this, items, R.layout.package_gridview_item, from, to);
        }
        gView.setAdapter(adapter);
    }

    private void RechargeByBoxReq(int type) {
        // 支付成功后查询用户是否绑定了保管箱
        // 绑定状态是否绑定
        String packageNo = packageSetsList.get(selectTaocan).getPackageNo();// taocanNo[i] = info.packageNo;
        LogUtil.d(" 绑定状态：" + UserInfo.UserBindState + "用户id：" + UserInfo.userId + " 保管箱ID:" + boxID_a + " 套餐编号:" + packageNo);
        WaitTool.showDialog(this, "正在充值，请稍等...");
        if (UserInfo.UserBindState) {
            // 已经绑定了则调用保管箱续费的接口
            RechargeByBindedBoxReq req = new RechargeByBindedBoxReq();
            req.setNetCallback(PackageMoneyActivity.this);
            req.setRequestType(Request.Method.POST);
            req.setBarcode(boxID_a);
            req.setPackageId(packageSetsList.get(selectTaocan).getId() + "");// taocanId[i] = info.id;
            req.setPayType(type);// 支付宝支付1 ，充值卡2，微信支付3
            req.setUserId(UserInfo.userId);
            req.addRequest();
        } else {
            // 未绑定则调用缴费到用户的接口
            LogUtil.d("haha  进入未绑定缴费接口");
            RechargeByUnbindedBoxReq req = new RechargeByUnbindedBoxReq();
            req.setNetCallback(PackageMoneyActivity.this);
            req.setRequestType(Request.Method.POST);
            req.setPackageId(packageSetsList.get(selectTaocan).getId() + "");// taocanId[i] = info.id;
            req.setPayType(type);// 支付宝支付1 ,充值卡2,微信支付3
            req.setUserId(UserInfo.userId);
            req.addRequest();
        }
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof ObtainPaySetsInfoResponse) {
            WaitTool.dismissDialog();
            ObtainPaySetsInfoResponse opsr = (ObtainPaySetsInfoResponse) baseRes;
            try {
                String status = opsr.getStatus();
                deposit = opsr.getDataMap().isDeposit();
                LogUtil.d("当前押金状态：" + deposit);
                if (!TextUtils.isEmpty(status) && status.equals("success")) {
                    // 有套餐则取消此提示语
                    txt_tip.setVisibility(View.GONE);
                    packageSetsList = opsr.getDataMap().getPackageList();
                    if (packageSetsList == null) {
                        packageSetsList = new ArrayList<ObtainPaySetsInfoResponse.PackageSetInfo>();
                    }
                    if (packageSetsList == null || packageSetsList.isEmpty()) {
                        ToastTool.showShortBigToast(this, "未获取到套餐");
                        return;
                    }
                    rebuildPackages();
                } else {
                    // 提示绑定保管箱后才能使用该付款方式
                    ToastTool.showShortBigToast(this, opsr.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (baseRes instanceof ObtainWeinResponse) {
            WaitTool.dismissDialog();
            ObtainWeinResponse obtainWeinResponse = (ObtainWeinResponse) baseRes;
            final ObtainWeinResponse.WeiXin weiXin = obtainWeinResponse.getDataMap().getWeixin();
            LogUtil.d("  得到的微信订单信息:" + weiXin.toString());
            if (weiXin.getRetmsg().contains("错误")) {
                ToastTool.showShortBigToast(this, "得到的微信订单信息为空");
                return;
            }
            LogUtil.d("haha 微信开始调用支付接口:" + weiXin.toString());
            weiXinPayTrade = weiXin.getOut_trade_no();
            LogUtil.d("sendType.WeiXinPayTrade：" + weiXinPayTrade);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PayReq payReq = new PayReq();
                    payReq.appId = weiXin.getAppid();
                    payReq.partnerId = weiXin.getPartnerId();
                    payReq.prepayId = weiXin.getPrepayId();
                    payReq.nonceStr = weiXin.getNonceStr();
                    payReq.timeStamp = weiXin.getTimeStamp();
                    payReq.sign = weiXin.getSign();
                    payReq.packageValue = "Sign=WXPay";
                    api.sendReq(payReq);
                }
            }).start();
        } else if (baseRes instanceof ObtainPayOrderInfoResponse) {
            ObtainPayOrderInfoResponse opoir = (ObtainPayOrderInfoResponse) baseRes;
            payInfo = opoir.getDataMap().getAllStr();// 完整的符合支付宝参数规范的订单信息
            LogUtil.d("  得到的支付宝订单信息:" + payInfo);
            if (TextUtils.isEmpty(payInfo)) {
                ToastTool.showShortBigToast(this, "得到的支付宝订单信息为空");
                return;
            }
            // 异步调用支付宝类进行支付 必须异步调用
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 构造PayTask 对象
                    LogUtil.d(" 支付宝开始调用支付接口:" + payInfo);
                    PayTask alipay = new PayTask(PackageMoneyActivity.this);
                    // 调用支付接口，获取支付结果
                    String result = alipay.pay(payInfo, true);
                    LogUtil.d(" 支付宝结果:" + result);
                    ObtainPayResultReq req = new ObtainPayResultReq();
                    req.setNetCallback(PackageMoneyActivity.this);
                    req.setRequestType(Request.Method.POST);
                    req.setResultStr(result);
                    req.addRequest();
                }
            }).start();
        } else if (baseRes instanceof JudgeWeiXinPayResult) {
            //判断微信的支付结果
            WaitTool.dismissDialog();
            JudgeWeiXinPayResult oprr = (JudgeWeiXinPayResult) baseRes;
            int statusCode = oprr.getStatusCode();
            if (statusCode == 200) {
                String status = oprr.getDataMap().getWeiXin().getStatus();
                LogUtil.d("收到微信支付后台服务器传过来的结果" + status);
                if (status.contains("支付成功")) {
                    RechargeByBoxReq(3);
                } else {
                    ToastTool.showShortBigToast(this, oprr.getDataMap().getWeiXin().getStatus());
                }
            }
            weiXinPayTrade = "";
        } else if (baseRes instanceof ObtainPayResultResponse) {
            //判断支付宝的支付结果
            ObtainPayResultResponse oprr = (ObtainPayResultResponse) baseRes;
            try {
                String message = oprr.getMessage();
                int statusCode = oprr.getStatusCode();
                LogUtil.d(" 支付結果    message=" + message + ", statusCode=" + statusCode);
                if (statusCode == 200) {
                    RechargeByBoxReq(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (baseRes instanceof RechargeResultResponse) {
            //支付宝or微信后台服务器充值成功的结果
            WaitTool.dismissDialog();
            RechargeResultResponse rrr = (RechargeResultResponse) baseRes;
            String message = rrr.getMessage();
            int statusCode = rrr.getStatusCode();
            String endDate = rrr.getDataMap().getEndDate();

            if (statusCode != 200) {
                ToastTool.showShortBigToast(this, message);
                return;
            }
            setResult(WEIXIN_ALIPAY_PAY_SUCCESS);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date;
                date = sdf.parse(endDate);
                sdf = new SimpleDateFormat("yyMMdd");
                String formatDate = sdf.format(date);
                LogUtil.d("  支付結果 message:" + message + "statusCode:" + statusCode + formatDate);
                ToastTool.showShortBigToast(this, message);
                UserInfo.endstringtime = formatDate;
                QWApplication.mPreferences.edit().putString("endstringtime", formatDate).commit();
                if (BluetoothService.isConnected /*&& !isPayMoneyBind*/) {
                    String endtime = UserInfo.endstringtime.substring(0, 6);
                    String startTime = UserInfo.starttime.substring(0, 6);
                    String password = UserInfo.UserOpenBoxPassowrd;
                    BLECommandManager.userBind(startTime, endtime, boxID_a, password, PackageMoneyActivity.this, "01");
                    try {
                        if (packageSetsList.get(selectTaocan).getPackageName().equals("试用套餐")) {
                            packageSetsList.remove(selectTaocan);
                            rebuildPackages();
                        } else {
                            deposit = false;
                            rebuildPackages();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    PackageMoneyActivity.this.finish();
                }
                if (!UserInfo.UserBindState) {//此处去绑定
                    finish();
                }
            } catch (ParseException e) {
                ToastTool.showShortBigToast(this, "充值异常，请重新登录");
                e.printStackTrace();
            }
        } else if (baseRes instanceof RechargeByCardNumberResponse) {
            //充值卡充值成功的结果
            WaitTool.dismissDialog();
            RechargeByCardNumberResponse rcnr = (RechargeByCardNumberResponse) baseRes;
            String message = rcnr.getMessage();
            int statusCode = rcnr.getStatusCode();
            String endDate = rcnr.getDataMap().getEndDate();
            if (statusCode != 200) {
                ToastTool.showShortBigToast(this, message);
                return;
            }
            setResult(CARDNUMBER_PAY_SUCCESS);
            try {
                LogUtil.d("  支付結果 message:" + message + "statusCode:" + statusCode + "endDate" + endDate);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date;
                date = sdf.parse(endDate);
                sdf = new SimpleDateFormat("yyMMdd");
                String formatDate = sdf.format(date);
                ToastTool.showShortBigToast(this, message);
                UserInfo.endstringtime = formatDate;
                QWApplication.mPreferences.edit().putString("endstringtime", formatDate).commit();
                if (BluetoothService.isConnected && !TextUtils.isEmpty(boxID_a)/*&& !isPayMoneyBind*/) {//首次 未绑定时需要绑定
                    String endtime = UserInfo.endstringtime.substring(0, 6);
                    String startTime = UserInfo.starttime.substring(0, 6);
                    String password = UserInfo.UserOpenBoxPassowrd;
                    BLECommandManager.userBind(startTime, endtime, boxID_a, password, PackageMoneyActivity.this, "01");
                    try {
                        if (packageSetsList.get(selectTaocan).getPackageName().equals("试用套餐")) {
                            packageSetsList.remove(selectTaocan);
                            rebuildPackages();
                        } else {
                            deposit = false;
                            rebuildPackages();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_cardNo.getWindowToken(), 0);
                    PackageMoneyActivity.this.finish();
                }
                if (!UserInfo.UserBindState) {//此处去绑定
                    finish();
                }
            } catch (ParseException e) {
                ToastTool.showShortBigToast(this, "充值异常，请重新登录");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        WaitTool.dismissDialog();
    }
}
