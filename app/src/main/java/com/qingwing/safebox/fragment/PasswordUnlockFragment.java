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
import android.util.Log;
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
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.bluetooth.BluetoothService;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.OpenBoxUploadReq;
import com.qingwing.safebox.net.response.OpenBoxUploadResponse;
import com.qingwing.safebox.observable.ObservableBean;
import com.qingwing.safebox.observable.ObserverManager;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;

import java.util.Observable;
import java.util.Observer;

public class PasswordUnlockFragment extends Fragment implements OnClickListener, OnTouchListener, Observer, NetCallBack {
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
    // 振动器
    private Vibrator vibrator;


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
        View view = inflater.inflate(R.layout.fragment_home_guesture_unlock, null);
        initView(view);
        ObserverManager.getObserver().addObserver(this);
        // 判断用户是否绑定保管箱
        if (!UserInfo.UserBindState) {
            tishi.setText("设备未绑定");
            tishi.setTextColor(Color.RED);
        }
        return view;
    }

    private void initView(View view) {
        Log.i("haha", "TodayFragment initView");
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
        Delete.setOnTouchListener(this);
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
        super.onDestroyView();
        ObserverManager.getObserver().deleteObserver(this);
    }

    @Override
    public void onDestroy() {
        LogUtil.d("onDestroy 执行啦");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        TextView id = (TextView) v;
        String string = id.getText().toString();
        if (string.matches("[0-9]")) {
            sb.append(string);
            updateUi();
        }
        switch (v.getId()) {
            case R.id.openbox:
                openBox();
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
            return;
        } else {
        }
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
        Message msg = new Message();
        msg.what = ob.getWhat();
        msg.obj = ob.getObject();
    }


    /**
     * 上传服务器
     */
    private void requestService(String timeFactor, String openTime) {
        LogUtil.d("requestService执行");
        if (CommUtils.isNetworkAvailable(getActivity())) {
            OpenBoxUploadReq req = new OpenBoxUploadReq();
            req.setNetCallback(this);
            req.setRequestType(Request.Method.POST);
//            req.setActionType(QingWindApplication.getInstance().openType + "");
            req.setBarcode(UserInfo.BlueId);
            req.setDate(openTime);
            req.addRequest();
        } else {
//            QingWindApplication.getInstance().isbreakBoxRecord = false;
//            BlueDeviceManage.UserOpenSure(new Intent(), UserInfo.BlueId,
//                    "0224", getActivity(), timeFactor);
        }
    }


    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof OpenBoxUploadResponse) {
            OpenBoxUploadResponse obur = (OpenBoxUploadResponse) baseRes;
            String status = obur.getStatus();
            LogUtil.d("app开箱记录传给后台结果：" + status);
            if (status.equals("success")) {
//                BlueDeviceManage.UserOpenSure(new Intent(), UserInfo.BlueId,
//                        "0124", getActivity(), timeFactor);
            } else {
//                BlueDeviceManage.UserOpenSure(new Intent(), UserInfo.BlueId,
//                        "0224", getActivity(), timeFactor);
            }
        }

    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
//        BlueDeviceManage.UserOpenSure(new Intent(), UserInfo.BlueId,
//                "0224", getActivity(), timeFactor);
//        QingWindApplication.getInstance().isReadBoxRecord = false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!UserInfo.UserBindState) {
                tishi.setText("设备未绑定");
                tishi.setTextColor(Color.RED);
                //清空密码字符串
                Message msg = new Message();
                msg.what = 1;
                return true;
            } else if (UserInfo.isOverDate) {
                tishi.setText("服务已到期");
                tishi.setTextColor(Color.RED);
                return true;
            } else if (!BluetoothService.isConnected) {
                tishi.setText("设备未连接");
                tishi.setTextColor(Color.RED);
                return true;
            } /*else if (QingWindApplication.getInstance().openboxErrorCount >= 5) {
                if (time == null) {
                    time = new TimeCount(QingWindApplication.getInstance().openboxErrorMillis, 1000);
                    time.start();
                }
                //清空密码字符串
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
                return true;
            }*/
        }
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
