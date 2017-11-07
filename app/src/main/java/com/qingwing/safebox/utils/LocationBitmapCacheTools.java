/**  
 * @Title: LocationBitmapCacheTools.java
 * @date: 2015-11-23 下午5:09:33
 * @Copyright: (c) 2015, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @Class: LocationBitmapCacheTools
 * @author: wsy@unibroad.com
 * @version: V1.0
 */
public class LocationBitmapCacheTools {
    public final static String SAVEPATH= Environment.getExternalStorageDirectory()+"/qingwing/ImageCache";
    /**
     * 保存图片到本地,这个是把图片压缩成字节流然后保存到本地，所以本地的图片是无法显示的
     * 
     * @param mBitmap
     * @param imageURL
     */
    public static void saveBitmap2Byte(Bitmap mBitmap, String imageURL) {
        String bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1); // 传入一个远程图片的url，然后取最后的图片名字
      //图片允许最大空间   单位：KB 
        double maxSize =400.00;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
//        //将字节换成KB 
//        double mid = byteArray.length/1024; 
//        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩 
//        if (mid > maxSize) { 
//                //获取bitmap大小 是允许最大大小的多少倍 
//                double i = mid / maxSize; 
//                //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小） 
//                mBitmap = zoomImage(mBitmap, mBitmap.getWidth() / Math.sqrt(i), 
//                        mBitmap.getHeight() / Math.sqrt(i)); 
//        } 
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            File file = new File(SAVEPATH, bitmapName);
            fos = new FileOutputStream(file);
//            fos = cxt.openFileOutput(bitmapName, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(byteArray);
        } catch (Exception e) {
            e.printStackTrace();
            // 这里是保存文件产生异常
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // fos流关闭异常
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    // oos流关闭异常
                    e.printStackTrace();
                }
            }
        }
    }
    /***
     * 图片的缩放方法
     *
     * @param bgimage
     *            ：源图片资源
     * @param newWidth
     *            ：缩放后宽度
     * @param newHeight
     *            ：缩放后高度
     * @return
     */ 
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }
    
    // 通过这种方式保存在本地的图片，是可以看到的
    public static String saveBitmap2PNG(Bitmap mBitmap, String imageURL) {
        String bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1);
        FileOutputStream fos = null;
        String path = imageURL;
        try {
            File file = new File(SAVEPATH, bitmapName);
            fos = new FileOutputStream(file);
//            fos = cxt.openFileOutput(bitmapName, Context.MODE_PRIVATE);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);//如果是JPEG的话，带有透明的PNG压缩会背景显示成黑色
            fos.flush();
            fos.close();
            path = file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            // 这里是保存文件产生异常
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // fos流关闭异常
                    e.printStackTrace();
                }
            }
        }
        return path;
    }

    /**
     * 读取本地私有文件夹的图片
     * 
     * @return
     */
    public static Bitmap getBitmap2Byte(String fileName) {
        String bitmapName = fileName.substring(fileName.lastIndexOf("/") + 1);
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            File file = new File(SAVEPATH, bitmapName);
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            byte[] byteArray = (byte[]) ois.readObject();
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            // 这里是读取文件产生异常
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // fis流关闭异常
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    // ois流关闭异常
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getBitmapPath(String url){
        try {
            String bitmapName = url.substring(url.lastIndexOf("/") + 1);
            File file = new File(SAVEPATH, bitmapName);
            return file.getAbsolutePath();
        } catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }

    public static Bitmap getBitmap2PNG(String fileName) {
        String bitmapName = fileName.substring(fileName.lastIndexOf("/") + 1);
        FileInputStream fis = null;
        try {
            File file = new File(SAVEPATH, bitmapName);
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            fis.close();
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            // 这里是读取文件产生异常
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // fis流关闭异常
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param fileName
     * @return
     * @Description:获取压缩的图片
     */
    public static Bitmap getCompressBitmap(String fileName) {
        String bitmapName = fileName.substring(fileName.lastIndexOf("/") + 1);
        File file = new File(SAVEPATH, bitmapName);
        Bitmap bitmap = null;
        int maxHeight = 300;
        //////////////////////////
//		InputStream inputStream = null;
//		try {
//			AssetManager assetManager = BXApplication.getInstance().getAssets();;
//			inputStream = assetManager.open(file.getPath());
//			bitmap = BitmapFactory.decodeStream(inputStream);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return bitmap;
        ///////////////////////////
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFile(file.getPath(), opts);
            opts.inJustDecodeBounds = false;// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
            // 计算缩放比
			int h = opts.outHeight;
			int w = opts.outWidth;
			int beWidth = w / maxHeight;
			int beHeight = h / maxHeight;
			int be = 1;
			if (beWidth < beHeight && beHeight >= 1) {
				be = beHeight;
			}
			if (beHeight < beWidth && beWidth >= 1) {
				be = beWidth;
			}
			if (be <= 0)
				be = 1;
            opts.inSampleSize = be;
            bitmap = BitmapFactory.decodeFile(file.getPath(), opts);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
    /**
     * 判断本地的私有文件夹里面是否存在当前名字的文件
     */
	public static boolean isFileExist(String fileName) {
		if (TextUtils.isEmpty(fileName)) {
			return false;
		}
		String bitmapName = fileName.substring(fileName.lastIndexOf("/") + 1);
		File file = new File(SAVEPATH);
		if (file.list() == null || file.list().length == 0) {
			return false;
		}
		List<String> nameLst = Arrays.asList(file.list());
		if (nameLst.contains(bitmapName)) {
			return true;
		} else {
			return false;
		}
	}
    
	/**
	 * 清空缓存
	 */
	public static void clearCacheFile() {
		File file = new File(SAVEPATH);
		try {
			if (file.isFile()) {
				file.delete();
				return;
			}

			if (file.isDirectory()) {
				File[] childFiles = file.listFiles();
				if (childFiles == null || childFiles.length == 0) {
					file.delete();
					return;
				}

				for (int i = 0; i < childFiles.length; i++) {
					childFiles[i].delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * @param url
     * @Description:清除缓存文件
     */
    public static void delFile(String url){
        try {
            String bitmapName = url.substring(url.lastIndexOf("/") + 1); // 传入一个远程图片的url，然后取最后的图片名字
            File file = new File(SAVEPATH, bitmapName);
            if(file!= null && file.exists()){
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
