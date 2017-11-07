package com.qingwing.safebox;

import android.app.Application;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.utils.BitmapLruCache;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;

public class QWApplication extends Application {
    private static QWApplication instance;
    public static int screenWidthPixels;
    public static int screenHeightPixels;
    /**
     * 全局统一一个实例，节省资源
     */
    private RequestQueue mRequestQueue;
    /**
     * mImageLoader: 网络加载图片并缓存
     */
    private ImageLoader mImageLoader;
    /**
     * bitmapCache: 图片缓存工具
     */
    private BitmapLruCache bitmapCache;
    /**
     * mPreferences:
     */
    public static SharedPreferences mPreferences;
    /**
     * lastTime: listview下拉刷新的时间
     */
    public static String lastTime = "";

    //errorCount记录开箱密码错误次数
    public static int openboxErrorCount = 0;
    //开箱密码错误5次后 倒计时锁键180秒
    public static long openboxErrorMillis = 0;

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            synchronized (QWApplication.class) {
                if (mRequestQueue == null) {
                    mRequestQueue = Volley.newRequestQueue(getApplicationContext());
                }
            }
        }
        return mRequestQueue;
    }

    /**
     * @return
     * @Description:图片加载网络
     */
    public ImageLoader getImageLoader() {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(getRequestQueue(), getBitmapCache());
        }
        return mImageLoader;
    }

    public BitmapLruCache getBitmapCache() {
        if (bitmapCache == null) {
            bitmapCache = new BitmapLruCache();
        }
        return bitmapCache;
    }

    public static QWApplication getInstance() {
        if (instance == null) {
            instance = new QWApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //加载本地初始化数据
        loadUserData();
//        Intent intent = new Intent(this, BluetoothService.class);
//        if (UserInfo.UserBindState) {
//            intent.putExtra("command", BluetoothService.START_SCAN_BT);
//            BluetoothService.isScanning = false;
//        }
//        startService(intent);
    }

    private void loadUserData() {
        mPreferences = getSharedPreferences("QingWing_SP", MODE_PRIVATE);
        UserInfo.BlueId = mPreferences.getString("BlueId", "");
        UserInfo.isOverDate = mPreferences.getBoolean("isOverDate", true);//默认是欠费状态
        UserInfo.UserBindState = mPreferences.getBoolean("UserBindState", false);
        UserInfo.UserOpenBoxPassowrd = mPreferences.getString("UserOpenBoxPassowrd", "");
        UserInfo.requestCode = mPreferences.getString("requestCode", "");
        UserInfo.isLoginSuccess = mPreferences.getBoolean("isLoginSuccess", false);
        UserInfo.mobile = mPreferences.getString("mobile", "");
        UserInfo.userLoginPassword = mPreferences.getString("LoginPassword", "");
        UserInfo.onlineCode = mPreferences.getString("onlineCode", "");
        //////////加载User_info的相关信息
        UserInfo.lock_style = mPreferences.getString("lock_style", "c");//a:手势   b:指纹  c:数字
        UserInfo.lock_password = mPreferences.getString("lock_password", "");
        UserInfo.account_Id = mPreferences.getString("account_Id", "");
        UserInfo.userId = mPreferences.getInt("userId", 0);
        UserInfo.starttime = mPreferences.getString("starttime", "");
        UserInfo.endstringtime = mPreferences.getString("endstringtime", "");
        UserInfo.recordListData = mPreferences.getString("RecordList", "");
        UserInfo.imagePicUrl = mPreferences.getString("imagePicUrl", "");
        //////////////////
        openboxErrorCount = mPreferences.getInt("openboxErrorCount", 0);
        openboxErrorMillis = mPreferences.getLong("openboxErrorMillis", 0L);
        LogUtil.d("UserInfo.UserBindState=" + UserInfo.UserBindState);
        LogUtil.d("UserInfo.isOverDate=" + UserInfo.isOverDate);
        LogUtil.d("UserInfo.BlueId=" + UserInfo.BlueId);
        LogUtil.d("UserInfo.UserOpenBoxPassowrd=" + UserInfo.UserOpenBoxPassowrd);
        LogUtil.d("UserInfo.requestCode=" + UserInfo.requestCode);
        LogUtil.d("UserInfo.isLoginSuccess=" + UserInfo.isLoginSuccess);
        LogUtil.d("UserInfo.mobile=" + UserInfo.mobile);
        LogUtil.d("UserInfo.userLoginPassword=" + UserInfo.userLoginPassword);
        LogUtil.d("UserInfo.onlineCode=" + UserInfo.onlineCode);
        LogUtil.d("UserInfo.lock_style=" + UserInfo.lock_style + ",   a:手势   b:指纹  c:数字");
        LogUtil.d("UserInfo.lock_password=" + UserInfo.lock_password);
        LogUtil.d("UserInfo.account_Id=" + UserInfo.account_Id);
        LogUtil.d("UserInfo.userId=" + UserInfo.userId);
        LogUtil.d("UserInfo.starttime=" + UserInfo.starttime);
        LogUtil.d("UserInfo.endstringtime=" + UserInfo.endstringtime);
        LogUtil.d("UserInfo.recordListData=" + UserInfo.recordListData);
        LogUtil.d("UserInfo.imagePicUrl=" + UserInfo.imagePicUrl);
        LogUtil.d("openboxErrorCount=" + openboxErrorCount);
        LogUtil.d("openboxErrorMillis=" + openboxErrorMillis);
    }

    public void getImagePicUrl() {
        // 获取头像
        if (UserInfo.userId != 0) {
            UserInfo.imagePicUrl = "http://www.keenzy.cn/depositbox/images/" + UserInfo.userId + ".png";
        } else {
            UserInfo.imagePicUrl = "";
        }
    }

    public void loadUserImage() {
        if (CommUtils.isNetworkAvailable(this) && initImagePicUrl()) {
            // 如果在别的手机更改了，原来的手机仍然不会更新，所以每次还是刷新下吧
            long freshTime = mPreferences.getLong("freshTime", 0L);
            long time = 1000 * 60 * 60 * 48L;// 一整天
            if (Math.abs(freshTime - System.currentTimeMillis()) > time || CommUtils.isWifi(this)) {//大于一整天就删掉刷新一次
                getBitmapCache().clearBitmap(UserInfo.imagePicUrl);
            }
            getImageLoader().get(UserInfo.imagePicUrl, new ImageLoader.ImageListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                }

                @Override
                public void onResponse(ImageLoader.ImageContainer ic, boolean arg1) {
                    if (ic.getBitmap() != null) {
                        mPreferences.edit().putLong("freshTime", System.currentTimeMillis()).commit();
                    }
                }
            });
        }
    }

    private boolean initImagePicUrl() {
        // 获取头像
        if (UserInfo.userId != 0) {
            UserInfo.imagePicUrl = "http://www.keenzy.cn/depositbox/images/" + UserInfo.userId + ".png";
            mPreferences.edit().putString("imagePicUrl", UserInfo.imagePicUrl).commit();
            return true;
        } else {
            UserInfo.imagePicUrl = "";
            mPreferences.edit().putString("imagePicUrl", UserInfo.imagePicUrl).commit();
            return false;
        }
    }
}
