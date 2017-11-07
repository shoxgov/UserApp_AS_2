package com.qingwing.safebox.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.qingwing.safebox.R;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.ApkUpdateReq;
import com.qingwing.safebox.net.response.ApkUpdateRespone;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.ToastTool;
import com.qingwing.safebox.utils.WaitTool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AboutActivity extends Activity implements NetCallBack {

    private ImageView about_app_exit;
    private TextView check_version, current_version, tv_feedback;
    private AlertDialog noticeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        AcitivityCollector.addActivity(this);
        about_app_exit = (ImageView) findViewById(R.id.calback);
        check_version = (TextView) findViewById(R.id.check_version);
        current_version = (TextView) findViewById(R.id.current_version);
        tv_feedback = (TextView) findViewById(R.id.tv_feedback);
        String version = getVersionName();
        if (version != null) {
            current_version.setText("当前版本：" + version);
        }
        about_app_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        check_version.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WaitTool.showDialog(AboutActivity.this, "正在检查更新...");
                checkUpdate();
                check_version.setClickable(false);
            }
        });
        tv_feedback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutActivity.this, FeedBackActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AcitivityCollector.removeActivity(this);
    }

    private void checkUpdate() {
        ApkUpdateReq apkUpdateReq = new ApkUpdateReq();
        apkUpdateReq.setType("1");// 1表示安卓
        apkUpdateReq.setNetCallback(this);
        apkUpdateReq.setRequestType(Request.Method.POST);
        apkUpdateReq.addRequest();
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        check_version.setClickable(true);
        if (baseRes instanceof ApkUpdateRespone) {
            WaitTool.dismissDialog();
            ApkUpdateRespone aur = (ApkUpdateRespone) baseRes;
            int s = aur.getStatusCode();
            if (s == 200) {
                if (getVersion() < aur.getDataMap().getCode() && aur.getDataMap().getIsforce() <= 2) {
                    // 有新版本，弹出更新提示
                    showNoticeDialog(aur);
                } else {
                    ToastTool.showShortBigToast(this, "当前已是最新版本!");
                }
            } else {
                ToastTool.showShortBigToast(this, "获取更新包错误");
            }
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        check_version.setClickable(true);
        WaitTool.dismissDialog();
        ToastTool.showShortBigToast(this, "网络异常，请检查您的网络");
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

    private String getVersionName() {
        // 获得一个系统包管理器
        PackageManager pm = getPackageManager();
        // 获得包管理器
        try {
            // 获得功能清单文件
            PackageInfo packInfo = pm.getPackageInfo(getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            // 不可能发生的异常
            return null;
        }
    }

    private void showNoticeDialog(final ApkUpdateRespone aur) {
        // 构造对话框
        if (noticeDialog != null) {
            if (noticeDialog.isShowing()) {
                noticeDialog.dismiss();
            }
        }
        Builder builder = new Builder(this);
        builder.setTitle("有新版本啦");
        builder.setMessage("版本更新内容：" + "\n" + aur.getDataMap().getBei());
        // 更新
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //				Toast.makeText(AboutActivity.this, "后台更新中,请稍后...", Toast.LENGTH_LONG).show();
                // // 显示下载对话框
                downloadApk(aur.getDataMap().getUrl());
            }
        });
        // 稍后更新
        String btnTip;
        if (aur.getDataMap().getIsforce() == 1)
            btnTip = "关闭应用";
        else
            btnTip = "以后再说";
        builder.setNegativeButton(btnTip, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (aur.getDataMap().getIsforce() == 1) {
                    closeApp();
                } else {
                    dialog.dismiss();
                }
            }
        });
        noticeDialog = builder.create();
        noticeDialog.setCanceledOnTouchOutside(false);
        noticeDialog.show();
    }

    private NotificationManager manager;
    private Notification notif;
    private ProgressDialog pd;

    protected void closeApp() {
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    private void downloadApk(String downloadUrl) {
        // 启动新线程下载软件
        new downloadAplThread(downloadUrl).start();
    }

    private String mSavePath;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;
    /* 记录进度条数量 */
    private int progress;
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    // 判断是否更新中
    public static String tag = "1";// 未点击更新
    int downloadCount = 0;
    private int length;

    public class downloadAplThread extends Thread {

        private final String downloadUrl;

        public downloadAplThread(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        @Override
        public void run() {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    String path = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = path + "qingwing/download";
                    String qingwing = "青熠智能保管箱";
                    String encodeString = URLEncoder.encode(qingwing, "UTF-8");
                    String url = downloadUrl.replace(qingwing, encodeString);
                    //					URL urlPath =new URL(sendType.userInfo.url.getDataMap().getUrl());
                    URL urlPath = new URL(url);
                    //创建连接
                    HttpURLConnection conn = (HttpURLConnection) urlPath.openConnection();
                    conn.connect();
                    // 获取文件大小
                    length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();
                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, "qingwingbox.apk");
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        //计算进度条位置
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        // 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
                        //						if (downloadCount==0||progress-5>downloadCount) {
                        //							downloadCount+=5;
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        //						}
                        if (numread <= 0) {
                            // 下载完成
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);
                    fos.close();
                    is.close();
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                //正在下载
                //设置进度条位置
                case DOWNLOAD:
                    if (pd == null) {
                        pd = new ProgressDialog(AboutActivity.this);
                        pd.setTitle("正在下载...");
                        pd.setMax(100);
                        pd.setProgress(progress);
                        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        pd.setCancelable(false);//对话框不能用返回按钮关闭
                        pd.show();
                    } else {
                        pd.setProgress(progress);
                        if (pd.getProgress() >= 100) {
                            pd.dismiss();
                            break;
                        }
                    }
                    break;
                case DOWNLOAD_FINISH:
                    // 安装文件
                    tag = "2";// 更新完成开始安装
                    pd.setProgress(progress);
                    pd.dismiss();
                    pd = null;
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
}
