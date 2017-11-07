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
import android.view.View.OnClickListener;
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

import java.util.Observable;
import java.util.Observer;

public class HomeNumberUnlockBoxFragment extends Fragment implements OnClickListener, OnTouchListener, Observer, NetCallBack {
    private TextView count_1, count_2, count_3, count_4, count_5, count_6, count_7, count_8, count_9, count_0, Delete;
    private StringBuffer sb = new StringBuffer();
    // 六位密码的留个textview控件
    private TextView box1;
    private TextView box2;
    private TextView box3;
    private TextView box4;
    private TextView box5;
    private TextView box6;
    // 会随情况更改的提示信息。默认是请输入密码；
    private TextView tishi;
    // 输入密码后提示信息
    private ProgressDialog dialog;
    private Vibrator vibrator;
    //开箱类型
    private int openType;
    /**
     * 开箱密码次数超过5次 锁定180秒后才可继续
     */
    private TimeCount180 time180;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    tishi.setText(msg.obj.toString());
                    tishi.setTextColor(Color.BLACK);
                    break;
                case 1:// 进行密码置空
                    if (getActivity() != null) {
                        if (sb.length() != 0) {
                            sb.delete(0, sb.length());
                            updateUi();
                        }
                    }
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_number_unlock_box, null);
        initView(view);
        ObserverManager.getObserver().addObserver(this);
        dialog = new ProgressDialog(getActivity());
        // 判断用户是否绑定保管箱
        if (!UserInfo.UserBindState) {
            tishi.setText("设备未绑定");
            tishi.setTextColor(Color.RED);
        }
        return view;
    }

    private void initView(View view) {
        LogUtil.d("HomeNumberUnlockBoxFragment initView");
        Delete = (TextView) view.findViewById(R.id.delete);
        count_1 = (TextView) view.findViewById(R.id.tv_1);
        count_2 = (TextView) view.findViewById(R.id.tv_2);
        count_3 = (TextView) view.findViewById(R.id.tv_3);
        count_4 = (TextView) view.findViewById(R.id.tv_4);
        count_5 = (TextView) view.findViewById(R.id.tv_5);
        count_6 = (TextView) view.findViewById(R.id.tv_6);
        count_7 = (TextView) view.findViewById(R.id.tv_7);
        count_8 = (TextView) view.findViewById(R.id.tv_8);
        count_9 = (TextView) view.findViewById(R.id.tv_9);
        count_0 = (TextView) view.findViewById(R.id.tv_0);
        Delete.setOnClickListener(this);
        view.findViewById(R.id.openbox).setOnClickListener(this);
        count_1.setOnClickListener(this);
        count_2.setOnClickListener(this);
        count_3.setOnClickListener(this);
        count_4.setOnClickListener(this);
        count_5.setOnClickListener(this);
        count_6.setOnClickListener(this);
        count_7.setOnClickListener(this);
        count_8.setOnClickListener(this);
        count_9.setOnClickListener(this);
        count_0.setOnClickListener(this);
        count_1.setOnTouchListener(this);
        count_2.setOnTouchListener(this);
        count_3.setOnTouchListener(this);
        count_4.setOnTouchListener(this);
        count_5.setOnTouchListener(this);
        count_6.setOnTouchListener(this);
        count_7.setOnTouchListener(this);
        count_8.setOnTouchListener(this);
        count_9.setOnTouchListener(this);
        count_0.setOnTouchListener(this);
        box1 = (TextView) view.findViewById(R.id.pay_box1);
        box2 = (TextView) view.findViewById(R.id.pay_box2);
        box3 = (TextView) view.findViewById(R.id.pay_box3);
        box4 = (TextView) view.findViewById(R.id.pay_box4);
        box5 = (TextView) view.findViewById(R.id.pay_box5);
        box6 = (TextView) view.findViewById(R.id.pay_box6);
        tishi = (TextView) view.findViewById(R.id.txt_tishi);
    }

    @Override
    public void onDestroyView() {
        LogUtil.d("onDestroyView 执行啦");
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroyView();
        ObserverManager.getObserver().deleteObserver(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (QWApplication.openboxErrorCount >= 5 && time180 == null) {
            time180 = new TimeCount180(QWApplication.openboxErrorMillis, 1000);
            time180.start();
        }
        if (!UserInfo.UserBindState) {
            tishi.setText("未绑定设备");
            tishi.setTextColor(Color.RED);
            return;
        } else {
            tishi.setText("请输入开箱密码");
            tishi.setTextColor(Color.BLACK);
        }
    }

    @Override
    public void onClick(View v) {
        TextView id = (TextView) v;
        String string = id.getText().toString();
        if (string.matches("[0-9]")) {
            if (sb.length() < 6) {
                sb.append(string);
                updateUi();
            }
        }
        switch (v.getId()) {
            case R.id.openbox:
                if (sb.length() == 6) {
                    // 进行开箱操作。
//                    openBox();
                }
                break;
            case R.id.delete:
                if (sb.length() != 0) {
                    sb.delete(0, sb.length());
                    updateUi();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 开箱操作
     */
    private void openBox() {
        String userPassWord = getPwd(sb.toString());
        if (!UserInfo.UserBindState) {
            tishi.setText("设备未绑定");
            tishi.setTextColor(Color.RED);
            /*
             * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
			 */
            vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {100, 400}; // 停止 开启
            vibrator.vibrate(pattern, -1); // 重复两次上面的pattern 如果只想震动一次，index设为-1

            // 红色字体抖动效果
            shakeAnimation();
            if (sb.length() != 0) {
                sb.delete(0, sb.length());
                updateUi();
            }
            return;

        } else if (!BluetoothService.isConnected) {
            tishi.setText("设备未连接");
            tishi.setTextColor(Color.RED);
            /*
             * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
			 */
            vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {100, 400}; // 停止 开启
            vibrator.vibrate(pattern, -1); // 重复两次上面的pattern 如果只想震动一次，index设为-1
            // 红色字体抖动效果
            shakeAnimation();
            if (sb.length() != 0) {
                sb.delete(0, sb.length());
                updateUi();
            }
            // 开锁后提示信息还原
            Message msg = new Message();
            msg.what = 0;
            msg.obj = "请输入开箱密码";
            mHandler.sendMessageDelayed(msg, 800);
            return;
        } else {
            LogUtil.d("输入的密码:保存的密码" + userPassWord + ":" + UserInfo.UserOpenBoxPassowrd);
            if (!TextUtils.isEmpty(UserInfo.BlueId)) {
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage("正在开箱,请稍后...");
                dialog.setIndeterminate(true);
                dialog.show();
                BLECommandManager.UserOpen(getActivity(), userPassWord);//回调BOX_USER_OPENBOX_STATUS
                mHandler.removeMessages(2);
                mHandler.sendEmptyMessageDelayed(2, 5000);
            }
        }
    }

    // 为用户拼接密码
    private String getPwd(String s) {
        char[] array = s.toCharArray();
        String sb = "";
        for (int i = 0; i < array.length; i++) {
            sb += "0" + String.valueOf(array[i]);
        }
        return sb;
    }

    /**
     * 红色字体抖动动画类
     */
    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        tishi.startAnimation(shake);
    }

    /**
     * 更新六位密码的ui
     */
    private void updateUi() {
        if (sb.length() == 0) {
            box1.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru));
            box2.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru));
            box3.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru));
            box4.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru));
            box5.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru));
            box6.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru));
            box1.setText("");
            box2.setText("");
            box3.setText("");
            box4.setText("");
            box5.setText("");
            box6.setText("");
        } else if (sb.length() == 1) {
            box1.setText(sb.charAt(0) + "");
            box1.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box2.setText("");
            box3.setText("");
            box4.setText("");
            box5.setText("");
            box6.setText("");
        } else if (sb.length() == 2) {
            box1.setText(sb.charAt(0) + "");
            box1.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box2.setText(sb.charAt(1) + "");
            box2.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box3.setText("");
            box4.setText("");
            box5.setText("");
            box6.setText("");
        } else if (sb.length() == 3) {
            box1.setText(sb.charAt(0) + "");
            box1.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box2.setText(sb.charAt(1) + "");
            box2.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box3.setText(sb.charAt(2) + "");
            box3.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box4.setText("");
            box5.setText("");
            box6.setText("");
        } else if (sb.length() == 4) {
            box1.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box1.setText(sb.charAt(0) + "");
            box2.setText(sb.charAt(1) + "");
            box2.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box3.setText(sb.charAt(2) + "");
            box3.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box4.setText(sb.charAt(3) + "");
            box4.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box5.setText("");
            box6.setText("");
        } else if (sb.length() == 5) {
            box1.setText(sb.charAt(0) + "");
            box1.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box2.setText(sb.charAt(1) + "");
            box2.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box3.setText(sb.charAt(2) + "");
            box3.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box4.setText(sb.charAt(3) + "");
            box4.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box5.setText(sb.charAt(4) + "");
            box5.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box6.setText("");
        } else if (sb.length() == 6) {
            box1.setText(sb.charAt(0) + "");
            box1.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box2.setText(sb.charAt(1) + "");
            box2.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box3.setText(sb.charAt(2) + "");
            box3.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box4.setText(sb.charAt(3) + "");
            box4.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box5.setText(sb.charAt(4) + "");
            box5.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            box6.setText(sb.charAt(5) + "");
            box6.setBackground(getResources().getDrawable(R.mipmap.tuiyuan_shuru_click));
            // 进行开箱操作。
            openBox();
        }
    }

    @Override
    public void update(Observable arg0, Object obj) {
        ObservableBean ob = (ObservableBean) obj;
        switch (ob.getWhat()) {
            case BleObserverConstance.BOX_USER_OPENBOX_STATUS:
                // 清除dialog
                dialog.dismiss();
                if (OpenBoxRecoder.operationResult.equals("21")) {
                    //密码正确则重置错误次数
                    if (QWApplication.openboxErrorCount != 0) {
                        QWApplication.openboxErrorCount = 0;
                        QWApplication.mPreferences.edit().putInt("openboxErrorCount", 0).commit();
                    }
//                    ToastTool.showLongBigToast(getActivity(), "开箱成功");
                    tishi.setText("开箱成功");
                    tishi.setTextColor(Color.RED);
                    // 开锁后提示信息还原 进行提示信息的重置
//                    tishi.setText("请输入开箱密码");
//                    tishi.setTextColor(Color.BLACK);
                    // 进行密码置空
                    if (sb.length() != 0) {
                        sb.delete(0, sb.length());
                        updateUi();
                    }
                    ObservableBean msg = new ObservableBean();
                    msg.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                    msg.setObject("App验证密码开箱成功" + "\n" + CommUtils.parseOpentimeToDate(OpenBoxRecoder.openTime));
                    ObserverManager.getObserver().setMessage(msg);
                    openType = 2;
                    // 发送请求
                    requestService(OpenBoxRecoder.timeFactor, OpenBoxRecoder.openTime);
                } else if (OpenBoxRecoder.operationResult.equals("22")) {
                    //密码正确则重置错误次数
                    if (QWApplication.openboxErrorCount != 0) {
                        QWApplication.openboxErrorCount = 0;
                        QWApplication.mPreferences.edit().putInt("openboxErrorCount", 0).commit();
                    }
                    ToastTool.showLongBigToast(getActivity(), "App密码开箱堵转");
                    ObservableBean msg = new ObservableBean();
                    msg.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                    msg.setObject("App开箱堵转" + "\n" + CommUtils.parseOpentimeToDate(OpenBoxRecoder.openTime));
                    ObserverManager.getObserver().setMessage(msg);
                    BLECommandManager.UserOpenSure("0124", getActivity(), OpenBoxRecoder.timeFactor);
                    // 开锁后提示信息还原 进行提示信息的重置
                    tishi.setText("请输入开箱密码");
                    tishi.setTextColor(Color.BLACK);
                    // 进行密码置空
                    if (sb.length() != 0) {
                        sb.delete(0, sb.length());
                        updateUi();
                    }
                } else if (OpenBoxRecoder.operationResult.equals("23")) {
                    ToastTool.showLongBigToast(getActivity(), "App开箱检测连接超时");
                    ObservableBean msg = new ObservableBean();
                    msg.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                    msg.setObject("App开箱检测连接超时" + "\n" + CommUtils.parseOpentimeToDate(OpenBoxRecoder.openTime));
                    ObserverManager.getObserver().setMessage(msg);
                    BLECommandManager.UserOpenSure("0124", getActivity(), OpenBoxRecoder.timeFactor);
                    // 开锁后提示信息还原 进行提示信息的重置
                    tishi.setText("请输入开箱密码");
                    tishi.setTextColor(Color.BLACK);
                    // 进行密码置空
                    if (sb.length() != 0) {
                        sb.delete(0, sb.length());
                        updateUi();
                    }
                } else if (OpenBoxRecoder.operationResult.equals("04")) {
                    LogUtil.d("开箱密码错误了");
                    ObservableBean msg = new ObservableBean();
                    msg.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                    msg.setObject("App数字开箱密码错误" + "\n" + CommUtils.parseOpentimeToDate(OpenBoxRecoder.openTime));
                    ObserverManager.getObserver().setMessage(msg);
                    QWApplication.openboxErrorCount = QWApplication.openboxErrorCount + 1;
                    QWApplication.mPreferences.edit().putInt("openboxErrorCount", QWApplication.openboxErrorCount).commit();
                    if (5 == QWApplication.openboxErrorCount) {
                        QWApplication.openboxErrorMillis = 180000;
                        QWApplication.mPreferences.edit().putLong("openboxErrorMillis", QWApplication.openboxErrorMillis).commit();
                        time180 = new TimeCount180(QWApplication.openboxErrorMillis, 1000);
                        time180.start();
                        // 进行密码置空
                        if (sb.length() != 0) {
                            sb.delete(0, sb.length());
                            updateUi();
                        }
                        openType = 5;
                        // 发送请求
                        requestService(OpenBoxRecoder.timeFactor, OpenBoxRecoder.openTime);
                    } else {
//                        ToastTool.showLongBigToast(getActivity(), "密码错误,错误" + (5 - QWApplication.openboxErrorCount) + "次后将锁屏三分钟");
                        tishi.setText("密码错误,错误" + (5 - QWApplication.openboxErrorCount) + "次后将锁屏三分钟");
                        tishi.setTextColor(Color.RED);
                        /*
                         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
						 */
                        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        long[] pattern = {100, 400}; // 停止 开启
                        vibrator.vibrate(pattern, -1); // 重复两次上面的pattern
                        // 如果只想震动一次，index设为-1
                        // 红色字体抖动效果
                        shakeAnimation();
                        // 开锁后提示信息还原 进行提示信息的重置
//                        Message msg2 = new Message();
//                        msg2.what = 0;
//                        msg2.obj = "请输入开箱密码";
//                        mHandler.sendMessageDelayed(msg2, 800);
                        // 进行密码置空
                        if (sb.length() != 0) {
                            sb.delete(0, sb.length());
                            updateUi();
                        }
                    }
                    BLECommandManager.UserOpenSure("0124", getActivity(), OpenBoxRecoder.timeFactor);
                } else if (OpenBoxRecoder.operationResult.equals("08")) {
                    //密码正确则重置错误次数
                    if (QWApplication.openboxErrorCount != 0) {
                        QWApplication.openboxErrorCount = 0;
                        QWApplication.mPreferences.edit().putInt("openboxErrorCount", 0).commit();
                    }
//                    ToastTool.showLongBigToast(getActivity(), "箱门已打开");
                    tishi.setText("箱门已打开");
                    tishi.setTextColor(Color.RED);
                    // 开锁后提示信息还原 进行提示信息的重置
//                    tishi.setText("请输入开箱密码");
//                    tishi.setTextColor(Color.BLACK);
                    // 进行密码置空
                    if (sb.length() != 0) {
                        sb.delete(0, sb.length());
                        updateUi();
                    }
                    BLECommandManager.UserOpenSure("0124", getActivity(), OpenBoxRecoder.timeFactor);
                }
                break;

            case BleObserverConstance.RECEIVER_BOX_DATA_CLOSEBOX:
                tishi.setText("请输入开箱密码");
                tishi.setTextColor(Color.BLACK);
                break;
        }
    }


    /**
     * 开关箱记录上传服务器
     */
    private void requestService(String timeFactor, String openTime) {
        LogUtil.d("requestService执行");
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


    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof OpenBoxUploadResponse) {
            OpenBoxUploadResponse obur = (OpenBoxUploadResponse) baseRes;
            String status = obur.getStatus();
            LogUtil.d("app开箱记录传给后台结果：" + status);
            dialog.dismiss();
            dialog.setCancelable(true);
            if (status.equals("success")) {
                BLECommandManager.UserOpenSure("0124", getActivity(), OpenBoxRecoder.timeFactor);
            } else {
                BLECommandManager.UserOpenSure("0224", getActivity(), OpenBoxRecoder.timeFactor);
            }
        }

    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        dialog.dismiss();
        dialog.setCancelable(true);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!UserInfo.UserBindState) {
                tishi.setText("设备未绑定");
                tishi.setTextColor(Color.RED);
                //清空密码字符串
                mHandler.sendEmptyMessage(1);
                return true;
            } else if (UserInfo.isOverDate) {
                tishi.setText("服务已到期");
                tishi.setTextColor(Color.RED);
                return true;
            } else if (!BluetoothService.isConnected) {
                tishi.setText("设备未连接");
                tishi.setTextColor(Color.RED);
                return true;
            } else if (QWApplication.openboxErrorCount >= 5) {
                if (time180 == null) {
                    time180 = new TimeCount180(QWApplication.openboxErrorMillis, 1000);
                    time180.start();
                }
                //清空密码字符串
                mHandler.sendEmptyMessage(1);
                return true;
            }
        }
        return false;
    }

    // 密码错误后再次输入密码的倒计时
    class TimeCount180 extends CountDownTimer {
        public TimeCount180(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            //3分钟之后重置错误次数
            QWApplication.openboxErrorCount = 0;
            QWApplication.mPreferences.edit().putInt("openboxErrorCount", 0).putLong("openboxErrorMillis", 0L).commit();
            //计时完成后进行信息重置
            tishi.setText("请输入开箱密码");
            tishi.setTextColor(Color.BLACK);
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程中
            QWApplication.openboxErrorMillis = millisUntilFinished;
            QWApplication.mPreferences.edit().putLong("openboxErrorMillis", millisUntilFinished).commit();
            //提示用户看
            tishi.setText("密码错误次数过多,请" + QWApplication.openboxErrorMillis / 1000 + "秒后再试");
            tishi.setTextColor(Color.RED);
        }
    }
}
