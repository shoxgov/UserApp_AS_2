package com.qingwing.safebox.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.bluetooth.BLECommandManager;
//import com.qingwing.safebox.bluetooth.BluetoothService;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.ApkUpdateReq;
import com.qingwing.safebox.net.request.UserLoginReq;
import com.qingwing.safebox.net.response.ApkUpdateRespone;
import com.qingwing.safebox.net.response.UserLoginResponse;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.MD5Utils;
import com.qingwing.safebox.utils.ToastTool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WelcomeActivity extends Activity implements NetCallBack {

    private ImageView iv_img;
    private NotificationManager manager;
    private Notification notif;
    private static final int REQUEST_ENABLE_BT = 1;
    private String mSavePath;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;
    /* 记录进度条数量 */
    private int progress;
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    private boolean isUpdate = false;
    // 判断是否更新中
    public static String tag = "1";// 未点击更新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        iv_img = (ImageView) findViewById(R.id.iv_img);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        QWApplication.screenWidthPixels = dm.widthPixels;
        QWApplication.screenHeightPixels = dm.heightPixels;
        initFile();
        login();
        if (BLECommandManager.isSupportBLE(this)) {
            // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                checkUpdate();
                // 先检查更新
                startAnimation();
            }
        } else {
            Toast.makeText(this, "该设备蓝牙不支持BLE", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                // 正在下载
                // 设置进度条位置
                case DOWNLOAD:
                    notif.contentView.setTextViewText(R.id.content_view_text1, "正在下载...  " + progress + "%");
                    notif.contentView.setProgressBar(R.id.content_view_progress, 100, progress, false);
                    manager.notify(0, notif);
                    break;
                case DOWNLOAD_FINISH:
                    // 安装文件
                    notif.contentView.setTextViewText(R.id.content_view_text1, "下载完成！");
                    notif.contentView.setProgressBar(R.id.content_view_progress, 100, 100, false);
                    manager.notify(0, notif);
                    tag = "2";// 更新完成开始安装
                    installApk();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    protected void installApk() {
        File apkfile = new File(mSavePath, "qingwingbox.apk");
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void checkUpdate() {
        ApkUpdateReq apkUpdateReq = new ApkUpdateReq();
        apkUpdateReq.setType("1");// 1表示安卓
        apkUpdateReq.setNetCallback(this);
        apkUpdateReq.setRequestType(Request.Method.POST);
        apkUpdateReq.addRequest();
    }

    private void login() {
        String loginName = QWApplication.mPreferences.getString("loginName", "");
        String loginPwd = QWApplication.mPreferences.getString("LoginPassword", "");
        if (TextUtils.isEmpty(loginName) || TextUtils.isEmpty(loginPwd)) {
            Intent in = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(in);
            WelcomeActivity.this.finish();
            return;
        }
        if (CommUtils.isNetworkAvailable(this)) {
            UserLoginReq req = new UserLoginReq();
            req.setNetCallback(this);
            req.setMobile(loginName);
            req.setPassword(loginPwd);
            req.setMobType(android.os.Build.MANUFACTURER);
            req.setMobModel(android.os.Build.MODEL);
            req.setRequestType(Request.Method.POST);
            req.addRequest();
        }
    }

    private void initFile() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/qingwing");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            startAnimation();
            // 先检查更新
            checkUpdate();
        } else {
            finish();
        }
    }

    // 开始搜索中央设备。
//    private void scanLeDevice() {// 启动蓝牙扫描
//        Intent intent = new Intent(this, BluetoothService.class);
//        intent.putExtra("command", 0);
//        startService(intent);
//    }


    private void startAnimation() {
        AnimationSet set = new AnimationSet(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);// 从透明到不透明
        alphaAnimation.setDuration(3000);
        alphaAnimation.setFillAfter(true);
        set.addAnimation(alphaAnimation);
        set.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
//                scanLeDevice();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isUpdate) {
                    return;
                }
                if (UserInfo.isLoginSuccess) {
                    startActivity(new Intent(WelcomeActivity.this, MainBoxActivity.class));
                    WelcomeActivity.this.finish();
                } else {
                    Intent in = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(in);
                    WelcomeActivity.this.finish();
                }
            }
        });
        iv_img.startAnimation(set);
    }

    private int getVersion() {

        // 获得一个系统包管理器
        PackageManager pm = getPackageManager();
        // 获得包管理器
        try {
            // 获得功能清单文件
            PackageInfo packInfo = pm.getPackageInfo(getPackageName(), 0);
            return packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            // 不可能发生的异常
            return 0;
        }

    }

    @Override
    public void onNetResponse(final BaseResponse baseRes) {
        if (baseRes instanceof ApkUpdateRespone) {
            ApkUpdateRespone ar = (ApkUpdateRespone) baseRes;
            int s = ar.getStatusCode();
            if (s == 200) {
                // 检验是否有新版本
                LogUtil.d(
                        "getVersion():url.getDataMap().getCode()=" + getVersion() + ":" + ar.getDataMap().getCode());
                if (getVersion() < ar.getDataMap().getCode() && ar.getDataMap().getIsforce() <= 2) {
                    // 有新版本，弹出更新提示
                    isUpdate = true;
                    iv_img.clearAnimation();
                    showNoticeDialog(ar);
                } else {
                    // 版本一致,无需更新，进入主页
                }
            } else {
                ToastTool.showShortBigToast(this, "获取更新包错误");
            }
        } else if (baseRes instanceof UserLoginResponse) {
            UserLoginResponse ulr = (UserLoginResponse) baseRes;
            int s = ulr.getStatusCode();
            LogUtil.d("haha UserLoginResponse 登录是否成功：" + ulr.getDataMap().toString());
            if (s == 200) {
                String userStatus = ulr.getDataMap().getUserStatus();
                if (!TextUtils.isEmpty(userStatus) && userStatus.equals("bind")) {
                    UserInfo.starttime = MD5Utils.change_start(ulr.getDataMap().getStartDate());
                    UserInfo.endstringtime = MD5Utils.change_start(ulr.getDataMap().getEndDate());
                    UserInfo.BlueId = ulr.getDataMap().getBlueId();
                    UserInfo.UserBindState = true;
                    UserInfo.UserOpenBoxPassowrd = ulr.getDataMap().getOpenPassword();
                    QWApplication.mPreferences.edit()
                            .putString("BlueId", UserInfo.BlueId)
                            .putString("UserOpenBoxPassowrd", UserInfo.UserOpenBoxPassowrd)
                            .putString("starttime", UserInfo.starttime)
                            .putString("endstringtime", UserInfo.endstringtime)
                            .putBoolean("UserBindState", true).commit();
                    LogUtil.d(("WelcomeActitivity UserInfo.UserBindState="+UserInfo.UserBindState));
                    LogUtil.d(("WelcomeActitivity UserInfo.BlueId="+UserInfo.BlueId));
                    LogUtil.d(("WelcomeActitivity UserInfo.UserOpenBoxPassowrd="+UserInfo.UserOpenBoxPassowrd));
                    LogUtil.d(("WelcomeActitivity UserInfo.starttime="+UserInfo.starttime));
                    LogUtil.d(("WelcomeActitivity UserInfo.endstringtime="+UserInfo.endstringtime));
                } else {
                    String isPayment = ulr.getDataMap().getIsPayment();
                    if (isPayment.equals("1")) {
                        UserInfo.endstringtime = MD5Utils.change_start(ulr.getDataMap().getEndDate());
                    } else {
                        //表示当前未缴费
                        UserInfo.endstringtime = "";
                    }
                    UserInfo.BlueId = "";
                    UserInfo.UserBindState = false;
                    QWApplication.mPreferences.edit()
                            .putBoolean("UserBindState", false)
                            .putString("endstringtime", UserInfo.endstringtime)
                            .putString("BlueId", "").commit();
                    LogUtil.d(("WelcomeActitivity UserInfo.UserBindState="+UserInfo.UserBindState));
                    LogUtil.d(("WelcomeActitivity UserInfo.BlueId="+UserInfo.BlueId));
                    LogUtil.d(("WelcomeActitivity UserInfo.endstringtime="+UserInfo.endstringtime));
                }
                UserInfo.userId = ulr.getDataMap().getUserId();
                UserInfo.isOverDate = Boolean.parseBoolean(ulr.getDataMap().getIsOverDate());
                UserInfo.onlineCode = ulr.getDataMap().getOnlineCode();
                UserInfo.account_Id = ulr.getDataMap().getID();
                UserInfo.requestCode = ulr.getDataMap().getRequestCode();
                UserInfo.mobile = ulr.getDataMap().getMobile();
                QWApplication.mPreferences.edit()
                        .putInt("userId", UserInfo.userId)
                        .putString("account_Id", UserInfo.account_Id)
                        .putString("requestCode", UserInfo.requestCode)
                        .putString("mobile", UserInfo.mobile)
                        .putString("onlineCode", UserInfo.onlineCode)
                        .putString("recordListData", "")
                        .putBoolean("isOverDate", UserInfo.isOverDate)
                        .commit();
                LogUtil.d(("WelcomeActitivity UserInfo.userId="+UserInfo.userId));
                LogUtil.d(("WelcomeActitivity UserInfo.account_Id="+UserInfo.account_Id));
                LogUtil.d(("WelcomeActitivity UserInfo.requestCode="+UserInfo.requestCode));
                LogUtil.d(("WelcomeActitivity UserInfo.mobile="+UserInfo.mobile));
                LogUtil.d(("WelcomeActitivity UserInfo.onlineCode="+UserInfo.onlineCode));
                LogUtil.d(("WelcomeActitivity UserInfo.recordListData="+UserInfo.recordListData));
                LogUtil.d(("WelcomeActitivity UserInfo.isOverDate="+UserInfo.isOverDate));
            }
        }
    }

    private void showNoticeDialog(final ApkUpdateRespone ar) {
        // 构造对话框
        Builder builder = new Builder(this);
        builder.setTitle("有新版本啦");
        builder.setMessage("版本更新内容：" + "\n" + ar.getDataMap().getBei());

        // 更新
        builder.setPositiveButton("立即升级", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isUpdate = false;
                Toast.makeText(WelcomeActivity.this, "后台更新中,请稍后...", Toast.LENGTH_LONG).show();
                // // 显示下载对话框
                showDownloadDialog(ar.getDataMap().getUrl());
                startAnimation();
            }
        });

        // 稍后更新
        String btnTip;
        if (ar.getDataMap().getIsforce() == 1)
            btnTip = "关闭应用";
        else
            btnTip = "以后再说";
        builder.setNegativeButton(btnTip, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ar.getDataMap().getIsforce() == 1) {
                    closeApp();
                } else {
                    dialog.dismiss();
                    isUpdate = false;
                    startAnimation();
                }
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.setCanceledOnTouchOutside(false);
        noticeDialog.setCancelable(false);
        noticeDialog.show();
    }

    protected void closeApp() {
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        ToastTool.showShortBigToast(this, "网络异常，请检查您的网络");
        startAnimation();
    }

    private void showDownloadDialog(String url) {
        // 点击通知栏后打开的activity
        Intent intent = new Intent(this, MainBoxActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notif = new Notification();
        notif.icon = R.mipmap.ic_launcher;
        notif.tickerText = "更新通知";
        // 通知栏显示所用到的布局文件
        notif.contentView = new RemoteViews(getPackageName(), R.layout.notify_layout);
        notif.contentIntent = pIntent;
        manager.notify(0, notif);
        // 下载文件
        downloadApk(url);
    }

    private void downloadApk(String url) {
        // 启动新线程下载软件
        new downloadAplThread(url).start();
    }

    int downloadCount = 0;

    public class downloadAplThread extends Thread {

        private String urls = "";

        public downloadAplThread(String url) {
            this.urls = url;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    String path = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = path + "qingwing/download";
                    String qingwing = "青熠智能保管箱";
                    String encodeString = URLEncoder.encode(qingwing, "UTF-8");
                    String url = urls.replace(qingwing, encodeString);
                    // URL urlPath =new
                    // URL(sendType.userInfo.url.getDataMap().getUrl());
                    URL urlPath = new URL(url);
                    // 创建连接
                    conn = (HttpURLConnection) urlPath.openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    is = conn.getInputStream();
                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, "qingwingbox.apk");
                    fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        // 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
                        if (downloadCount == 0 || progress - 5 > downloadCount) {
                            downloadCount += 5;
                            mHandler.sendEmptyMessage(DOWNLOAD);
                        }
                        if (numread <= 0) {
                            // 下载完成
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (conn == null || fos == null || is == null) {
                        return;
                    }
                    conn.disconnect();
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
