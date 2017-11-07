package com.qingwing.safebox.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.adapter.MsgRecordHistoryAdapter;
import com.qingwing.safebox.bean.BoxStatusBean;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.bluetooth.BLECommandManager;
import com.qingwing.safebox.bluetooth.BleObserverConstance;
import com.qingwing.safebox.bluetooth.BluetoothService;
import com.qingwing.safebox.dialog.UnbindBoxDialogs;
import com.qingwing.safebox.fragment.HomeGuestureUnlockBoxFragment;
import com.qingwing.safebox.fragment.HomeNumberUnlockBoxFragment;
import com.qingwing.safebox.imp.DialogCallBack;
import com.qingwing.safebox.imp.SingleLoginThread;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.BoxAlarmUploadReq;
import com.qingwing.safebox.net.request.CloseBoxUploadReq;
import com.qingwing.safebox.net.request.GetWebTimeReq;
import com.qingwing.safebox.net.request.OpenBoxUploadReq;
import com.qingwing.safebox.net.request.UploadAlarmOrOpenBoxDataReq;
import com.qingwing.safebox.net.response.BoxAlarmUploadResponse;
import com.qingwing.safebox.net.response.CloseBoxUploadResponse;
import com.qingwing.safebox.net.response.GetWebTimeResponse;
import com.qingwing.safebox.net.response.OpenBoxUploadResponse;
import com.qingwing.safebox.net.response.UploadAlarmDataResponse;
import com.qingwing.safebox.net.response.UploadOpenBoxDataResponse;
import com.qingwing.safebox.observable.ObservableBean;
import com.qingwing.safebox.observable.ObserverManager;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.BlueDeviceUtils;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.ToastTool;
import com.qingwing.safebox.view.BoxMsgRecordPopwindow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class MainBoxActivity extends FragmentActivity implements OnClickListener, Observer, NetCallBack {
    /**
     * 标题栏蓝牙的状态
     */
    private static final int REQUEST_ENABLE_BT = 801;
    private ImageView btStatus;
    private FragmentManager fragmentManager;
    private Fragment[] mFragments;
    /**
     * 消息显示弹窗
     */
    private BoxMsgRecordPopwindow msgRecordPopwindow;
    private ListView boxMsgListView;
    private MsgRecordHistoryAdapter msgAdapter;
    private TextView btElectricValue;
    private ImageView btElectricIcon;
    /**
     * 联网服务端取得的系统时间
     */
    private String currentServiceTime;
    private RadioButton qr;
    /**
     * 保存上次加载时的开锁类型， 默认数字
     */
    private String lockStyle = "c";
    /*
    请求时 剩余的条数，用于判断是不是最后一条
     */
    private int boxSurplus;
    private String boxTime;
    /**
     * 收到保管箱报警消息上报的时间
     */
    private String boxWarnShortTime = "";
    /**
     * 收到保管箱开消息箱上报的时间
     */
    private String openboxTime = "";
    /**
     * 收到保管箱关箱上报的时间
     */
    private String closeBoxTime = "";
    private TextView recorderhistory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mainbox);
        ObserverManager.getObserver().addObserver(this);
        LogUtil.d("MainBoxActivity  addObserver");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
        AcitivityCollector.addActivity(this);
        Intent intent = new Intent(MainBoxActivity.this, BluetoothService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
        initView();
        init();
        SingleLoginThread.getInstance().startThread();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!UserInfo.lock_style.equals(lockStyle)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .hide(mFragments[0]).hide(mFragments[1]);
            if (UserInfo.lock_style.equals("a")) {
                lockStyle = "a";
                fragmentTransaction.show(mFragments[1]).commit();
            } else {
                lockStyle = "c";
                fragmentTransaction.show(mFragments[0]).commit();
            }
        }
    }

    @Override
    protected void onDestroy() {
        ObserverManager.getObserver().deleteObserver(this);
        unbindService(conn);
        unregisterReceiver(broadcastReceiver);
        LogUtil.d("MainBoxActivity  deleteObserver");
        super.onDestroy();
        BluetoothService.isScanning = false;
        SingleLoginThread.getInstance().stopThread();
        AcitivityCollector.removeActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AcitivityCollector.finishAll();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        recorderhistory = (TextView) findViewById(R.id.recorder_history_title);
        btElectricIcon = (ImageView) findViewById(R.id.image_dianliang);
        btElectricValue = (TextView) findViewById(R.id.tv_dianliang);
        btStatus = (ImageView) findViewById(R.id.booleth_state);
        boxMsgListView = (ListView) findViewById(R.id.state_list);
        qr = (RadioButton) findViewById(R.id.rb_qr);
        qr.setOnClickListener(this);
        findViewById(R.id.rb_pay).setOnClickListener(this);
        findViewById(R.id.rb_help).setOnClickListener(this);
        findViewById(R.id.rb_setting).setOnClickListener(this);
        boxMsgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (CommUtils.isFastClick()) {
                    return;
                }
                if (!UserInfo.UserBindState) {
                    ToastTool.showShortBigToast(MainBoxActivity.this, "您还未绑定保管箱，无法查看记录！");
                    return;
                }
                msgRecordPopwindow.show_center(boxMsgListView);
                /**
                 * 读取保管箱里的记录
                 */
                readBoxRecordInfo();
            }
        });
        if (UserInfo.UserBindState) {
            qr.setText("解绑");
            Drawable top = getResources().getDrawable(R.drawable.bottom_unbind);
/// 这一步必须要做,否则不会显示.
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            qr.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
        } else {
            qr.setText("扫码租箱");
            Drawable top = getResources().getDrawable(R.drawable.bottom_scan);
/// 这一步必须要做,否则不会显示.
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            qr.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
        }
    }

    private void init() {
        fragmentManager = getSupportFragmentManager();
        if (mFragments == null) {// 正常情况下去 加载根Fragment
            mFragments = new Fragment[2];
        }
        mFragments[0] = fragmentManager.findFragmentById(R.id.fragement_number_unlock);
        mFragments[1] = fragmentManager.findFragmentById(R.id.fragement_guesture_unlock);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .hide(mFragments[0]).hide(mFragments[1]);
        if (UserInfo.lock_style.equals("a")) {
            lockStyle = "a";
            fragmentTransaction.show(mFragments[1]).commit();
        } else {
            lockStyle = "c";
            fragmentTransaction.show(mFragments[0]).commit();
        }
        msgRecordPopwindow = new BoxMsgRecordPopwindow(this);
        msgAdapter = new MsgRecordHistoryAdapter(this);
        boxMsgListView.setAdapter(msgAdapter);
        msgAdapter.addData("");
        msgAdapter.addData("");
    }

    protected void showSingleLoginDialog() {
        if (bluetoothService != null) {
            bluetoothService.closeBT();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("青熠智能保管箱提示您");
        builder.setMessage("您的账号在另一个终端登录，您被强制下线！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //退出登录
                //取消用户自动登录
                UserInfo.isLoginSuccess = false;
                UserInfo.onlineCode = "";
                AcitivityCollector.finishAll();
                //开启活动
                Intent intent = new Intent(MainBoxActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //将状态监听设置为空
                dialog.dismiss();

            }
        });
        AlertDialog create = builder.create();
        create.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        create.show();
        QWApplication.mPreferences.edit().putBoolean("isLoginSuccess", false).commit();
    }

    //用户已经欠费，需要续费才能使用
    private void showEnterPayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialogv = builder.create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_paymoney, null);
        TextView cancle = (TextView) view.findViewById(R.id.cancle);
        TextView exit = (TextView) view.findViewById(R.id.exit);
        cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogv.dismiss();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainBoxActivity.this, PackageMoneyActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.putExtra("isPayMoneyBind", true);
                i.putExtra("boxID_a", UserInfo.BlueId);
                startActivityForResult(i, 6);
            }
        });
        dialogv.setView(view, 0, 0, 0, 0);
        dialogv.setCanceledOnTouchOutside(false);
        dialogv.show();
    }

    private void analyticStateData() {
        LogUtil.d("在analyticStateData里");
        LogUtil.d("后台服务到期时间：保管箱的服务到期时间 endstringtime=" + UserInfo.endstringtime + ":" + BoxStatusBean.serviceEndTimeBineary);
        switch (BoxStatusBean.lifeStatus) {
            case 0://00代表出厂初始状态（状态0）
                break;
            case 1://02代表出厂检验完成并已经设置保管箱ID号（状态1）
                ToastTool.showShortBigToast(this, "当前您的保管箱还未位置绑定，请联系客服！");
                break;
            case 2://04代表位置绑定完成但尚未有客户绑定（状态2）
                LogUtil.d("当前尚未有客户绑定");
                String endtime = UserInfo.endstringtime.substring(0, 6);
                String startTime = UserInfo.starttime.substring(0, 6);
                LogUtil.d("开箱密码：" + UserInfo.UserOpenBoxPassowrd);
                BLECommandManager.userBind(startTime, endtime, UserInfo.BlueId, UserInfo.UserOpenBoxPassowrd, this, "00");
                break;
            case 3://08代表客户绑定，且处于正常缴费期（状态3）
                String currentTime3;
                //服务到期时间
                String serviceStopTime3 = UserInfo.endstringtime.substring(0, 6);
                if (TextUtils.isEmpty(currentServiceTime)) {
                    //取手机时间
                    currentTime3 = BLECommandManager.getSystemTime().substring(0, 6);
                } else {
                    currentTime3 = currentServiceTime.substring(0, 6);
                }
                //服务到期时间
                if (!BoxStatusBean.serviceEndTimeBineary.equals(serviceStopTime3)) {
                    LogUtil.d("当前已绑定，但是服务器时间和保管箱时间不同步");
                    ToastTool.showShortBigToast(this, "正在同步保管箱到期时间，请稍等...");
                    LogUtil.d("密码是不是等于空的:服务器保存时间：读取保管箱的时间" + UserInfo.UserOpenBoxPassowrd + "：" + serviceStopTime3 + ":" + BoxStatusBean.serviceEndTimeBineary);
                    BLECommandManager.userBind(currentTime3, serviceStopTime3, UserInfo.BlueId, UserInfo.UserOpenBoxPassowrd, this, "01");
                    return;
                } else if (UserInfo.isOverDate) {
                    LogUtil.d("后台已过期，但是保管箱还可以正常使用就下发此条指令");
                    ToastTool.showShortBigToast(this, "正在同步保管箱到期时间，请稍等...");
                    BLECommandManager.userBind(currentTime3, serviceStopTime3, UserInfo.BlueId, UserInfo.UserOpenBoxPassowrd, this, "01");
                    return;
                }
                break;
            case 4://0x10代表客户绑定，但是服务已经到期。(状态4）
                String currentTime4;
                //服务到期时间
                String serviceStopTime4 = UserInfo.endstringtime.substring(0, 6);
                if (TextUtils.isEmpty(currentServiceTime)) {
                    //取手机时间
                    currentTime4 = BLECommandManager.getSystemTime().substring(0, 6);
                } else {
                    currentTime4 = currentServiceTime.substring(0, 6);
                }
                //如果后台的实时时间大于后台保管箱结束时间，则弹框出来缴费，否则续费。
                if (Integer.parseInt(currentTime4) > Integer.parseInt(serviceStopTime4)) {
                    //当前用户已经欠费
                    UserInfo.isOverDate = true;
                    QWApplication.mPreferences.edit().putBoolean("isOverDate", true).commit();
                    LogUtil.d("当前已欠费,弹出Dialog框给用户充值");
                    showEnterPayDialog();
                } else {
                    LogUtil.d("当前已欠费但是用户已经交钱,直接下发续费指令给用户充值");
                    ToastTool.showShortBigToast(this, "正在更新保管箱到期时间，请稍等...");
                    String endtime4 = UserInfo.endstringtime.substring(0, 6);
                    String startTime4 = UserInfo.starttime.substring(0, 6);
                    String password = UserInfo.UserOpenBoxPassowrd;
                    BLECommandManager.userBind(startTime4, endtime4, UserInfo.BlueId, password, this, "01");
                    return;
                }
                break;
        }
        //发送到解绑对话框
        ObservableBean obj = new ObservableBean();
        obj.setWhat(BleObserverConstance.DIALOG_USER_UNBIND_STATUS_CALLBACK);
        ObserverManager.getObserver().setMessage(obj);
    }

    /**
     * 读取保管箱里的记录
     */
    private long lastReadTime = 0;
    private boolean isReadBoxInfo = false;

    private void readBoxRecordInfo() {
        LogUtil.d("MainBoxActivity    readBoxRecordInfo");
        if (!BluetoothService.isConnected) {
            msgRecordPopwindow.requestRecoderInfo();
            return;
        }
        //先读取报警的信息
//        if (BoxStatusBean.recordAlarmCount > 0) {
//            BLECommandManager.UserReadBoxInfo(this, "FFFFFF", "90", false);
//        } else if(BoxStatusBean.recordOpenCount > 0){
        if (System.currentTimeMillis() - lastReadTime < 2000) {
            msgRecordPopwindow.requestRecoderInfo();
            return;
        }
        lastReadTime = System.currentTimeMillis();
        isReadBoxInfo = true;
        if (BoxStatusBean.recordOpenCount > 0) {
            BLECommandManager.UserReadBoxInfo(this, "FFFFFF", "91", false);
        } else {
            msgRecordPopwindow.requestRecoderInfo();
        }
    }

    @Override
    public void onClick(View v) {
        if (CommUtils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.rb_qr:// 设备绑定
                if (!CommUtils.isNetworkAvailable(this)) {
                    ToastTool.showShortBigToast(this, "无网络连接");
                    return;
                }
                if (UserInfo.UserBindState) {
                    if (TextUtils.isEmpty(UserInfo.BlueId)) {
                        ToastTool.showShortBigToast(this, "当前蓝牙ID为空");
                        return;
                    }
                    long closeKeyBoardTime = QWApplication.mPreferences.getLong("CloseKeyBoardTime", 0L);
                    if (System.currentTimeMillis() - closeKeyBoardTime < 10000) {
                        int span = 10000 - (int) (System.currentTimeMillis() - closeKeyBoardTime);
                        if (span > 1000) {
                            ToastTool.showLongBigToast(this, "保管箱键盘临时锁定中，请 " + span / 1000 + " 秒后重试");
                            return;
                        } else if (span > 500) {
                            ToastTool.showLongBigToast(this, "保管箱键盘临时锁定中，请 1 秒后重试");
                            return;
                        }
                    }
                    if (!BluetoothService.isConnected) {
                        ToastTool.showShortBigToast(this, "保管箱未连接");
                        return;
                    }
                    UnbindBoxDialogs dialogs = new UnbindBoxDialogs(this, new DialogCallBack() {
                        @Override
                        public void OkDown(Object obj) {
                            Intent intent = new Intent(MainBoxActivity.this, UnbindMipcaCaptureActivity.class);
                            startActivityForResult(intent, 2);
                        }

                        @Override
                        public void CancleDown() {
                        }
                    });
                    dialogs.show();
                } else {
                    // 扫码并访问后台用户是否可以绑定
                    try {
                        // 扫描二维码
                        Intent intent = new Intent(this, MipcaCaptureActivity.class);
                        startActivityForResult(intent, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.rb_pay:
//                if (!UserInfo.UserBindState) {
//                    ToastTool.showShortBigToast(this, "请先绑定设备");
//                    return;
//                }
                Intent packageSet = new Intent();
                packageSet.setClass(this, PackageMoneyActivity.class);
                packageSet.putExtra("boxID_a", UserInfo.BlueId);
                startActivityForResult(packageSet, 6);
                break;
            case R.id.rb_help:
                Intent intentHelp = new Intent(this, HelpCenterActivity.class);
                startActivity(intentHelp);
                break;
            case R.id.rb_setting:
                Intent setting = new Intent();
                setting.setClass(this, SettingActivity.class);
                startActivity(setting);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {//绑定成功
            if (UserInfo.UserBindState) {
                qr.setText("解绑");
                Drawable top = getResources().getDrawable(R.drawable.bottom_unbind);
                qr.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                BluetoothService.isScanning = false;
                if (bluetoothService != null && !BluetoothService.isConnected) {
                    bluetoothService.startBLEscan();
                }
            } else {
                qr.setText("扫码租箱");
                Drawable top = getResources().getDrawable(R.drawable.bottom_scan);
                qr.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                if (bluetoothService != null) {
                    bluetoothService.closeBT();
                    bluetoothService.startBLEscan();
                }
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {//绑定成功
            if (UserInfo.UserBindState) {
                qr.setText("解绑");
                Drawable top = getResources().getDrawable(R.drawable.bottom_unbind);
                qr.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                BluetoothService.isScanning = false;
                if (bluetoothService != null && !BluetoothService.isConnected) {
                    bluetoothService.startBLEscan();
                }
            } else {
                qr.setText("扫码租箱");
                Drawable top = getResources().getDrawable(R.drawable.bottom_scan);
                qr.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                if (bluetoothService != null) {
                    bluetoothService.closeBT();
                    bluetoothService.startBLEscan();
                }
            }
        } else if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                LogUtil.d("Acitivity全部关闭！！");
                AcitivityCollector.finishAll();
            } else if (resultCode == Activity.RESULT_OK) {
                if (bluetoothService != null && !BluetoothService.isScanning) {
                    bluetoothService.startBLEscan();
                }
            }
        } else if (requestCode == 6) {//从套餐入口回来
//            if (resultCode == PackageMoneyActivity.CARDNUMBER_PAY_SUCCESS || resultCode == PackageMoneyActivity.WEIXIN_ALIPAY_PAY_SUCCESS) {
//                if (CommUtils.isNetworkAvailable(this)) {
//                    // 请求获取当前服务器时间
//                    GetWebTimeReq req = new GetWebTimeReq();
//                    req.setNetCallback(this);
//                    req.setRequestType(Request.Method.POST);
//                    req.addRequest();
//                } else if (BluetoothService.isConnected) {
//                    BLECommandManager.BoxStateCheck(this);
//                }
//
//            }
        }
    }

    private void updateBoxState() {
        LogUtil.d("updateBoxState  electricValue=" + BoxStatusBean.electricValue);
        if (BoxStatusBean.electricValue.equals("0")) {
            btElectricIcon.setImageDrawable(getResources().getDrawable(R.mipmap.battery_0));
            btElectricValue.setText("0%");
        } else if (BoxStatusBean.electricValue.equals("10")) {
            btElectricIcon.setImageDrawable(getResources().getDrawable(R.mipmap.battery_1));
            btElectricValue.setText("10%");
        } else if (BoxStatusBean.electricValue.equals("20")) {
            btElectricIcon.setImageDrawable(getResources().getDrawable(R.mipmap.battery_1));
            btElectricValue.setText("20%");
        } else if (BoxStatusBean.electricValue.equals("30")) {
            btElectricIcon.setImageDrawable(getResources().getDrawable(R.mipmap.battery_1));
            btElectricValue.setText("30%");
        } else if (BoxStatusBean.electricValue.equals("40")) {
            btElectricIcon.setImageDrawable(getResources().getDrawable(R.mipmap.battery_2));
            btElectricValue.setText("40%");
        } else if (BoxStatusBean.electricValue.equals("50")) {
            btElectricIcon.setImageDrawable(getResources().getDrawable(R.mipmap.battery_2));
            btElectricValue.setText("50%");
        } else if (BoxStatusBean.electricValue.equals("60")) {
            btElectricIcon.setImageDrawable(getResources().getDrawable(R.mipmap.battery_3));
            btElectricValue.setText("60%");
        } else if (BoxStatusBean.electricValue.equals("70")) {
            btElectricIcon.setImageDrawable(getResources().getDrawable(R.mipmap.battery_4));
            btElectricValue.setText("70%");
        } else if (BoxStatusBean.electricValue.equals("80")) {
            btElectricIcon.setImageDrawable(getResources().getDrawable(R.mipmap.battery_4));
            btElectricValue.setText("80%");
        } else if (BoxStatusBean.electricValue.equals("90")) {
            btElectricIcon.setImageDrawable(getResources().getDrawable(R.mipmap.battery_4));
            btElectricValue.setText("90%");
        } else if (BoxStatusBean.electricValue.equals("100")) {
            btElectricIcon.setImageDrawable(getResources().getDrawable(R.mipmap.battery_normal));
            btElectricValue.setText("100%");
        }
    }


    private void handBoxAlarmInfo(String BlueId, String data) {
        //当前网络是否有网，如果有网就把报警数据给后台否则就直接给保管箱
        int actionType = checkAlarmLevel(data);
        boxWarnShortTime = data.substring(8, 14);
        if (CommUtils.isNetworkAvailable(this)) {
            LogUtil.d("当前有网发送报警数据给后台");
            BoxAlarmUploadReq boxWarnUploadReq = new BoxAlarmUploadReq();
            boxWarnUploadReq.setNetCallback(this);
            boxWarnUploadReq.setRequestType(Request.Method.POST);
            boxWarnUploadReq.setBarcode(BlueId);
            boxWarnUploadReq.setDate(data.substring(2));
            boxWarnUploadReq.setActionType(actionType + "");
            boxWarnUploadReq.addRequest();
        } else {
            LogUtil.d("当前没网发送报警数据给保管箱");
            BLECommandManager.HandleBoxActiveRecordData(this, "02A0", boxWarnShortTime);
        }
    }

    private int checkAlarmLevel(String data) {
        String s = "";
        int type = 0;
        String level = data.substring(0, 2);
        if (level.contains("1")) {
            s = "收到轻微震动报警";
            type = 8;
        } else if (level.contains("2")) {
            s = "收到较强震动报警";
            type = 9;
        } else if (level.contains("3")) {
            s = "收到强烈震动报警";
            type = 10;
        }
        String checkString = CommUtils.parseOpentimeToDate(s, data.substring(2, data.length()));
        msgAdapter.addData(checkString);
        recorderhistory.setVisibility(View.GONE);
        return type;
    }

    private void HandlBoxAlarmRecoder(String data, String time, int boxSurplus, int recordCount) {
        /**
         * 这里需要传送报警数据给服务器。
         */
        ArrayList<String> boxAlarmInfo = new ArrayList<String>();
        int start = 28;
        for (int i = 1; i <= recordCount; i++) {
            LogUtil.d("保管箱的报警数据：" + data.substring(start, start + 14));
            boxAlarmInfo.add(data.substring(start, start + 14));
            start = start + 14;
        }
        LogUtil.d("要显示给用户看的报警数据：" + boxAlarmInfo.toString());
        // 有网上传服务器，没网则再读一包开关箱数据
        if (CommUtils.isNetworkAvailable(this)) {
            UploadAlarmOrOpenBoxDataReq req = new UploadAlarmOrOpenBoxDataReq();
            req.setNetCallback(this);
            req.setRequestType(Request.Method.POST);
            req.setBlueId(UserInfo.BlueId);
            req.setList(boxAlarmInfo);
            req.setType(UploadAlarmOrOpenBoxDataReq.UpdateType.ALARM);
            req.addRequest();
        } else {
            //读开关箱数据
            if (BoxStatusBean.recordOpenCount > 0) {
                BLECommandManager.UserReadBoxInfo(this, "FFFFFF", "91", false);
            }
        }
    }

    private void HandleBoxOpenRecorder(String data, String time, int boxSurplus, int recordCount) {
        ArrayList<String> boxOpenInfo = new ArrayList<String>();
        int start = 28;
        for (int i = 1; i <= recordCount; i++) {
            LogUtil.d("保管箱的开箱数据：" + data.substring(start, start + 14));
            boxOpenInfo.add(data.substring(start, start + 14));
            start = start + 14;
        }
        LogUtil.d("要显示给用户看的开关箱数据：" + boxOpenInfo.toString());
        // 有网则继续没网则直接回02给保管箱
        if (CommUtils.isNetworkAvailable(this)) {
            UploadAlarmOrOpenBoxDataReq req = new UploadAlarmOrOpenBoxDataReq();
            req.setNetCallback(this);
            req.setRequestType(Request.Method.POST);
            req.setBlueId(UserInfo.BlueId);
            req.setList(boxOpenInfo);
            req.setType(UploadAlarmOrOpenBoxDataReq.UpdateType.OPENBOX);
            req.addRequest();
        } else {
            BLECommandManager.SendErrorRecordInfo(UserInfo.BlueId, this);
        }
    }

    /**
     * 收到开箱信息时 处理
     */
    private void UserOpenRecordInfo(String data) {
        String BlueId = data.substring(4, 12);
        String info = data.substring(16, 18);
        String time = data.substring(18, 30);
        String openboxTime = data.substring(24, 30);
        int count = Integer.parseInt(info);
        switch (count) {
            case 11:
                setReceivInfo("密码键盘开箱成功", time);
                // 发送请求
                if (CommUtils.isNetworkAvailable(this)) {
                    OpenBoxUploadReq req = new OpenBoxUploadReq();
                    req.setNetCallback(this);
                    req.setRequestType(Request.Method.POST);
                    req.setActionType("1");
                    req.setBarcode(BlueId);
                    req.setDate(time);
                    req.addRequest();
                } else {
                    BLECommandManager.HandleBoxActiveRecordData(this, "02A1", openboxTime);
                }
                break;
            case 12:
                setReceivInfo("密码键盘开箱堵转", time);
                break;
            case 13:
                setReceivInfo("密码键盘开箱超时", time);
                break;
            case 21:
                setReceivInfo("蓝牙指令开箱成功", time);
                break;
            case 22:
                setReceivInfo("蓝牙指令开箱堵转", time);
                break;
            case 23:
                setReceivInfo("蓝牙指令开箱超时", time);
                break;
            case 31:
                setReceivInfo("被动开箱成功", time);
                // 发送请求
                if (CommUtils.isNetworkAvailable(this)) {
                    OpenBoxUploadReq req = new OpenBoxUploadReq();
                    req.setNetCallback(this);
                    req.setRequestType(Request.Method.POST);
                    req.setActionType("24");
                    req.setBarcode(BlueId);
                    req.setDate(time);
                    req.addRequest();
                } else {
                    BLECommandManager.HandleBoxActiveRecordData(this, "02A1", openboxTime);
                }
                break;
            case 32:
                setReceivInfo("被动开箱堵转", time);
                break;
            case 33:
                setReceivInfo("被动开箱超时", time);
                break;
            default:
                break;
        }
        this.openboxTime = openboxTime;
    }

    /**
     * 收到关箱动作时 处理
     */
    private void UserCloseRecordInfo(String data) {
        String blueId = data.substring(4, 12);
        String info = data.substring(16, 18);
        String time = data.substring(18, 30);
        String shortTime = data.substring(24, 30);
        int count = Integer.parseInt(info);
        switch (count) {
            case 44:
                setReceivInfo("关箱成功", time);
                this.closeBoxTime = shortTime;
                // 发送请求
                if (CommUtils.isNetworkAvailable(this)) {
                    CloseBoxUploadReq req = new CloseBoxUploadReq();
                    req.setNetCallback(this);
                    req.setRequestType(Request.Method.POST);
                    req.setActionType("7");
                    req.setBarcode(blueId);
                    req.setDate(time);
                    req.addRequest();
                } else {
                    BLECommandManager.HandleBoxActiveRecordData(this, "02A4", shortTime);
                }
                break;
            case 55:
                setReceivInfo("关箱堵转", time);
                if (CommUtils.isNetworkAvailable(this)) {
                    CloseBoxUploadReq req = new CloseBoxUploadReq();
                    req.setNetCallback(this);
                    req.setRequestType(Request.Method.POST);
                    req.setActionType("6");
                    req.setBarcode(blueId);
                    req.setDate(time);
                    req.addRequest();
                }
                break;
            case 66:
                setReceivInfo("关箱超时", time);
                break;
            default:
                break;
        }
    }

    private void UserRechargeInfo(String data) {
        String blueId = data.substring(4, 12);
        String info = data.substring(16, 18);
        String time = data.substring(18, 30);
        int count = Integer.parseInt(info);
        switch (count) {
            case 00:
                setReceivInfo("充电线已移除", time);
                break;
            case 01:
                setReceivInfo("正在充电", time);
                break;
            case 02:
                setReceivInfo("当前已充满，请移除电源线", time);
                break;
            default:
                break;
        }
    }

    private void setReceivInfo(String string, String openTime) {
        String checkString = CommUtils.parseOpentimeToDate(string, openTime);
        msgAdapter.addData(checkString);
        msgRecordPopwindow.addData(checkString);
        recorderhistory.setVisibility(View.GONE);
    }

    @Override
    public void update(Observable arg0, Object obj) {
        ObservableBean ob = (ObservableBean) obj;
        switch (ob.getWhat()) {
            case BleObserverConstance.ACTION_ADD_MSG:
                if (ob.getObject() == null) {
                    return;
                }
                String content = ob.getObject().toString();
                if (!TextUtils.isEmpty(content)) {
                    msgAdapter.addData(content);
                    msgRecordPopwindow.addData(content);
                    recorderhistory.setVisibility(View.GONE);
                }
                break;
            case BleObserverConstance.BOX_CONNTECING_BLE:
                LogUtil.d("  BOX_CONNTECING_BLE  ");
                //创建旋转动画
                btStatus.setImageResource(R.mipmap.bluetooth_connecting);
                Animation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setFillAfter(true); // 设置保持动画最后的状态
                anim.setDuration(500); // 设置动画时间
                anim.setRepeatCount(-1);
                anim.setInterpolator(new LinearInterpolator()); // 设置插入器
                btStatus.startAnimation(anim);
                break;
            case BleObserverConstance.BOX_CONNTEC_BLE_STATUS:
                LogUtil.d("update  BOX_CONNTEC_BLE_STATUS  btStatus=" + ob.getObject());
                if ((boolean) ob.getObject()) {
                    btStatus.setImageResource(R.mipmap.bluetooth_connect);
                    ToastTool.showShortBigToast(this, "蓝牙连接成功");
                } else {
                    btStatus.setImageResource(R.mipmap.bluetooth_disconnect);
                    ToastTool.showShortBigToast(this, "蓝牙断开连接");
                    if (!isReadBoxInfo && msgRecordPopwindow.isShowing()) {
                        readBoxRecordInfo();
                    }
                }
                btStatus.clearAnimation();
                if (UserInfo.lock_style.equals("a")) {
                    lockStyle = "a";
                    ((HomeGuestureUnlockBoxFragment) mFragments[1]).onResume();
                } else {
                    lockStyle = "c";
                    ((HomeNumberUnlockBoxFragment) mFragments[0]).onResume();
                }
                break;
            case BleObserverConstance.LOGIN_SINGLE_STOP_ACTION:
                showSingleLoginDialog();
                break;
            case BleObserverConstance.BOX_QUERY_STATUS_CALLBACK://查询保管箱状态的回调
                updateBoxState();
                if (UserInfo.UserBindState) {
                    analyticStateData();
                } else {

                }
                break;

            case BleObserverConstance.BOX_USER_BIND_RESULT://注意 修改开箱密码也是这个指令 注意区分
                if ((boolean) ob.getObject()) {
                    UserInfo.UserBindState = true;
                    LogUtil.d("MainBoxActivity 绑定指令生效");
                } else {
                    UserInfo.UserBindState = false;
                    LogUtil.d("MainBoxActivity 绑定指令失败");
                }
                break;

            case BleObserverConstance.ACTION_GATT_CONNECT_SUCCESS:
                // 发送18指令查询保管箱状态
                if (CommUtils.isNetworkAvailable(this)) {
                    // 请求获取当前服务器时间
                    GetWebTimeReq req = new GetWebTimeReq();
                    req.setNetCallback(this);
                    req.setRequestType(Request.Method.POST);
                    req.addRequest();
                } else {
                    if (BluetoothService.isConnected) {
                        BLECommandManager.BoxStateCheck(this);
                    } else {
                        ToastTool.showShortBigToast(this, "当前蓝牙未连接");
                    }
                }
                break;
            case BleObserverConstance.BOX_RECEIVER_READINFO:
                try {
                    String data = ob.getObject().toString();
                    String info = data.substring(12, 14);
                    // 时间因子
                    boxTime = data.substring(16, 22);
                    // 代表数据类型
                    String dataType = data.substring(22, 24);
                    // 保管箱剩余的条数
                    boxSurplus = BlueDeviceUtils.hexStringToInteger(data.substring(24, 26));
                    // 代表本次包含多少条报警数据
                    int recordCount = BlueDeviceUtils.hexStringToInteger(data.substring(26, 28));
                    LogUtil.d("本次包含的报警或开箱剩余数据  boxSurplus=" + boxSurplus + " , recordCount=" + recordCount);
                    if (info.equals("01")) {
                        if (dataType.equals("90")) {
                            if (recordCount > 0) {
                                LogUtil.d("本次包含的报警数据：" + recordCount);
                                HandlBoxAlarmRecoder(data, boxTime, boxSurplus, recordCount);
                            } else {
                                msgRecordPopwindow.requestRecoderInfo();
                            }
                        } else if (dataType.equals("91")) {
                            if (recordCount > 0) {
                                LogUtil.d("本次包含的开关箱数据：" + recordCount);
                                HandleBoxOpenRecorder(data, boxTime, boxSurplus, recordCount);
                            } else {
                                msgRecordPopwindow.requestRecoderInfo();
                            }
                        }
                    } else if (info.equals("02")) {
                        LogUtil.d("BOX_RECEIVER_READINFO 时间因子错误，查询失败");
                    } else if (info.equals("03")) {
                        LogUtil.d("BOX_RECEIVER_READINFO 数据类型错误，查询失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case BleObserverConstance.RECEIVER_BOX_DATA_ALARM:// 接收报警数据
                String data = ob.getObject().toString();
                LogUtil.d("接收报警数据， data=" + data);
                if (!UserInfo.UserBindState) {
                    return;
                }
                if (CommUtils.isAppIsInBackground(this)) {
                    //当前程序正在后台，需要推送消息到桌面给用户查看
                    LogUtil.d("接收报警数据， 应用处于后台，准备推送消息给用户");
                    Intent intent = new Intent(this, MainBoxActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    Notification notification = new Notification.Builder(this)
                            //触摸之后立即取消
                            .setAutoCancel(true)
                            //显示的时间
                            .setWhen(System.currentTimeMillis())
                            //设置通知的小图标
                            .setSmallIcon(R.mipmap.img_5)
                            //设置状态栏显示的文本
                            .setTicker("您有一条新的报警记录，请注意查收")
                            //设置通知的标题
                            .setContentTitle("湖南青之翼信息技术有限公司")
                            //设置通知的内容
                            .setContentText("您收到了一条保管箱报警信息")
                            //向通知添加声音等效果。
                            .setDefaults(Notification.DEFAULT_ALL)
                            //设置跳转的activity
                            .setContentIntent(pendingIntent).build();
                    NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mManager.notify(1, notification);
                }
                handBoxAlarmInfo(data.substring(4, 12), data.substring(16, 30));
                break;
            case BleObserverConstance.RECEIVER_BOX_DATA_OPENBOX:// 接收到用户在保管箱开箱数据
                // 判断是否在解除绑定的过程中。若是则收到开箱指令直接退出解绑流程
                LogUtil.d("接收到用户在保管箱开箱数据 --");
                if (!UserInfo.UserBindState) {
                    return;
                }
                UserOpenRecordInfo(ob.getObject().toString());
                break;
            case BleObserverConstance.RECEIVER_BOX_DATA_CLOSEBOX:// 接收到用户关保管箱数据
                LogUtil.d("保管箱关箱成功了");
                if (!UserInfo.UserBindState) {
                    return;
                }
                UserCloseRecordInfo(ob.getObject().toString());
                break;
            case BleObserverConstance.RECEIVER_BOX_DATA_RECHARGE:// 接收到插入充电线主动上传APP
                UserRechargeInfo(ob.getObject().toString());
                break;
        }
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof GetWebTimeResponse) {
            // 得到当前时间判断是否发送0018或者0118
            GetWebTimeResponse gwtr = (GetWebTimeResponse) baseRes;
            String status = gwtr.getStatus();
            String message = gwtr.getMessage();
            String time = gwtr.getDataMap().getSeverTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = sdf.parse(time);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
                currentServiceTime = dateFormat.format(date);
                //服务器当前时间：服务器后台时间170515195548:
                //执行保管箱状态查询 并同步蓝牙系统时间
                LogUtil.d("服务器当前时间：服务器后台时间" + currentServiceTime + ":" + UserInfo.endstringtime);

                if (status.equals("success")) {// 成功则下发能更新时间的18指令
                    if (BluetoothService.isConnected) {
                        BLECommandManager.queryBoxStateSetBleDate(this, currentServiceTime);
                    } else {
                        ToastTool.showShortBigToast(this, "当前蓝牙未连接");
                    }
                } else {
                    if (BluetoothService.isConnected) {
                        BLECommandManager.BoxStateCheck(this);
                    } else {
                        ToastTool.showShortBigToast(this, "当前蓝牙未连接");
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else if (baseRes instanceof UploadAlarmDataResponse) {
            UploadAlarmDataResponse alarmDataResponse = (UploadAlarmDataResponse) baseRes;
            int statusCode = alarmDataResponse.getStatusCode();
            if (statusCode == 200) {
                LogUtil.d("报警信息发送服务器成功");
                if (boxSurplus > 0) {
                    BLECommandManager.UserReadBoxInfo(this, boxTime, "90", false);
                } else {
                    BLECommandManager.UserReadBoxInfo(this, boxTime, "90", true);
                    //可以同时发 服务做了间隔发送指令操作
                    if (BoxStatusBean.recordOpenCount != 0) {//读取开关箱记录的
                        BLECommandManager.UserReadBoxInfo(MainBoxActivity.this, "FFFFFF", "91", false);
                    } else {
                        LogUtil.d("读取保管箱记录信息完成！！");
                        return;
                    }
                }
            } else {
                LogUtil.d("报警数据上传服务器失败");
            }
        } else if (baseRes instanceof UploadOpenBoxDataResponse) {
            UploadOpenBoxDataResponse uploadOpenBoxDataResponse = (UploadOpenBoxDataResponse) baseRes;
            int statusCode = uploadOpenBoxDataResponse.getStatusCode();
            if (statusCode == 200) {
                LogUtil.d("开关箱信息发送服务器成功  boxSurplus=" + boxSurplus);
                if (boxSurplus > 0) {
                    BLECommandManager.UserReadBoxInfo(this, boxTime, "91", false);
                } else {
                    LogUtil.d("读取完所有的信息了，用户可以操作其他指令了！！");
                    BLECommandManager.UserReadBoxInfo(this, boxTime, "91", true);
                    for (int i = 0; i < 100; i++) {
                    }
                    //要重新查询状态
                    BLECommandManager.BoxStateCheck(this);
                    msgRecordPopwindow.requestRecoderInfo();
                }
            } else {
                LogUtil.d("开关箱数据上传服务器失败");
                msgRecordPopwindow.requestRecoderInfo();
            }
        } else if (baseRes instanceof BoxAlarmUploadResponse) {
            BoxAlarmUploadResponse boxWarnUploadResponse = (BoxAlarmUploadResponse) baseRes;
            String status = boxWarnUploadResponse.getStatus();
            if (!TextUtils.isEmpty(status) && status.equals("success")) {
                LogUtil.d("传送报警数据给服务器成功");
                BLECommandManager.HandleBoxActiveRecordData(this, "01A0", boxWarnShortTime);
            } else {
                LogUtil.d("传送报警数据给服务器失败");
                BLECommandManager.HandleBoxActiveRecordData(this, "02A0", boxWarnShortTime);
            }
        } else if (baseRes instanceof OpenBoxUploadResponse) {
            OpenBoxUploadResponse obur = (OpenBoxUploadResponse) baseRes;
            String status = obur.getStatus();
            if (!TextUtils.isEmpty(status) && status.equals("success")) {
                BLECommandManager.HandleBoxActiveRecordData(this, "01A1", openboxTime);
            } else {
                BLECommandManager.HandleBoxActiveRecordData(this, "02A1", openboxTime);
            }
        }
        if (baseRes instanceof CloseBoxUploadResponse) {
            CloseBoxUploadResponse cbur = (CloseBoxUploadResponse) baseRes;
            String status = cbur.getStatus();
            String message = cbur.getMessage();
            if (!TextUtils.isEmpty(status) && status.equals("success")) {
                BLECommandManager.HandleBoxActiveRecordData(this, "01A4", closeBoxTime);
            } else {
                BLECommandManager.HandleBoxActiveRecordData(this, "02A4", closeBoxTime);
            }
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                LogUtil.d("广播传过来的action：" + action);
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                    LogUtil.d("监听到的蓝牙状态变化 state=" + state);
                    switch (state) {
                        case BluetoothAdapter.STATE_TURNING_OFF://手机蓝牙正在关闭
                            if (bluetoothService != null) {
                                bluetoothService.closeBT();
                            }
                            break;
                        case BluetoothAdapter.STATE_OFF://手机蓝牙关闭
                            LogUtil.d("某个应用想要打开蓝牙!!!");
                            ObservableBean ob = new ObservableBean();
                            ob.setWhat(BleObserverConstance.BT_OFF_HAND_ACTION);
                            ObserverManager.getObserver().setMessage(ob);
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            break;
                        case BluetoothAdapter.STATE_ON:
                            LogUtil.d("打开蓝牙!!!");
                            if (bluetoothService != null) {
                                bluetoothService.startBLEscan();
                            }
                            break;
                    }
                }// 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。.
                // 最好用的还是这个监听。wifi如果打开，关闭，以及连接上可用的连接都会接到监听。见log
                // 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
                else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                    if (CommUtils.isNetworkAvailable(context)) { // connected to the internet
                        LogUtil.d("CONNECTIVITY_ACTION 网络连接已经打开");
                    } else {   // not connected to the internet
                        LogUtil.d("CONNECTIVITY_ACTION 当前没有网络连接，请确保你已经打开网络 ");
                        ObservableBean ob = new ObservableBean();
                        ob.setWhat(BleObserverConstance.NETWORK_OFF_HAND_ACTION);
                        ObserverManager.getObserver().setMessage(ob);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private BluetoothService bluetoothService;
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothService.MyBinder binder = (BluetoothService.MyBinder) service;
            bluetoothService = binder.getService();
            LogUtil.d("onServiceConnected -------------------------");
            bluetoothService.startBLEscan();
        }

        //client 和service连接意外丢失时，会调用该方法
        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d("onServiceDisconnected -----------------------------");
        }
    };

}
