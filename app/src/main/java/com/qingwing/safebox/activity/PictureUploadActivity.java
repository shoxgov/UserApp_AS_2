package com.qingwing.safebox.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.network.ServerAddress;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.ToastTool;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 从相册中选择头像
 *
 * @author Administrator
 */
public class PictureUploadActivity extends Activity implements OnClickListener {
    // 头像文件名
    private final static String IMAGE_FILE_NAME = UserInfo.userId + ".png";
    private final static String IMAGE_TEMPFILE_NAME = "temp.jpg";

    // 请求识别码 .1为从相册中获取相片 2为拍照的 3为裁剪相片
    private final static int REQUEST_CODE_GETPHOTO = 1;
    private final static int TAKE_PHOTO = 2;
    private final static int PHOTO_REQUEST_CUT = 3;
    //    private Button uploadImg;// 头像上传按钮
    private Button camera = null;// 拍照上传头像
    private Button photo = null;// 从相册中上传头像
    private ImageView headImg = null;// 头像显示的imageview
    private ImageView calback;
    //    private File f;// 裁剪之后的图片存在本地
    private Bitmap picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_upload);
        AcitivityCollector.addActivity(this);
        camera = (Button) this.findViewById(R.id.camera);
        camera.setOnClickListener(this);
        photo = (Button) this.findViewById(R.id.getPhoto);
        photo.setOnClickListener(this);
        //        uploadImg = (Button) findViewById(R.id.uploadImg);
        //        uploadImg.setVisibility(View.GONE);
        //        uploadImg.setOnClickListener(this);
        headImg = (ImageView) this.findViewById(R.id.headImg);
        calback = (ImageView) findViewById(R.id.calback);
        calback.setOnClickListener(this);
        // 将现有的头像显示在imageview中
        QWApplication.getInstance().getImagePicUrl();
        if (!TextUtils.isEmpty(UserInfo.imagePicUrl)) {
            Log.i("haha", "创建获取图片了");
            ImageListener listener = ImageLoader.getImageListener(headImg, R.mipmap.wwj_748,
                    R.mipmap.wwj_748);
            QWApplication.getInstance().getImageLoader().get(UserInfo.imagePicUrl, listener);
        }
    }


    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有内存卡
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        if (picture != null) {
            picture.recycle();
        }
        AcitivityCollector.removeActivity(this);
        super.onDestroy();
    }

    /**
     * 选择上传头像方式的点击事件
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.uploadImg://上传图片

                break;
            case R.id.camera:
                // 启动手机相机拍摄照片作为头像
                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 判断存储卡是否可用,存储照片文件
                if (hasSdcard()) {
                    intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/qingwing/", IMAGE_FILE_NAME)));
                }
                startActivityForResult(intentFromCapture, TAKE_PHOTO);
                break;
            case R.id.getPhoto:
                // 跳转到相册
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                // 如果要限制上传头像的照片格式可以写image/jpg/jpeg/png的类型
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                // 设置返回RequestCode
                startActivityForResult(intent, REQUEST_CODE_GETPHOTO);
                break;
            case R.id.calback:
                PictureUploadActivity.this.finish();
                break;
        }
    }

    private void UploadImg() {
        if (CommUtils.isNetworkAvailable(PictureUploadActivity.this)) {
            File f = new File(Environment.getExternalStorageDirectory() + "/qingwing/", IMAGE_FILE_NAME);
            if (f.exists()) {
                uploadImageToServer();
            } else {
                ToastTool.showShortBigToast(this, "请先选择头像");
            }
        } else {
            ToastTool.showShortBigToast(this, "网络异常，请先连接网络再进行头像上传");
        }
    }

    private void uploadImageToServer() {
        showProgressDialog(this, "正在上传头像，请稍后...");
        String url = ServerAddress.UPLOADIMG;
        RequestParams params = new RequestParams();
        try {
            File f = new File(Environment.getExternalStorageDirectory() + "/qingwing/", IMAGE_FILE_NAME);
            params.put("file", f);
            params.put("fileName", IMAGE_FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        new AsyncHttpClient().post(this, url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1,
                                  byte[] arg2, Throwable arg3) {
                closeProgressDialog();
                ToastTool.showShortBigToast(PictureUploadActivity.this, "网络异常");
                LogUtil.d("haha   访问失败了");
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1,
                                  byte[] arg2) {
                String allInfo = new String(arg2);
                LogUtil.d("haha   图片上传结果:" + allInfo);
                try {
                    JSONObject json = new JSONObject(allInfo);
                    String status = json.getString("status");
                    String message = json.getString("message");
                    if (status.equals("success")) {
                        closeProgressDialog();
                        QWApplication.getInstance().getBitmapCache().clearBitmap(UserInfo.imagePicUrl);
                        QWApplication.getInstance().getBitmapCache().putBitmap(UserInfo.imagePicUrl, picture);
                        ToastTool.showShortBigToast(PictureUploadActivity.this, message);
                    } else {
                        ToastTool.showShortBigToast(PictureUploadActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 用户没有进行有效的设置操作,返回
        if (resultCode == RESULT_CANCELED) {
            //            ToastTool.showToast(this,"取消");
            return;
        }

        switch (requestCode) {
            // 从相册中上传头像
            case REQUEST_CODE_GETPHOTO:
                if (data != null) {
                    Uri uri = data.getData();
                    // 选完图片后进行图片裁剪
                    photoZoom(uri, null);
                } else {
                    ToastTool.showShortBigToast(this, "返回为空");
                }
                break;
            // 图片剪切之后将图片放在imageview上
            case PHOTO_REQUEST_CUT:
                getImageToView(data);
                break;
            // 从照相机中自拍后裁剪获取头像
            case TAKE_PHOTO:
                if (hasSdcard()) {
                    File file = new File(Environment.getExternalStorageDirectory() + "/qingwing/", IMAGE_FILE_NAME);
                    photoZoom(Uri.fromFile(file), null);
                } else {
                    ToastTool.showShortBigToast(this, "没有SDCard");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 裁剪图片之后调用
    private void getImageToView(Intent data) {
        Log.i("haha", "进行裁剪");
        if (data == null) {
            Log.i("haha", "intent data为空");
            return;
        }
        try {
            Uri uri = data.getData();
            Log.i("haha", "裁剪3" + uri);
            if (uri != null) {
                // 读取uri所在的图片
                picture = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } else {
                picture = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/qingwing/" + IMAGE_FILE_NAME);
            }
            if (picture == null) {
                LogUtil.d("哦呵图片是等于空的");
                return;
            }
            headImg.setImageBitmap(picture);
            saveMyBitmap(picture);
            UploadImg();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 裁剪图像
     *
     * @param uri 图片的路径
     *            裁剪的宽高
     */
    private void photoZoom(Uri uri, Uri uri2) {
        Log.i("haha", "裁剪2" + uri);
        // 裁剪图片路径
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启intent中设置显示的view可以裁剪
        intent.putExtra("crop", "true");
        // aspect设置宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高，切忌不要再改动下列数字，会卡死
        intent.putExtra("outputX", 480);//输出X方向的像素
        intent.putExtra("outputY", 480);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);

        //		intent.putExtra(MediaStore.EXTRA_OUTPUT, IMAGE_FILE_NAME);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/qingwing/", IMAGE_FILE_NAME)));
        /*
         * “return-data”设置为了true然后在onActivityResult中通过data.getParcelableExtra("data")来获取数据，
		 * 不过这样的话dp这个变量的值就不能太大了，不然您的程序就挂了
		 */
        //		intent.putExtra("return-data", false);

        // .当裁剪图片小于规定的大小时，会存在毛边
        //		intent.putExtra("scale", true);// 黑边
        //		intent.putExtra("scaleUpIfNeeded", true);// 黑边

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * 保存bitmap到本地文件中
     * <p>
     * 文件名
     *
     * @param mBitmap 图片
     * @throws IOException
     */
    public void saveMyBitmap(Bitmap mBitmap) throws IOException {
        //		File f = new File(Environment.getExternalStorageDirectory()+"/qingwing/" , IMAGE_FILE_NAME);
        File f = new File("/sdcard/qingwing/", IMAGE_FILE_NAME);
        //		if (f.exists()) {
        //			f.delete();
        //		}
        if (!f.exists()) {
            f.createNewFile();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {  // 刷新output
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {  // 关闭output
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 显示对话框
     */
    public ProgressDialog progressDialog;

    // 显示对话框
    public void showProgressDialog(Context context, String string) {
        try {
            if (progressDialog == null && this != null) {
                progressDialog = new ProgressDialog(context);
            }
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(string);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
