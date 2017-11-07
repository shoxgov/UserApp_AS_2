package com.qingwing.safebox.activity;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.BoxStatusBean;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.bluetooth.BLECommandManager;
import com.qingwing.safebox.bluetooth.BleObserverConstance;
import com.qingwing.safebox.dialog.LoadingDialog;
import com.qingwing.safebox.dialog.WarnInfosDialog;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.QueryIsCanUnbindReq;
import com.qingwing.safebox.net.request.UserBindReq;
import com.qingwing.safebox.net.request.UserUnbindReq;
import com.qingwing.safebox.net.response.QueryIsCanUnbindRespone;
import com.qingwing.safebox.net.response.UserBindResponse;
import com.qingwing.safebox.net.response.UserUnbindResponse;
import com.qingwing.safebox.observable.ObservableBean;
import com.qingwing.safebox.observable.ObserverManager;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.BlueDeviceUtils;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.ToastTool;
import com.qingwing.safebox.zxing.camera.CameraManager;
import com.qingwing.safebox.zxing.decoding.CaptureActivityHandler;
import com.qingwing.safebox.zxing.decoding.InactivityTimer;
import com.qingwing.safebox.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * Initial the camera
 */
public class UnbindMipcaCaptureActivity extends BaseMipcaCaptureActivity implements Callback, NetCallBack, Observer {
    private static final int DELAYTIME = 180000;
    /**
     * 是否要进行解绑
     */
    private static boolean isUnbind = false;
    private static final int FAIL = 2;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private TextView iv_dengguang;
    private static Camera camera = null;
    private Parameters parameters;
    private boolean isopen;//是否开启了摄像头
    private WarnInfosDialog warnInfos;
    /**
     * 区分是否键盘开箱
     */
    private boolean isKeyboardOpenBox = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FAIL:
                    if (msg.obj == null || TextUtils.isEmpty(msg.obj.toString())) {
                        ToastTool.showShortBigToast(UnbindMipcaCaptureActivity.this, "解绑超时，退出解绑流程，请重新解绑");
                    } else {
                        ToastTool.showShortBigToast(UnbindMipcaCaptureActivity.this, msg.obj.toString());
                    }
                    finish();
                    break;
            }
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_unbind_capture);
        ObserverManager.getObserver().addObserver(this);
        AcitivityCollector.addActivity(this);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        TextView title = (TextView) findViewById(R.id.textview_title);
        ImageView mButtonBack = (ImageView) findViewById(R.id.button_back);
        title.setText("解绑");
        warnInfos = new WarnInfosDialog(this);
        iv_dengguang = (TextView) findViewById(R.id.iv_dengguang);
        iv_dengguang.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    camera = CameraManager.get().getCamera();
                    parameters = camera.getParameters();
                    if (!isopen) {
                        isopen = true;
                        parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(parameters);
                        iv_dengguang.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.light_press), null, null);//左上右下
                    } else {
                        isopen = false;
                        parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                        camera.setParameters(parameters);
                        iv_dengguang.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.light_unpress), null, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mButtonBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UnbindMipcaCaptureActivity.this.finish();
            }
        });
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onStop() {
        super.onStop();
        iv_dengguang.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.light_unpress), null, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        ObserverManager.getObserver().deleteObserver(this);
        AcitivityCollector.removeActivity(this);
        mHandler.removeMessages(FAIL);
        LoadingDialog.dismissDialog();
        if (warnInfos != null) {
            warnInfos.dismiss();
        }
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 处理扫描结果
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (resultString.equals("")) {
            Toast.makeText(UnbindMipcaCaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            LogUtil.d("返回的结果" + resultString);
            if (resultString.contains("&sn=")) {// http://qr06.cn/C9RyUW?type=1&sn=00050020
                resultString = resultString.split("&sn=")[1];
            }
            if (resultString.equals(UserInfo.BlueId)) {
                requestUnbindStatus();
            } else {
                Toast.makeText(UnbindMipcaCaptureActivity.this, "与绑定的蓝牙ID不符", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
        if (camera != null) {
            CameraManager.get().stopPreview();
        }
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private void requestUnbindStatus() {
        LogUtil.d("requestUnbindStatus  UserInfo.BlueId：" + UserInfo.BlueId);
        LoadingDialog.showDialog(this, "正在检测...");
        isKeyboardOpenBox = false;
        mHandler.removeMessages(FAIL);
        mHandler.sendEmptyMessageDelayed(FAIL, DELAYTIME);
        QueryIsCanUnbindReq req = new QueryIsCanUnbindReq();
        req.setNetCallback(this);
        req.setUserId(UserInfo.userId + "");
        req.setBarcode(UserInfo.BlueId);
        req.addRequest();
    }

    @Override
    public void update(Observable observable, Object obj) {
        ObservableBean ob = (ObservableBean) obj;
        switch (ob.getWhat()) {
            case BleObserverConstance.BOX_CONNTEC_BLE_STATUS:
                LogUtil.d(" unBind >>>> update  BOX_CONNTEC_BLE_STATUS  btStatus=" + ob.getObject());
                if (!UserInfo.UserBindState && !(boolean) ob.getObject()) {
                    Message msg = new Message();
                    msg.what = FAIL;
                    msg.obj = "蓝牙断开，取消解绑！";
                    mHandler.sendMessage(msg);
                }
                break;
            case BleObserverConstance.BOX_USER_UNBIND_EXIT:
                int count = BlueDeviceUtils.hexStringToInteger(ob.getObject().toString());
                String binary = Integer.toBinaryString(count);
                LogUtil.d("checkExitBoxState转换之前:" + binary);
                StringBuffer sb = new StringBuffer();
                sb.append(binary);
                sb.reverse();
                int length = sb.toString().length();
                if (sb.toString().length() <= 8) {
                    for (int i = 0; i < 8 - length; i++) {
                        sb.append(0);
                    }
                }
                LogUtil.d("checkExitBoxState转换之后:" + sb.toString());
                //如果正常，则进入下一步，如果异常则提示哪一块出错，并退出用户解除绑定流程
                for (int i = 0; i < sb.toString().length(); i++) {
                    int binaryParseIntResult = Integer.parseInt(String.valueOf(sb.toString().charAt(i)));
                    //查看每个bit位是否正确
                    boolean result = checkBinary(binaryParseIntResult, i);
                    if (!result) {
                        LogUtil.d("检测不合格");
                        finish();
                        return;
                    }
                }
                LogUtil.d("检测全部合格");
                //如果在解绑过程中开箱了则不进行下一步。否则继续
                if (BoxStatusBean.openStatus.equals("00")) {//关闭状态
                    LoadingDialog.dismissDialog();
                    warnInfos.show("请使用保管箱密码,键盘开箱,并拿出所有物品");
                } else {
                    //解绑过程中开箱了。
                    ToastTool.showShortBigToast(UnbindMipcaCaptureActivity.this, "保管箱未关闭，解绑失败，请勿在解绑过程中进行异常操作");
                    LoadingDialog.dismissDialog();
                    warnInfos.show("解绑异常，箱门未关,请关闭箱门");
                }
                break;
            case BleObserverConstance.BOX_CLOSE_KEYBOARD_STATUS://临时锁定键盘状态结果
                /*查看目前的箱门状态：
                1.【如果箱门打开，则保管箱回复APP锁定面板失败--02 53| 锁定失败，此时，APP应该终止解绑流程。】
                2.【如果箱门是关着的，那么就锁定触摸面板10秒，并回复APP锁定面板成功--01 53| 锁定成功。】*/
                LogUtil.d("UnbindBoxDialogs:BOX_CLOSE_KEYBOARD_STATUS");
                if ((boolean) ob.getObject()) {//发送服务器注销解绑保管箱
                    if (CommUtils.isNetworkAvailable(this)) {
                        UserUnbindReq req = new UserUnbindReq();
                        req.setNetCallback(UnbindMipcaCaptureActivity.this);
                        req.setBarcode(UserInfo.BlueId);
                        req.setUserId(UserInfo.userId + "");
                        req.addRequest();
                    } else {
                        Message msg = new Message();
                        msg.what = FAIL;
                        msg.obj = "当前无网络，取消解绑！";
                        mHandler.sendMessage(msg);
                    }
                } else {
                    Message msg = new Message();
                    msg.what = FAIL;
                    msg.obj = "锁定键盘失败，取消解绑！";
                    mHandler.sendMessage(msg);
                }
                break;

            case BleObserverConstance.BOX_USER_UNBIND_BY_HAND:
                if ((boolean) ob.getObject()) {
                    LogUtil.d("UnbindBoxDialogs:解绑成功，关闭蓝牙");
                    UserInfo.UserBindState = false;
                    UserInfo.lock_password = "";//手势密码置空
                    UserInfo.lock_style = "c";//默认开锁改为数字解锁
                    UserInfo.BlueId = "";
                    QWApplication.mPreferences.edit().putBoolean("UserBindState", false).putString("BlueId", "").putString("lock_password", "").putString("lock_style", "c").commit();
                    //最后一步，解绑成功
                    // 将显示文字弄成未绑定
                    ToastTool.showShortBigToast(this, "解绑成功");
                    ObservableBean off = new ObservableBean();
                    off.setWhat(BleObserverConstance.BOX_CONNTEC_BLE_STATUS);
                    off.setObject(false);
                    ObserverManager.getObserver().setMessage(off);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastTool.showShortBigToast(UnbindMipcaCaptureActivity.this, "解除绑定失败，现重新绑回服务器");
                    LogUtil.d("UnbindBoxDialogs:解绑失败，恢复原来绑定");
                    UserBindReq req = new UserBindReq();
                    req.setNetCallback(UnbindMipcaCaptureActivity.this);
                    req.setUserId(UserInfo.userId + "");
                    req.setPassword(UserInfo.UserOpenBoxPassowrd);
                    req.setBarcode(UserInfo.BlueId);
                    req.addRequest();
                }
                break;
            case BleObserverConstance.DIALOG_USER_UNBIND_STATUS_CALLBACK:
                isKeyboardOpenBox = false;
                if (BoxStatusBean.openStatus.equals("00")) {//关闭状态
                    BLECommandManager.UserUnBindExitBox(this);//回调是BOX_USER_UNBIND_EXIT
                } else {//开着状态
                    ToastTool.showShortBigToast(UnbindMipcaCaptureActivity.this, "请关闭保管箱");
                    LoadingDialog.dismissDialog();
                    warnInfos.show("解绑异常，箱门未关,请关闭箱门");
                }
                break;

            case BleObserverConstance.BT_OFF_HAND_ACTION:
                Message ms = new Message();
                ms.what = FAIL;
                ms.obj = "蓝牙断开，退出解绑";
                mHandler.sendMessage(ms);
                break;
            case BleObserverConstance.NETWORK_OFF_HAND_ACTION:
                Message ms2 = new Message();
                ms2.what = FAIL;
                ms2.obj = "网络断开，退出解绑";
                mHandler.sendMessage(ms2);
                break;
            case BleObserverConstance.RECEIVER_BOX_DATA_ALARM:
                mHandler.sendEmptyMessage(FAIL);
                break;
            case BleObserverConstance.RECEIVER_BOX_DATA_OPENBOX:// 接收到用户在保管箱开箱数据
                //要在关箱的状态下才能解 用做监听用
                // 判断用户开箱方式是否是键盘开箱
                String data = ob.getObject().toString();
                if (data.substring(16, 18).equals("11")) {//11:玩锁开箱; 10:	蓝牙指令开箱;01:按键开箱
                    if (isKeyboardOpenBox) {
                        mHandler.sendEmptyMessage(FAIL);
                        return;
                    }
                    LoadingDialog.dismissDialog();
                    warnInfos.show("已开箱,请确认取出物品后,关闭箱门...");
                    isKeyboardOpenBox = true;
                } else {
                    Message msg = new Message();
                    msg.what = FAIL;
                    msg.obj = "解绑失败，请根据提示使用键盘密码开箱！！";
                    mHandler.sendMessage(msg);
                }
                break;
            case BleObserverConstance.RECEIVER_BOX_DATA_CLOSEBOX:// 接收到用户关保管箱数据
                LogUtil.d("保险箱关闭了");
                // 向保管箱发送退箱检测指令。HandlBoxAlarmInfo
                if (isKeyboardOpenBox) {
                    isKeyboardOpenBox = false;
                    LoadingDialog.dismissDialog();
                    warnInfos.show("正在解绑...");
                    QWApplication.mPreferences.edit().putLong("CloseKeyBoardTime", System.currentTimeMillis()).commit();
                    BLECommandManager.CloseKeyBoard(UnbindMipcaCaptureActivity.this);//回调是BOX_CLOSE_KEYBOARD_STATUS
                } else if (isUnbind && warnInfos != null && warnInfos.isShowing()) {
                    BLECommandManager.UserUnBindExitBox(this);//回调是BOX_USER_UNBIND_EXIT
                    LoadingDialog.showDialog(this, "正在解绑...");
                    warnInfos.dismiss();
                }
                break;
        }
    }

    private boolean checkBinary(int binaryParseIntResult, int count) {
        switch (count) {
            case 0:
                // 0代表右开关正常，为1代表错误。
                //错误--0x01
                if (binaryParseIntResult == 0) {
                    return true;
                } else if (binaryParseIntResult == 1) {
                    ObservableBean ob_0 = new ObservableBean();
                    ob_0.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                    ob_0.setObject("用户解除绑定失败:右开关检测异常");
                    ToastTool.showLongBigToast(this, "用户解除绑定失败:右开关检测异常");
                    ObserverManager.getObserver().setMessage(ob_0);
                    return false;
                }
                break;
            case 1:
                // 0代表左开关检测正常，1代表错误。
                //错误--0x02
                if (binaryParseIntResult == 0) {
                    return true;
                } else if (binaryParseIntResult == 1) {
                    ObservableBean ob_0 = new ObservableBean();
                    ob_0.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                    ob_0.setObject("用户解除绑定失败:左开关检测异常");
                    ToastTool.showLongBigToast(this, "用户解除绑定失败:左开关检测异常");
                    ObserverManager.getObserver().setMessage(ob_0);
                    return false;
                }
                break;
            case 2:
                // 0代表小无线检测正常，1代表错误
                // 错误--0x04
                if (binaryParseIntResult == 0) {
                    return true;
                } else if (binaryParseIntResult == 1) {
                    ObservableBean ob_0 = new ObservableBean();
                    ob_0.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                    ob_0.setObject("用户解除绑定失败:小无线检测异常");
                    ToastTool.showLongBigToast(this, "用户解除绑定失败:小无线检测异常");
                    ObserverManager.getObserver().setMessage(ob_0);
                    return false;
                }
                break;

            case 3:
                // 0代表时钟检测正常，1代表错误。
                //错误--0x08
                if (binaryParseIntResult == 0) {
                    return true;
                } else if (binaryParseIntResult == 1) {
                    ObservableBean ob_0 = new ObservableBean();
                    ob_0.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                    ob_0.setObject("用户解除绑定失败:时钟检测异常");
                    ToastTool.showLongBigToast(this, "用户解除绑定失败:时钟检测异常");
                    ObserverManager.getObserver().setMessage(ob_0);
                    return false;
                }
                break;

            case 4:
                // 0代表外部flash检测正常，1代表错误。
                //错误--0x10
                if (binaryParseIntResult == 0) {
                    return true;
                } else if (binaryParseIntResult == 1) {
                    ObservableBean ob_0 = new ObservableBean();
                    ob_0.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                    ob_0.setObject("用户解除绑定失败:外部flash检测异常");
                    ToastTool.showLongBigToast(this, "用户解除绑定失败:外部flash检测异常");
                    ObserverManager.getObserver().setMessage(ob_0);
                    return false;
                }
                break;
            case 5:
                // 0代表外部EEPROM，1代表错误。
                //错误0x20
                if (binaryParseIntResult == 0) {
                    return true;
                } else if (binaryParseIntResult == 1) {
                    ObservableBean ob_0 = new ObservableBean();
                    ob_0.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                    ob_0.setObject("用户解除绑定失败:外部EEPROM检测异常");
                    ToastTool.showLongBigToast(this, "用户解除绑定失败:外部EEPROM检测异常");
                    ObserverManager.getObserver().setMessage(ob_0);
                    return false;
                }
                break;
            default:
                return true;
        }
        return true;
    }

    private String setPwdString(String s) {
        char[] array = s.toCharArray();
        String sb = "";
        for (int i = 0; i < array.length; i++) {
            sb += "0" + String.valueOf(array[i]);
        }
        return sb;
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof QueryIsCanUnbindRespone) {
            LogUtil.d("收到后台申请是否可以解除绑定返回数据 ");
            QueryIsCanUnbindRespone qicur = (QueryIsCanUnbindRespone) baseRes;
            String status = qicur.getStatus();
            LogUtil.d("QueryIsCanUnbindRespone(getStatus())::" + status);
            if (!TextUtils.isEmpty(status) && status.equals("success")) {
                //向保管箱发送18指令查看保管箱是否是关闭状态
                isUnbind = true;
                BLECommandManager.BoxStateCheck(this);
            } else {
                isUnbind = false;
                ToastTool.showShortBigToast(this, qicur.getMessage());
                Message msg = new Message();
                msg.what = FAIL;
                msg.obj = "不可解绑，请重试";
                mHandler.sendMessageDelayed(msg, 600);
            }
        } else if (baseRes instanceof UserUnbindResponse) {
            UserUnbindResponse uur = (UserUnbindResponse) baseRes;
            if (uur.getStatus().equals("success")) {
                ObservableBean ob = new ObservableBean();
                ob.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                ob.setObject(uur.getDataMap().getUnbindRecord());
                ObserverManager.getObserver().setMessage(ob);
                BLECommandManager.UserUnBindUnRegister(this, UserInfo.BlueId, setPwdString(UserInfo.UserOpenBoxPassowrd));//回调BOX_USER_UNBIND_BY_HAND
            } else {
                ToastTool.showShortBigToast(this, uur.getMessage());
                finish();
            }
        } else if (baseRes instanceof UserBindResponse) {
            UserBindResponse ubr = (UserBindResponse) baseRes;
            if (ubr.getStatusCode() == 200) {
                String status = ubr.getStatus();
                UserInfo.isBindMeseage = ubr.getDataMap().getBindRecord();
                ObservableBean ob = new ObservableBean();
                ob.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                ob.setObject(ubr.getDataMap().getBindRecord());
                ObserverManager.getObserver().setMessage(ob);

                if (!TextUtils.isEmpty(status) && status.equals("success")) {
                    UserInfo.starttime = ubr.getStartTime();
                    UserInfo.endstringtime = ubr.getEndTime();
                    LogUtil.d("开始时间=" + UserInfo.starttime);
                }
                // 得到保管箱到期时间和服务器时间
                if (TextUtils.isEmpty(UserInfo.starttime)
                        || TextUtils.isEmpty(UserInfo.endstringtime)) {
                    ToastTool.showShortBigToast(this, "服务器未保存服务时间和保管箱到期时间");
                    finish();
                } else {
                    String endtime = UserInfo.endstringtime.substring(0, 6);
                    String startTime = UserInfo.starttime.substring(0, 6);
                    LogUtil.d("服务器时间：服务到期时间   " + startTime + ":" + endtime);
                    BLECommandManager.userBind(startTime, endtime, UserInfo.BlueId, UserInfo.UserOpenBoxPassowrd, this, "00");
                }
            } else {
                ToastTool.showShortBigToast(this, ubr.getMessage());
                UserInfo.UserBindState = false;
                finish();
            }
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        Message msg = new Message();
        msg.what = FAIL;
        msg.obj = "无网络连接，退出解绑";
        mHandler.sendMessage(msg);
    }

}