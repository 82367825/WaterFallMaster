package com.zero.waterfalllib.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;


import com.zero.waterfalllib.cache.executor.WFThreadPoolProxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ImageLoader 
 * 
 * the ImageLoader uses the ThreadFactory to create tasks that loading bitmap and execute them.
 * the cache system of the ImageLoader include memory cache and disk cache. 
 * If the bitmap has been cached in the cache system, it will return the bitmap that in the cache.
 * If the bitmap are not in the cache system, it will start a task to loading from network or 
 * loading from local files.
 * 
 * 内存缓存+磁盘缓存+网络读取/本地读取
 * 
 * @author lin
 * @version 1.0
 * @date 2015/11/26
 */
public class ImageLoader {
    private static final String TAG = "ImageLoader";
    private ImageCache mImageCache;
    private MainHandler mMainHandler;
    
    /**
     * loading from network or loading from local files
     * the way of loading the bitmap when you can not get bitmap from the cache.
     */
    public enum LOAD_WAY{
        LOAD_LOCAL,
        LOAD_NETWORK
    }
    /**
     * default way of loading bitmap
     */
    private static final LOAD_WAY DEFAULT_LOAD_WAY = LOAD_WAY.LOAD_NETWORK;
    /**
     * way of loading bitmap
     */
    private static LOAD_WAY LOAD_WAY_NOW = DEFAULT_LOAD_WAY;
    /**
     * default params: if the mem cache is enabled
     */
    private static final boolean DEFAULT_MEN_CACHE_ENABLED = true;
    /**
     * default params: if the disk cache is enabled
     */
    private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;

    // Default file name of the disk cache
    private static final String DEFAULT_DISK_CACHE_NAME = "images";      //图片缓存文件名，默认为"images"
    // Default memory cache size in kilobytes
    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 5;          // 5MB  内存缓存大小
    // Default disk cache size in bytes
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB  磁盘缓存大小
    
    private static final int MESSAGE_POST_RESULT = 1;
    
    /**
     * the context for the ImageLoader
     */
    private static Context mContext;
    
    /**
     * 单一实例
     */
    private static ImageLoader mInstance;

    /**
     * 单例模式，因此构造方法为私有
     */
    private ImageLoader() {
    }

    /**
     * 初始化ImageLoader
     * @param context
     * @param imageLoaderBuilder
     */
    public void init(Context context,ImageLoaderBuilder imageLoaderBuilder) {
        mContext = context;
        mMainHandler = new MainHandler();
        init(imageLoaderBuilder);
    }

    /**
     * 初始化
     * @param imageLoaderBuilder
     */
    private void init(ImageLoaderBuilder imageLoaderBuilder){
        ImageCache.ImageCacheParams imageCacheParams = new ImageCache.ImageCacheParams();
        imageCacheParams.DISK_CACHE_ENABLED = imageLoaderBuilder.getDISK_CACHE_ENABLED();
        imageCacheParams.MEN_CACHE_ENABLED = imageLoaderBuilder.getMEM_CACHE_ENABLED();
        imageCacheParams.DISK_CACHE_NAME = imageLoaderBuilder.getDISK_CACHE_NAME();
        imageCacheParams.DISK_CACHE_SIZE = imageLoaderBuilder.getDISK_CACHE_SIZE();
        imageCacheParams.MEM_CACHE_SIZE = imageLoaderBuilder.getMEM_CACHE_SIZE();
        mImageCache = new ImageCache(mContext,imageCacheParams);
        
        LOAD_WAY_NOW = imageLoaderBuilder.getLOAD_WAY_NOW();
    }


    /**
     * Builder for the ImageLoader
     */
    public static class ImageLoaderBuilder{
        private final boolean MEN_CACHE_ENABLED = DEFAULT_MEN_CACHE_ENABLED;
        private boolean DISK_CACHE_ENABLED = DEFAULT_DISK_CACHE_ENABLED;
        private String DISK_CACHE_NAME = DEFAULT_DISK_CACHE_NAME;
        private int MEM_CACHE_SIZE = DEFAULT_MEM_CACHE_SIZE;
        private int DISK_CACHE_SIZE = DEFAULT_DISK_CACHE_SIZE;
        private LOAD_WAY LOAD_WAY_NOW = DEFAULT_LOAD_WAY;

        public ImageLoaderBuilder(){
        }
        public ImageLoaderBuilder setMEN_CACHE_ENABLED(boolean MEN_CACHE_ENABLED){
            this.DISK_CACHE_ENABLED = MEN_CACHE_ENABLED;
            return this;
        }
        public ImageLoaderBuilder setDISK_CACHE_ENABLED(boolean DISK_CACHE_ENABLED){
            this.DISK_CACHE_ENABLED = DISK_CACHE_ENABLED;
            return this;
        }
        public ImageLoaderBuilder setDISK_CACHE_NAME(String disk_cache_name){
            this.DISK_CACHE_NAME = disk_cache_name;
            return this;
        }
        public ImageLoaderBuilder setMEM_CACHE_SIZE(int MEM_CACHE_SIZE){
            this.MEM_CACHE_SIZE = MEM_CACHE_SIZE;
            return this;
        }
        public ImageLoaderBuilder setDISK_CACHE_SIZE(int DISK_CACHE_SIZE){
            this.DISK_CACHE_SIZE = DISK_CACHE_SIZE;
            return this;
        }
        public ImageLoaderBuilder setLOAD_WAY_NOW(LOAD_WAY LOAD_WAY_NOW){
            this.LOAD_WAY_NOW = LOAD_WAY_NOW;
            return this;
        }
        public boolean getMEM_CACHE_ENABLED(){
            return this.MEN_CACHE_ENABLED;
        }
        public boolean getDISK_CACHE_ENABLED(){
            return this.DISK_CACHE_ENABLED;
        }
        public int getMEM_CACHE_SIZE(){
            return this.MEM_CACHE_SIZE;
        }
        public int getDISK_CACHE_SIZE(){
            return this.DISK_CACHE_SIZE;
        }
        public LOAD_WAY getLOAD_WAY_NOW(){
            return this.LOAD_WAY_NOW;
        }
        public String getDISK_CACHE_NAME(){
            return this.DISK_CACHE_NAME;
        }
    }

    /**
     * MainHandler 关联主线程的Handler
     * 用于接收线程池中线程的消息
     */
    private static class MainHandler extends Handler{
        public void handleMessage(Message msg) {
            LoaderResult loaderResult = (LoaderResult) msg.obj;
            ImageView imageView = loaderResult.imageView;
            ImageLoadListener imageLoadListener = loaderResult.mImageLoadListener;

            if (imageView != null) {
                if (loaderResult.bitmap != null) {
                    imageView.setImageBitmap(loaderResult.bitmap);
                    Log.d(TAG, "LoadBitmap To ImageView Success");
                } else {
                    imageView.setImageResource(loaderResult.ErrorBitmap);
                    Log.d(TAG, "LoadBitmap To ImageView Fail");
                }

            } else if (imageLoadListener != null) {
                if (loaderResult.bitmap == null) {
                    imageLoadListener.onFail(loaderResult.url);
                    Log.d(TAG, "LoadBitmap To Listener Success");
                } else {
                    imageLoadListener.onSuccess(loaderResult.url, loaderResult.bitmap);
                    Log.d(TAG, "LoadBitmap To Listener Fail");
                }
            }
        }
    }
    
    /**
     * ImageLoad输出到日志
     * 通常到活动暂停时，用于刷新cache
     */
    public void ImageLoaderPause() {
        mImageCache.flushCache();
    }

    /**
     * ImageLoad获取磁盘缓存的大小
     */
    public long ImageLoaderCacheSize() {
        return mImageCache.getDiskCacheSize();
    }

    /**
     * ImageLoad清理内存缓存
     */
    public void ImageLoaderCacheClean() {
        mImageCache.clearMenCache();
    }

    /**
     * ImageLoad磁盘缓存清理
     */
    public void ImageLoaderDiskCacheClean() {
        mImageCache.clearDiskCache();
    }
    
    /**
     * 获取单一实例
     *
     * @return
     */
    public static ImageLoader getInstance() {
        if (mInstance == null) {
            mInstance = new ImageLoader();
        }
        return mInstance;
    }

    /************************************* 公开方法 start ***************************************/
    
    /**
     * 公开方法 获取图片
     *
     * @param url
     * @param imageLoadListener
     */
    public void loadBitmap(String url, ImageLoadListener imageLoadListener) {
        LoaderRunnable loaderRunnable = new LoaderRunnable(url, null, 0, 0, -1, imageLoadListener);
        WFThreadPoolProxy.getInstance().execute(loaderRunnable);
    }

    /**
     * 公开方法 获取图片
     *
     * @param url
     * @param imageView
     */
    public void loadBitmap(String url, ImageView imageView, int errorBitmap) {
        LoaderRunnable loaderRunnable = new LoaderRunnable(url, imageView, 0, 0, errorBitmap, null);
        WFThreadPoolProxy.getInstance().execute(loaderRunnable);
    }

    /**
     * 公开方法 获取指定宽度的图片
     * @param url
     * @param defaultWidth
     * @param imageLoadListener
     */
    public void loadBitmapWithWidth(String url, int defaultWidth, ImageLoadListener imageLoadListener) {
        LoaderRunnable loaderRunnable = new LoaderRunnable(url, null, -1, defaultWidth, 0, 
                imageLoadListener);
        WFThreadPoolProxy.getInstance().execute(loaderRunnable);
    }

    /**
     * 公开方法 获取指定高度的图片
     * @param url
     * @param defaultHeight
     * @param imageLoadListener
     */
    public void loadBitmapWithHeight(String url, int defaultHeight, ImageLoadListener imageLoadListener) {
        LoaderRunnable loaderRunnable = new LoaderRunnable(url, null, -1, 0, defaultHeight, 
                imageLoadListener);
        WFThreadPoolProxy.getInstance().execute(loaderRunnable);
    }

    /**
     * 公开方法 获取制定宽度和高度的图片
     * @param url
     * @param defaultWidth
     * @param defaultHeight
     * @param imageLoadListener
     */
    public void loadBitmapWithSize(String url, int defaultWidth, int defaultHeight, 
                                   ImageLoadListener imageLoadListener) {
        LoaderRunnable loaderRunnable = new LoaderRunnable(url, null, -1, defaultWidth, defaultHeight, 
                imageLoadListener);
        WFThreadPoolProxy.getInstance().execute(loaderRunnable);
    }
    
    /************************************* 公开方法 end ***************************************/
    
    /**
     * 同步方法loadBitmap 获取Bitmap
     *
     * @param imageUrl
     * @return
     */
    private Bitmap loadBitmap(String imageUrl, int defaultWidth, int defaultHeight) {
        
        if(mImageCache == null){
            Log.e(TAG,"the ImageLoader has not init.");
            return null;
        }
        Bitmap bitmap = mImageCache.loadBitmapFromCache(imageUrl);
        if(bitmap != null){
            Log.v(TAG,"READ FROM CACHE");
            return bitmap;
        }
        if(LOAD_WAY_NOW == LOAD_WAY.LOAD_NETWORK){
            try {
                bitmap = downloadFromNetwork(imageUrl, defaultWidth, defaultHeight);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(bitmap != null) {
                Log.v(TAG,"READ FROM NETWORK");
                mImageCache.addBitmapToDiskCache(imageUrl, bitmap);
            }
        }else{
            bitmap = downloadFromLocal(defaultWidth, defaultHeight, imageUrl);
            if(bitmap != null){
                Log.v(TAG,"READ FROM LOCAL");
                mImageCache.addBitmapToDiskCache(imageUrl, bitmap);
            }
        }
        return bitmap;
    }


    /**
     * 图片加载监听接口
     */
    public interface ImageLoadListener {
        
        void onSuccess(String url, Bitmap bitmap);

        void onFail(String url);
    }

    /**
     * 线程池任务类
     */
    public class LoaderRunnable implements Runnable {
        private LoaderResult loaderResult = null;
        private int defaultWidth;    //外部指定的宽度
        private int defaultHeight;   //外部指定的高度
        @Override
        public void run() {
            //开始读取图片
            Bitmap bitmap = loadBitmap(loaderResult.url, defaultWidth, defaultHeight);

            if (bitmap != null) {
                loaderResult.bitmap = bitmap;
                //获取成功
                mMainHandler.obtainMessage(MESSAGE_POST_RESULT, loaderResult).sendToTarget();
            } else {
                //获取失败
                mMainHandler.obtainMessage(MESSAGE_POST_RESULT, loaderResult).sendToTarget();
            }
        }
        public LoaderRunnable(String url, ImageView imageView, int ErrorBitmap,
                              int defaultWidth, int defaultHeight, ImageLoadListener imageLoadListener) {
            this.defaultWidth = defaultWidth;
            this.defaultHeight = defaultHeight;
            loaderResult = new LoaderResult(imageView, url, ErrorBitmap, imageLoadListener);
        }
    }

    
    /**
     * 读取结果类
     */
    public class LoaderResult {
        public final ImageView imageView;
        public final String url;
        public Bitmap bitmap;
        public final int ErrorBitmap;

        public final ImageLoadListener mImageLoadListener;

        public LoaderResult(ImageView imageView, String url, int ErrorBitmap, ImageLoadListener imageLoadListener) {
            this.imageView = imageView;
            this.url = url;
            this.bitmap = null;
            this.ErrorBitmap = ErrorBitmap;
            this.mImageLoadListener = imageLoadListener;
        }
    }
    
    /**
     * 从网络获取图片
     * @param urlString 图片的URL地址
     * @return 解析后的Bitmap对象
     */
    private Bitmap downloadFromNetwork(String urlString, int defaultWidth, int defaultHeight) 
            throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        //获取服务器返回回来的流   
        InputStream is = conn.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while((len = is.read(buffer))!=-1){
            bos.write(buffer, 0, len);
        }
        is.close();
        bos.flush();
        byte[] result = bos.toByteArray();

        return scaleBitmap(defaultWidth, defaultHeight, result);
    }

    /**
     * 获取特定宽度或者特定高度的Bitmap
     * @param defaultWidth 特定的宽度
     * @param defaultHeight 特定的高度
     * @param buffer
     * @return
     */
    private Bitmap scaleBitmap(int defaultWidth, int defaultHeight, byte[] buffer) {
        // 获取原图宽度 高度
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
        int realWidth = options.outWidth;
        int realHeight = options.outHeight;
        int scaledWidth = 0, scaledHeight = 0;
        
        if (defaultWidth != 0 && defaultHeight != 0) {
            /* 设置为指定的高度和宽度 */
            scaledWidth = defaultWidth;
            scaledHeight = defaultHeight;
        } else if (defaultWidth != 0 && defaultHeight == 0) {
            /* 根据指定宽度来设置 */
            scaledWidth = defaultWidth;
            scaledHeight = realHeight * (scaledWidth / realWidth);
        } else if (defaultHeight != 0 && defaultWidth == 0) {
            /* 根据制定高度来设置 */
            scaledHeight = defaultHeight;
            scaledWidth = realWidth * (scaledHeight / realHeight);
        } else if (defaultWidth == 0 && defaultHeight == 0){
            /* 按照图片实际大小来设置 */
            scaledWidth = realWidth;
            scaledHeight = realHeight;
        }
        
        options.inJustDecodeBounds = false;
        options.outWidth = scaledWidth;
        options.outHeight = scaledHeight;
        return BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
    }
    
    
    /**
     * 从本地读取图片
     * @param path
     * @return
     */
    private Bitmap downloadFromLocal(int defaultWidth, int defaultHeight, String path){
        return scaleBitmap(defaultWidth, defaultHeight, path);
    }

    /**
     * 获取特定宽度或者特定高度的Bitmap
     * @param defaultWidth 特定的宽度
     * @param defaultHeight 特定的高度
     * @param path
     * @return
     */
    private Bitmap scaleBitmap(int defaultWidth, int defaultHeight, String path) {
        // 获取原图宽度 高度
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int realWidth = options.outWidth;
        int realHeight = options.outHeight;
        int scaledWidth = 0, scaledHeight = 0;
        if (defaultWidth != 0 && defaultHeight != 0) {
            /* 设置为指定的高度和宽度 */
            scaledWidth = defaultWidth;
            scaledHeight = defaultHeight;
        } else if (defaultWidth != 0 && defaultHeight == 0) {
            /* 根据指定宽度来设置 */
            scaledWidth = defaultWidth;
            scaledHeight = realHeight * (scaledWidth / realWidth);
        } else if (defaultHeight != 0 && defaultWidth == 0) {
            /* 根据制定高度来设置 */
            scaledHeight = defaultHeight;
            scaledWidth = realWidth * (scaledHeight / realHeight);
        } else if (defaultWidth == 0 && defaultHeight == 0){
            /* 按照图片实际大小来设置 */
            scaledWidth = realWidth;
            scaledHeight = realHeight;
        }
        options.inJustDecodeBounds = false;
        options.outWidth = scaledWidth;
        options.outHeight = scaledHeight;
        return BitmapFactory.decodeFile(path, options);
    }
}
