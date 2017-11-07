package com.qingwing.safebox.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.volley.Request;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.OpenBoxRecoder;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.bluetooth.BLECommandManager;
import com.qingwing.safebox.bluetooth.BleObserverConstance;
import com.qingwing.safebox.bluetooth.BluetoothService;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.OpenBoxUploadReq;
import com.qingwing.safebox.net.response.OpenBoxUploadResponse;
import com.qingwing.safebox.observable.ObservableBean;
import com.qingwing.safebox.observable.ObserverManager;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.ToastTool;
import com.qingwing.safebox.view.LocusPassWordView;

import java.util.Observable;
import java.util.Observer;

public class HomeGuestureUnlockBoxFragment extends Fragment implements Observer, NetCallBack {
    // 振动器
    private Vibrator vibrator;
    private LocusPassWordView mLocusPassWordView;
    private TextView tishiyu;
    // 输入密码后提示信息
    private ProgressDialog dialog;
    /**
     * 密码输入错误提示
     */
    private TimeCount time;
    private int openType = 4;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    tishiyu.setText(msg.obj.toString());
                    tishiyu.setTextColor(Color.BLACK);
                    break;
                case 2:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_number_unlockbox, null);
        dialog = new ProgressDialog(getActivity());
        mLocusPassWordView = (LocusPassWordView) view
                .findViewById(R.id.mLocusPassWordView);
        tishiyu = (TextView) view.findViewById(R.id.tishiyu);
        ObserverManager.getObserver().addObserver(this);
        mLocusPassWordView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!UserInfo.UserBindState) {
                        tishiyu.setText("未绑定设备");
                        tishiyu.setTextColor(Color.RED);
                        return true;
                    }
                    if (UserInfo.isOverDate) {
                        tishiyu.setText("服务已到期");
                        tishiyu.setTextColor(Color.RED);
                        return true;
                    }
                    if (!BluetoothService.isConnected) {
                        tishiyu.setText("设备未连接");
                        tishiyu.setTextColor(Color.RED);
                        return true;
                    }
                    if (QWApplication.openboxErrorCount >= 5) {
                        tishiyu.setText("密码错误次数过多,请" + QWApplication.openboxErrorMillis / 1000 + "秒后再试");
                        tishiyu.setTextColor(Color.RED);
                        if (time == null) {
                            time = new TimeCount(QWApplication.openboxErrorMillis, 1000);
                            time.start();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        mLocusPassWordView.setOnCompleteListener(new LocusPassWordView.OnCompleteListener() {

            @Override
            public void onComplete(String password) {
                //判断密码是否正确
                if (mLocusPassWordView.verifyPassword(password)) {
                    //正确则执行开箱操作
                    openBox();
                    mHandler.removeMessages(2);
                    mHandler.sendEmptyMessageDelayed(2, 5000);
                    mLocusPassWordView.enableTouch();
                    mLocusPassWordView.clearPassword(2200);
                } else {
                    QWApplication.openboxErrorCount = QWApplication.openboxErrorCount + 1;
                    QWApplication.mPreferences.edit().putInt("openboxErrorCount", QWApplication.openboxErrorCount).commit();
                    QWApplication.openboxErrorMillis = 180000;
                    QWApplication.mPreferences.edit().putLong("openboxErrorMillis", QWApplication.openboxErrorMillis).commit();
                    if (5 == QWApplication.openboxErrorCount) {
                        time = new TimeCount(QWApplication.openboxErrorMillis, 1000);
                        time.start();
                        mLocusPassWordView.enableTouch();
                        mLocusPassWordView.clearPassword(1000);
                        openType = 5;
                        // 发送请求
                        requestService(OpenBoxRecoder.timeFactor, OpenBoxRecoder.openTime);
                    } else {
                        mLocusPassWordView.enableTouch();
                        //变红并在一秒后清空手势密码
                        mLocusPassWordView.markError();
                        mLocusPassWordView.clearPassword(1000);
                        ObservableBean ob = new ObservableBean();
                        ob.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                        ob.setObject("APP手势开箱密码错误");
                        ObserverManager.getObserver().setMessage(ob);
                        tishiyu.setText("密码错误,错误" + (5 - QWApplication.openboxErrorCount) + "次后将锁定三分钟");
                        tishiyu.setTextColor(Color.RED);
//                        ToastTool.showLongBigToast(getActivity(), "手势绘制错误,错误" + (5 - QWApplication.openboxErrorCount) + "次后将锁屏三分钟");
                        /*
                         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
						 */
                        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        long[] pattern = {100, 400}; // 停止 开启
                        vibrator.vibrate(pattern, -1); // 重复两次上面的pattern
                        // 如果只想震动一次，index设为-1
                        // 红色字体抖动效果
                        shakeAnimation();
                        // 开锁后提示信息还原  进行提示信息的重置
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = "请绘制解锁手势";
                        mHandler.sendMessage(msg);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!UserInfo.UserBindState) {
            tishiyu.setText("未绑定设备");
            tishiyu.setTextColor(Color.RED);
            return;
        }
        if (UserInfo.isOverDate) {
            tishiyu.setText("服务已到期");
            tishiyu.setTextColor(Color.RED);
            return;
        }
        if (!BluetoothService.isConnected) {
            tishiyu.setText("设备未连接");
            tishiyu.setTextColor(Color.RED);
            return;
        } else {
            tishiyu.setText("请绘制开箱手势");
            tishiyu.setTextColor(Color.BLACK);
        }
        if (QWApplication.openboxErrorCount >= 5) {
            time = new TimeCount(QWApplication.openboxErrorMillis, 1000);
            time.start();
        }
    }

    private void openBox() {
        if (!UserInfo.UserBindState) {
            ToastTool.showLongBigToast(getActivity(), "请先绑定设备在开箱");
            return;
        }
        if (!BluetoothService.isConnected) {
            mLocusPassWordView.clearPassword();
            ToastTool.showLongBigToast(getActivity(), "您当前需要连接设备才能开箱，是否连接？");
            return;
        } else if (!TextUtils.isEmpty(UserInfo.BlueId)) {
            //密码正确则重置错误次数
            if (QWApplication.openboxErrorCount != 0) {
                QWApplication.openboxErrorCount = 0;
                QWApplication.mPreferences.edit().putInt("openboxErrorCount", 0).commit();
            }
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("正在开箱,请稍后...");
            dialog.setIndeterminate(true);
            dialog.show();
            BLECommandManager.UserOpen(getActivity(), addZero(UserInfo.UserOpenBoxPassowrd));
        } else {
            ToastTool.showShortBigToast(getActivity(), "当前蓝牙未连接");
        }
    }

    private String addZero(String mima) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mima.length(); i++) {
            sb.append("0" + mima.charAt(i));
        }
        LogUtil.d("addZero UserOpenBoxPassowrd 密码=" + sb.toString());
        return sb.toString();
    }

    /**
     * 上传服务器
     */
    private void requestService(String timeFactor, String openTime) {
        LogUtil.d("requestService执行了");
        if (CommUtils.isNetworkAvailable(getActivity())) {
            OpenBoxUploadReq req = new OpenBoxUploadReq();
            req.setNetCallback(this);
            req.setRequestType(Request.Method.POST);
            req.setActionType(openType + "");
            req.setBarcode(UserInfo.BlueId);
            req.setDate(openTime);
            req.addRequest();
        } else {
            BLECommandManager.UserOpenSure("0224", getActivity(), timeFactor);
        }
    }

    /**
     * 红色字体抖动动画类
     */
    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        tishiyu.startAnimation(shake);

    }

    public void connectSucces(){
        tishiyu.setText("设备已连接");
        tishiyu.setTextColor(Color.BLACK);
    }

    @Override
    public void onDestroyView() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        ObserverManager.getObserver().deleteObserver(this);
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof OpenBoxUploadResponse) {
            OpenBoxUploadResponse obur = (OpenBoxUploadResponse) baseRes;
            String status = obur.getStatus();
            dialog.dismiss();
            dialog.setCancelable(true);
            if (status.equals("success")) {
            } else {
            }
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        dialog.dismiss();
        dialog.setCancelable(true);
    }

    @Override
    public void update(Observable observable, Object obj) {
        ObservableBean ob = (ObservableBean) obj;
        switch (ob.getWhat()) {
            case BleObserverConstance.BOX_USER_OPENBOX_STATUS_HAND:
                dialog.dismiss();
                if (OpenBoxRecoder.operationResult.equals("21")) {
                    LogUtil.d("HomeGuestureUnlockBoxFragment  21开箱成功");
                    //开箱成功
//                    ToastTool.showLongBigToast(getActivity(), "开箱成功");
                    tishiyu.setText("开箱成功");
                    tishiyu.setTextColor(Color.RED);
                    // 开锁后提示信息还原
                    // 进行提示信息的重置
//                    tishiyu.setText("请绘制解锁手势");
//                    tishiyu.setTextColor(Color.BLACK);
                    ObservableBean msg = new ObservableBean();
                    msg.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                    msg.setObject("App验证手势开箱成功" + "\n" + CommUtils.parseOpentimeToDate(OpenBoxRecoder.openTime));
                    ObserverManager.getObserver().setMessage(msg);
                    openType = 4;
                    // 发送请求
                    requestService(OpenBoxRecoder.timeFactor, OpenBoxRecoder.openTime);
                } else if (OpenBoxRecoder.operationResult.equals("22")) {
                    //使用App开箱堵转了
                    ToastTool.showLongBigToast(getActivity(), "App手势开箱堵转");
//                    tishiyu.setText("开箱堵转");
//                    tishiyu.setTextColor(Color.RED);
                    // 进行提示信息的重置
//                    tishiyu.setText("请绘制解锁手势");
//                    tishiyu.setTextColor(Color.BLACK);
                    ObservableBean msg = new ObservableBean();
                    msg.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                    msg.setObject("App开箱堵转" + "\n" + CommUtils.parseOpentimeToDate(OpenBoxRecoder.openTime));
                    ObserverManager.getObserver().setMessage(msg);
                    BLECommandManager.UserOpenSure("0124", getActivity(), OpenBoxRecoder.timeFactor);
                } else if (OpenBoxRecoder.operationResult.equals("23")) {
                    //使用App开箱检测连接超时
                    ToastTool.showLongBigToast(getActivity(), "App开箱检测连接超时");
                    // 进行提示信息的重置
                    tishiyu.setText("请绘制解锁手势");
                    tishiyu.setTextColor(Color.BLACK);
                    ObservableBean msg = new ObservableBean();
                    msg.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                    msg.setObject("App手势开箱检测连接超时" + "\n" + CommUtils.parseOpentimeToDate(OpenBoxRecoder.openTime));
                    ObserverManager.getObserver().setMessage(msg);
                    BLECommandManager.UserOpenSure("0124", getActivity(), OpenBoxRecoder.timeFactor);
                } else if (OpenBoxRecoder.operationResult.equals("08")) {
                    //保管箱已打开
//                    ToastTool.showLongBigToast(getActivity(), "箱门已打开");
                    tishiyu.setText("箱门已打开");
                    tishiyu.setTextColor(Color.RED);
                    // 进行提示信息的重置
//                    tishiyu.setText("请绘制解锁手势");
//                    tishiyu.setTextColor(Color.BLACK);
                    BLECommandManager.UserOpenSure("0124", getActivity(), OpenBoxRecoder.timeFactor);
                }
                break;
            case BleObserverConstance.RECEIVER_BOX_DATA_CLOSEBOX:
                tishiyu.setText("请绘制解锁手势");
                tishiyu.setTextColor(Color.BLACK);
                break;
        }
    }

    // 密码错误后再次输入密码的倒计时
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            //3分钟之后重置错误次数
            QWApplication.openboxErrorCount = 0;
            QWApplication.mPreferences.edit().putInt("openboxErrorCount", 0).putLong("openboxErrorMillis", 0L).commit();
            //计时完成后进行信息重置
            tishiyu.setText("请绘制解锁手势");
            tishiyu.setTextColor(Color.BLACK);
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程中
            QWApplication.openboxErrorMillis = millisUntilFinished;
            QWApplication.mPreferences.edit().putLong("openboxErrorMillis", millisUntilFinished).commit();
            //提示用户看
            tishiyu.setText("密码错误次数过多,请" + QWApplication.openboxErrorMillis / 1000 + "秒后再试");
            tishiyu.setTextColor(Color.RED);
        }
    }
}
