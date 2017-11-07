package com.qingwing.safebox.activity;

import android.content.Intent;
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
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.bluetooth.BLECommandManager;
import com.qingwing.safebox.bluetooth.BleObserverConstance;
import com.qingwing.safebox.bluetooth.BluetoothService;
import com.qingwing.safebox.dialog.LoadingDialog;
import com.qingwing.safebox.dialog.SettingBoxOpenPasswordDialogs;
import com.qingwing.safebox.imp.DialogCallBack;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.QueryUserBindStatusReq;
import com.qingwing.safebox.net.request.UserBindReq;
import com.qingwing.safebox.net.response.QueryUserBindStatusRespone;
import com.qingwing.safebox.net.response.UserBindResponse;
import com.qingwing.safebox.observable.ObservableBean;
import com.qingwing.safebox.observable.ObserverManager;
import com.qingwing.safebox.utils.AcitivityCollector;
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
public class MipcaCaptureActivity extends BaseMipcaCaptureActivity implements Callback, NetCallBack, Observer {

    private static final int FAIL = 1;
    private static final int DELAYTIME = 180000;
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
    private TextView iv_handle_no;
    static Camera camera = null;
    private Parameters parameters;
    private boolean isopen;
    /**
     * 开箱密码
     */
    private String openBoxPassowrd = "";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FAIL:
                    if (msg.obj == null || TextUtils.isEmpty(msg.obj.toString())) {
                        ToastTool.showShortBigToast(MipcaCaptureActivity.this, "绑定超时，退出绑定流程，请重新解绑");
                    } else {
                        ToastTool.showShortBigToast(MipcaCaptureActivity.this, msg.obj.toString());
                    }
                    finish();
                    break;
            }
        }
    };
    /**
     * flag数据位
     * 如果为01，那么代表此条命令为用户续费命令，用户密码6字节无意义，保持原有密码。
     * 如果为00，代表是新用户绑定保管箱，需要同时更新服务到期时间和密码。
     */
    private String flag = "00";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_capture);
        ObserverManager.getObserver().addObserver(this);
        AcitivityCollector.addActivity(this);
        CameraManager.init(getApplication());
        UserInfo.QrBtId = "";
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        ImageView mButtonBack = (ImageView) findViewById(R.id.button_back);
        iv_handle_no = (TextView) findViewById(R.id.iv_handle_no);
        iv_dengguang = (TextView) findViewById(R.id.iv_dengguang);
        iv_handle_no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent handle = new Intent();
                    handle.setClass(MipcaCaptureActivity.this, HandleNoActivity.class);
                    startActivityForResult(handle, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
                MipcaCaptureActivity.this.finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {//手动输入编码
            String resultString = data.getStringExtra("result");
            if (!TextUtils.isEmpty(resultString)) {
                requestBindStatus(resultString);
            }
//            this.setResult(RESULT_OK, data);
//            finish();
        } else if (requestCode == 1) {//绑定时要先缴费
            if (resultCode == PackageMoneyActivity.WEIXIN_ALIPAY_PAY_SUCCESS || resultCode == PackageMoneyActivity.CARDNUMBER_PAY_SUCCESS) {
                requestBindStatus(UserInfo.QrBtId);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        LoadingDialog.dismissDialog();
        mHandler.removeMessages(FAIL);
        inactivityTimer.shutdown();
        UserInfo.QrBtId = "";
        super.onDestroy();
    }

    /**
     * 处理扫描结果
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (resultString.equals("")) {
            Toast.makeText(MipcaCaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("返回的结果", resultString);
            if (resultString.contains("&sn=")) {// http://qr06.cn/C9RyUW?type=1&sn=00050020
                resultString = resultString.split("&sn=")[1];
            }
            LoadingDialog.showDialog(this, "开始绑定...");
            requestBindStatus(resultString);
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
            // The volume on STREAM_SYSTEM is not adjustable, and users found it too loud, so we now play on the music stream.
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

    private void requestBindStatus(String qr) {
        if (!CommUtils.isNetworkAvailable(this)) {
            ToastTool.showShortBigToast(this, "无网络连接");
            return;
        }
        LoadingDialog.showDialog(this, "绑定中...");
        mHandler.removeMessages(FAIL);
        mHandler.sendEmptyMessageDelayed(FAIL, DELAYTIME);
        UserInfo.QrBtId = qr;
        LogUtil.d("onActivityResult  QR result=" + UserInfo.QrBtId);
        QueryUserBindStatusReq req = new QueryUserBindStatusReq();
        req.setNetCallback(this);
        req.setRequestType(Request.Method.POST);
        req.setUserId(UserInfo.userId + "");
        req.setBarcode(qr);
        req.addRequest();
    }


    @Override
    public void update(Observable observable, Object obj) {
        ObservableBean ob = (ObservableBean) obj;
        switch (ob.getWhat()) {
            case BleObserverConstance.BOX_QUERY_STATUS_CALLBACK://查询保管箱状态的回调
                final SettingBoxOpenPasswordDialogs dialogs = new SettingBoxOpenPasswordDialogs(MipcaCaptureActivity.this, new DialogCallBack() {

                    @Override
                    public void OkDown(Object obj) {
                        if (obj == null || TextUtils.isEmpty(obj.toString())) {
                            return;
                        }
                        openBoxPassowrd = obj.toString();
                        UserBindReq req = new UserBindReq();
                        req.setNetCallback(MipcaCaptureActivity.this);
                        req.setUserId(UserInfo.userId + "");
                        req.setPassword(obj.toString());
                        req.setBarcode(UserInfo.QrBtId);
                        req.setRequestType(Request.Method.POST);
                        req.addRequest();
                        LoadingDialog.showDialog(MipcaCaptureActivity.this, "正在绑定保管箱...");
                    }

                    @Override
                    public void CancleDown() {
                        ToastTool.showShortBigToast(MipcaCaptureActivity.this, "退出绑定");
                        finish();
                    }
                });
                dialogs.show();
                break;

            case BleObserverConstance.BOX_USER_BIND_RESULT:
                if ((boolean) ob.getObject()) {
                    UserInfo.UserBindState = true;
                    UserInfo.UserOpenBoxPassowrd = openBoxPassowrd;
                    UserInfo.BlueId = UserInfo.QrBtId;
                    UserInfo.lock_password = "";//手势密码置空
                    UserInfo.lock_style = "c";//默认开锁改为数字解锁
                    QWApplication.mPreferences.edit().putBoolean("UserBindState", UserInfo.UserBindState).putString("BlueId", UserInfo.BlueId)
                            .putString("UserOpenBoxPassowrd", UserInfo.UserOpenBoxPassowrd)
                            .putString("lock_password", "").putString("lock_style", "c").commit();
                    ToastTool.showShortBigToast(MipcaCaptureActivity.this, "绑定成功");
                    setResult(RESULT_OK);
                } else {
                    UserInfo.UserBindState = false;
                    setResult(RESULT_OK);
                    ToastTool.showShortBigToast(MipcaCaptureActivity.this, "绑定失败");
                }
                finish();
                break;
            case BleObserverConstance.BOX_BIND_CONNECT_NODEVICE:
                ToastTool.showShortBigToast(this, "当前没有找到保管箱设备,绑定失败");
                finish();
                break;
            case BleObserverConstance.BT_OFF_HAND_ACTION:
                Message ms = new Message();
                ms.what = FAIL;
                ms.obj = "蓝牙断开，退出绑定";
                mHandler.sendMessage(ms);
                break;
            case BleObserverConstance.NETWORK_OFF_HAND_ACTION:
                Message ms2 = new Message();
                ms2.what = FAIL;
                ms2.obj = "网络断开，退出绑定";
                mHandler.sendMessage(ms2);
                break;
        }
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof QueryUserBindStatusRespone) {
            QueryUserBindStatusRespone qubr = (QueryUserBindStatusRespone) baseRes;
            String status = qubr.getStatus();
            String message = qubr.getMessage();
            if (!TextUtils.isEmpty(status) && status.equals("success")) {
                if (message.contains("允许绑定")) {
                    flag = "00";
                    Intent server = new Intent();
                    server.setAction(BluetoothService.ACTION_BT_COMMAND);
                    server.putExtra("command", "CONNECT_BT_ADDRESS");
                    server.putExtra("blueId", UserInfo.QrBtId);
                    sendBroadcast(server);
                } else if (message.contains("但超过绑定保护时间")) {
                    ToastTool.showLongBigToast(this, message);
                    finish();
                }
            } else {
                if (message.contains("请先缴费")) {
                    flag = "01";
                    Intent packageSet = new Intent();
                    packageSet.setClass(this, PackageMoneyActivity.class);
                    packageSet.putExtra("boxID_a", UserInfo.QrBtId);
                    startActivityForResult(packageSet, 1);
                    return;
                } else if (message.contains("密保未设置")) {
                    ToastTool.showShortBigToast(this, "请先完善密保问题再绑定");
                    Intent intent = new Intent(this, UserQuestionActivity.class);
                    intent.putExtra("leftfragment", "leftfragment");// leftfragment跳转过去的
                    intent.putExtra("mibao", "mibao");// 弹出密保问题
                    startActivityForResult(intent, 3);
                    return;
                } else if (message.contains("设置身份证")) {
                    return;
                } else {
                    ToastTool.showLongBigToast(this, message);
                    finish();
                }
            }
        } else if (baseRes instanceof UserBindResponse) {
            UserBindResponse nubr = (UserBindResponse) baseRes;
            if (nubr.getStatusCode() == 200) {
                String status = nubr.getStatus();
                UserInfo.isBindMeseage = nubr.getDataMap().getBindRecord();
                ObservableBean ob = new ObservableBean();
                ob.setWhat(BleObserverConstance.ACTION_ADD_MSG);
                ob.setObject(nubr.getDataMap().getBindRecord());
                ObserverManager.getObserver().setMessage(ob);

                if (!TextUtils.isEmpty(status) && status.equals("success")) {
                    UserInfo.starttime = nubr.getStartTime();
                    UserInfo.endstringtime = nubr.getEndTime();
                    LogUtil.d("开始时间=" + UserInfo.starttime);
                }
                // 得到保管箱到期时间和服务器时间
                if (TextUtils.isEmpty(UserInfo.starttime)
                        || TextUtils.isEmpty(UserInfo.endstringtime)) {
                    ToastTool.showShortBigToast(this, "服务器未保存服务时间和保管箱到期时间");
                } else {
                    String endtime = UserInfo.endstringtime.substring(0, 6);
                    String startTime = UserInfo.starttime.substring(0, 6);
                    LogUtil.d("服务器时间：服务到期时间   " + startTime + ":" + endtime);
                    BLECommandManager.userBind(startTime, endtime, UserInfo.QrBtId, openBoxPassowrd, this, flag);
                }
            } else {
                ToastTool.showLongBigToast(this, nubr.getMessage());
                finish();
            }
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        try {
            ToastTool.showLongBigToast(this, ((VolleyError) error).getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

}