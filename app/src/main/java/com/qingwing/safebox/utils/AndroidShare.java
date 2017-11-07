package com.qingwing.safebox.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qingwing.safebox.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;

public class AndroidShare extends Dialog implements AdapterView.OnItemClickListener, PlatformActionListener {
    /**
     * 消息回调名柄
     */
    private Handler handler;
    private LinearLayout mLayout;
    private GridView mGridView;
    private float mDensity;
    private String url	;
    private String mImgPath;
    private int mScreenOrientation;
    private List<ShareItem> mListData;
    private Context mContext;
    private Handler mHandler = new Handler();

    private Runnable work = new Runnable() {
        public void run() {
            int orient = getScreenOrientation();
            if (orient != mScreenOrientation) {
                if (orient == 0)
                    mGridView.setNumColumns(4);
                else {
                    mGridView.setNumColumns(6);
                }
                mScreenOrientation = orient;
                ((AndroidShare.MyAdapter) mGridView.getAdapter()).notifyDataSetChanged();
            }
            mHandler.postDelayed(this, 1000L);
        }
    };

    public AndroidShare(Context context) {
        super(context, R.style.shareDialogTheme);
    }

    private String initImagePath(String imageUri) {
        try {
            String cachePath = Environment.getExternalStorageDirectory().getPath();
            String path = cachePath + "share.png";
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
                Bitmap pic = BitmapFactory.decodeFile(imageUri);
                FileOutputStream fos = new FileOutputStream(file);
                pic.compress(CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
            return path;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }

    }
    public AndroidShare(Context context, int theme) {
        super(context, theme);
    }

    public AndroidShare(Context context, Handler handler, String url, String imgUri) {
        super(context, R.style.shareDialogTheme);
        this.mContext=context;
        this.handler = handler;
        this.url=url;
        this.mImgPath = initImagePath(imgUri);
    }


    void init(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        this.setCanceledOnTouchOutside(true);
        this.mDensity = dm.density;
        Log.d("AndroidShare"," mDensity等于："+mDensity);
        Log.d("AndroidShare"," 像素密度等于："+dm.densityDpi);
        this.mListData = new ArrayList<ShareItem>();
        this.mListData.add(new ShareItem("微信", R.mipmap.logo_wechat,
                "com.tencent.mm.ui.tools.ShareImgUI", "com.tencent.mm"));
        this.mListData.add(new ShareItem("朋友圈", R.mipmap.logo_wechatmoments,
                "com.tencent.mm.ui.tools.ShareToTimeLineUI", "com.tencent.mm"));
        this.mListData.add(new ShareItem("QQ", R.mipmap.logo_qq,
                "com.tencent.mobileqq.activity.JumpActivity","com.tencent.mobileqq"));
        this.mLayout = new LinearLayout(context);
        this.mLayout.setOrientation(1);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.leftMargin = ((int) (10.0F * this.mDensity));
        params.rightMargin = ((int) (10.0F * this.mDensity));
        this.mLayout.setLayoutParams(params);
        this.mLayout.setBackgroundColor(Color.parseColor("#D9DEDF"));

        this.mGridView = new GridView(context);
        this.mGridView.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        this.mGridView.setGravity(17);
        this.mGridView.setHorizontalSpacing((int) (10.0F * this.mDensity));
        this.mGridView.setVerticalSpacing((int) (10.0F * this.mDensity));
        this.mGridView.setStretchMode(1);
        this.mGridView.setColumnWidth((int) (90.0F * this.mDensity));
        this.mGridView.setHorizontalScrollBarEnabled(false);
        this.mGridView.setVerticalScrollBarEnabled(false);
        this.mLayout.addView(this.mGridView);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getContext();
        init(context);
        setContentView(this.mLayout);

        getWindow().setGravity(80);

        if (getScreenOrientation() == 0) {
            this.mScreenOrientation = 0;
            this.mGridView.setNumColumns(4);
        } else {
            this.mGridView.setNumColumns(6);
            this.mScreenOrientation = 1;
        }
        this.mGridView.setAdapter(new MyAdapter());
        this.mGridView.setOnItemClickListener(this);

        this.mHandler.postDelayed(this.work, 1000L);

        setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                mHandler.removeCallbacks(work);
            }
        });
    }

    public void show() {
        super.show();
    }

    public int getScreenOrientation() {
        int landscape = 0;
        int portrait = 1;
        Point pt = new Point();
        getWindow().getWindowManager().getDefaultDisplay().getSize(pt);
        int width = pt.x;
        int height = pt.y;
        return width > height ? portrait : landscape;
    }


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		ShareItem share = (ShareItem) this.mListData.get(position);
//		shareMsg(getContext(), "分享到...", this.msgText, this.mImgPath, share);
        switch (position) {
            case 0:
                //微信
                Platform wx=ShareSDK.getPlatform("Wechat");
                if (!wx.isClientValid()) {
                    ToastTool.showShortBigToast(mContext, "请下载微信客户端!");
                    return ;
                }
                wx.setPlatformActionListener(this);
                cn.sharesdk.wechat.friends.Wechat.ShareParams wechatParam = new cn.sharesdk.wechat.friends.Wechat.ShareParams();
                wechatParam.shareType = Platform.SHARE_WEBPAGE;
                wechatParam.title="智能保管箱推荐有奖！";
                wechatParam.text = "下载app直接送优惠啦~小伙伴们，赶紧行动起来！！";
                wechatParam.imagePath = mImgPath;
                wechatParam.url = url;
                wx.share(wechatParam);
                break;
            case 1:
                //朋友圈
                Platform pyq = ShareSDK.getPlatform("WechatMoments");
                if (!pyq.isClientValid()) {
                    ToastTool.showShortBigToast(mContext, "请下载微信客户端");
                    return ;
                }
                pyq.setPlatformActionListener(this);
                cn.sharesdk.wechat.moments.WechatMoments.ShareParams pyqParam = new cn.sharesdk.wechat.moments.WechatMoments.ShareParams();
                pyqParam.shareType = Platform.SHARE_WEBPAGE;
                pyqParam.title="智能保管箱推荐有奖！";
                pyqParam.text = "下载app直接送优惠啦~小伙伴们，赶紧行动起来！！";
                pyqParam.imagePath = mImgPath;
                pyqParam.url = url;
                pyq.share(pyqParam);
                break;
            case 2:
                //QQ
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                qq.setPlatformActionListener(this);
                cn.sharesdk.tencent.qq.QQ.ShareParams qqParam = new cn.sharesdk.tencent.qq.QQ.ShareParams();
                qqParam.title="智能保管箱推荐有奖！";
                qqParam.text = "下载app直接送优惠啦~小伙伴们，赶紧行动起来！！";
                qqParam.imagePath = mImgPath;
                qqParam.titleUrl = url;
                qq.share(qqParam);
                break;
            default:
                break;
        }
    }


    public String getImagePath(String imageUrl, File cache) throws Exception {
        String name = imageUrl.hashCode() + imageUrl.substring(imageUrl.lastIndexOf("."));
        File file = new File(cache, name);

        if (file.exists()) {
            return file.getAbsolutePath();
        }

        URL url = new URL(imageUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        if (conn.getResponseCode() == 200) {
            InputStream is = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            is.close();
            fos.close();

            return file.getAbsolutePath();
        }

        return null;
    }

    private final class MyAdapter extends BaseAdapter {
        private static final int image_id = 256;
        private static final int tv_id = 512;

        public MyAdapter() {
        }

        public int getCount() {
            return mListData.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0L;
        }

        private View getItemView() {
            LinearLayout item = new LinearLayout(getContext());
            item.setOrientation(1);
            int padding = (int) (10.0F * mDensity);
            item.setPadding(padding, padding, padding, padding);
            item.setGravity(17);

            ImageView iv = new ImageView(getContext());
            item.addView(iv);
            iv.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
            iv.setId(image_id);

            TextView tv = new TextView(getContext());
            item.addView(tv);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
            layoutParams.topMargin = ((int) (5.0F * mDensity));
            tv.setLayoutParams(layoutParams);
            tv.setTextColor(Color.parseColor("#212121"));
            tv.setTextSize(16.0F);
            tv.setId(tv_id);

            return item;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getItemView();
            }
            ImageView iv = (ImageView) convertView.findViewById(image_id);
            TextView tv = (TextView) convertView.findViewById(tv_id);
            AndroidShare.ShareItem item = (AndroidShare.ShareItem) mListData.get(position);
            iv.setImageResource(item.logo);
            tv.setText(item.title);
            return convertView;
        }
    }


    public class ShareItem {
        String title;
        int logo;
        String activityName;
        String packageName;

        public ShareItem(String title, int logo, String activityName, String packageName) {
            this.title = title;
            this.logo = logo;
            this.activityName = activityName;
            this.packageName = packageName;
        }
    }


    @Override
    public void onCancel(Platform plat, int action) {
        Message msg = new Message();
        msg.what = 1;
        msg.arg2 = action;
        msg.obj = plat;
        handler.sendMessage(msg);
    }

    @Override
    public void onComplete(Platform plat, int action, HashMap<String, Object> arg2) {
        Message msg = new Message();
        msg.what = 2;
        msg.arg2 = action;
        msg.obj = plat;
        handler.sendMessage(msg);
    }

    @Override
    public void onError(Platform plat, int action, Throwable arg2) {
        Message msg = new Message();
        msg.what = 3;
        msg.arg2 = action;
        msg.obj = plat;
        handler.sendMessage(msg);
    }
}
