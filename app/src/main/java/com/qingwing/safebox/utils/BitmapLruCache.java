/**  
 * @Title: BitmapLruCache.java
 * @date: 2015-11-23 下午4:34:18
 * @Copyright: (c) 2015, unibroad.com Inc. All rights reserved.
 */
package com.qingwing.safebox.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

import java.io.File;

/**
 * @Class: BitmapLruCache
 * @author: wsy@unibroad.com
 * @version: V1.0
 */
public class BitmapLruCache implements ImageCache {

    private LruCache<String, Bitmap> cache;

    public BitmapLruCache() {
    	File file = new File(LocationBitmapCacheTools.SAVEPATH);
        if (!file.exists()) {
            file.mkdirs();
        }
     // 分配8M的缓存空间
        cache = new LruCache<String, Bitmap>(8 * 1024 * 1024) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    /**
     * Creates a new instance of BitmapLruCache.
     *
     * @param cacheSize
     */
    public BitmapLruCache(int cacheSize) {
        File file = new File(LocationBitmapCacheTools.SAVEPATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        cache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		if (LocationBitmapCacheTools.isFileExist(url)) {
			return LocationBitmapCacheTools.getCompressBitmap(url);
		}
        return cache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        cache.put(url, bitmap);
        // 保存到本地
        LocationBitmapCacheTools.saveBitmap2PNG(bitmap, url);
    }

    public void clearBitmap(String url){
        try {
            LocationBitmapCacheTools.delFile(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            cache.remove(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}