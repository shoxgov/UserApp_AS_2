package com.qingwing.safebox.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.network.ServerAddress;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.AndroidShare;
import com.qingwing.safebox.utils.ToastTool;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Hashtable;

import cn.sharesdk.framework.ShareSDK;

/**
 * 分享获取优惠界面
 */
public class ShareActivity extends Activity {
    // 生成二维码图片存放控件
    private ImageView img;
    // 二维码的标识符
    private static final int PHOTO_PIC = 1;
    // 优惠码
    private TextView txt;
    // 分享的按钮
    private Button btn_share;
    private String title, content, url, imgUrl;
    private Bitmap bmp;
    //	// 微信分享按钮
    //	private Button btn_weixin;
    private ImageView calback;
    public Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    ToastTool.showShortBigToast(ShareActivity.this, "分享取消！");
                    break;
                case 2:
                    ToastTool.showShortBigToast(ShareActivity.this, "分享成功！");
                    break;
                case 3:
                    ToastTool.showShortBigToast(ShareActivity.this, "分享失败！");
                    break;
                case 4:
                    // 显示图片
                    img.setImageBitmap(bmp);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private void setData() {
        title = "智能保管箱推荐有奖！";
        content = "下载app直接送优惠啦~小伙伴们，赶紧行动起来！！";
        url = ServerAddress.SHARE_CENTER;
        new Thread(new Runnable() {
            public void run() {
                // 生成二维码并将二维码存放到对应的imageview之中(这里是根据accountid生成相应的二维码)
                createImage(url);
            }
        }).start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        AcitivityCollector.addActivity(this);
        ShareSDK.initSDK(this);
        initView();
        setData();

        // 将用户id作为优惠码存放到对应的textview之中
        txt.setText(UserInfo.requestCode);

        calback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ShareActivity.this.finish();
                overridePendingTransition(0, R.anim.out);
            }
        });

        // 点击分享给更多好友回调方法
        btn_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 需要分享的内容
                AndroidShare as = new AndroidShare(ShareActivity.this, handler, url,
                        Environment.getExternalStorageDirectory() + "/qingwing/myShareCode.jpg");
                as.show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AcitivityCollector.removeActivity(this);
    }

    private void initView() {
        // 获得所需控件
        img = (ImageView) findViewById(R.id.img_code);
        txt = (TextView) findViewById(R.id.txt_code);
        btn_share = (Button) findViewById(R.id.btn_share);
        //		btn_weixin = (Button) findViewById(R.id.btn_weixin);
        calback = (ImageView) findViewById(R.id.calback);
    }


    private Options getBitmapOption(int inSampleSize) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    /**
     * 要转换的地址或者字符串,可以是中文
     */
    public void createImage(String url) {
        try {
            // 判断URL的合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return;
            }
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            // hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 设置二维码和边框的距离
            hints.put(EncodeHintType.MARGIN, 1);
            // 设置二维码的容错率,容错率等级越高,二维码的黑色密度越大
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);

            // 图像数据转换,使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, 500, 500, hints);
            int[] pixels = new int[500 * 500];
            // 下面这里按照二维码的算法,诸葛生成二维码的图片
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < 500; y++) {
                for (int x = 0; x < 500; x++) {
                    // 取出这点判断是白色还是黑色
                    if (bitMatrix.get(x, y)) {
                        pixels[y * 500 + x] = 0xff000000;
                    } else {
                        pixels[y * 500 + x] = 0xffffffff;
                    }
                }
            }

            // 生成二维码图片的格式,使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, 500, 0, 0, 500, 500);
            // //给二维码加个logo
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 8;   // width，hight设为原来的十分一
            Bitmap logoBmp = BitmapFactory.decodeResource(getResources(), R.mipmap.qingzhiyi, options);
            float scaleFactor = bitmap.getWidth() * 1.0f / 4 / logoBmp.getWidth();
            //
            Canvas canvas = new Canvas(bitmap);
            // 调整图片大小
            canvas.scale(scaleFactor, scaleFactor, bitmap.getWidth() / 2, bitmap.getHeight() / 2);

            canvas.drawBitmap(logoBmp, bitmap.getWidth() / 2 - logoBmp.getWidth() / 2,
                    bitmap.getHeight() / 2 - logoBmp.getHeight() / 2, null);

            // 显示到一个Imageview上面
            // img.setImageBitmap(bitmap);

            bmp = bitmap;
            handler.sendEmptyMessage(4);
            // 保存二维码图到手机内存里
            saveCode(bmp);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    @Override
    public void finish() {
        super.finish();
        if (bmp != null && !bmp.isRecycled())
            bmp.recycle();
    }

    /**
     * 保存二维码
     *
     * @param bitmap
     */
    private void saveCode(Bitmap bitmap) {
        // 创建文件夹
        String fileName = "myShareCode.jpg";
        String path = Environment.getExternalStorageDirectory() + "/qingwing/";

        File myFile = new File(path);
        if (!myFile.exists()) {
            // 必须前面的目录存在才能生成后面的目录
            myFile.mkdir();
        }
        File file = new File(path + fileName);

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            // 80为压缩率，压缩20%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_OK) {
            switch (requestCode) {
                case PHOTO_PIC:
                    String result = data.getExtras().getString("result");
                    String str = result.substring(0, 7);
                    if (str.equals("http://")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
                        startActivity(intent);
                    }
                    break;
            }
        }
    }

}
