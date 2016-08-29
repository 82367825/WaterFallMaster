package com.zero.waterfalllib.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;

import com.zero.waterfalllib.cache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * ImageCache
 * @author lin
 * @date 16-1-18
 */
public class ImageCache {
    
    private static final String TAG = "ImageCache";
    private static final boolean DEFAULT_MEN_CACHE_ENABLED = true;
    private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;
    // Default file name of the disk cache
    private static final String DEFAULT_DISK_CACHE_NAME = "images";      //图片缓存文件名，默认为"images"
    // Default memory cache size in kilobytes
    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 5;          // 5MB  内存缓存大小
    // Default disk cache size in bytes
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB  磁盘缓存大小

    /**
     * 参数
     */
    private ImageCacheParams mParams;
    /**
     * 缓存类
     */
    private final Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;

    /**
     * 构造方法
     * @param imageCacheParams
     */
    public ImageCache(Context context,ImageCacheParams imageCacheParams){
        this.mContext = context;
        init(imageCacheParams);
    }

    /**
     * 初始化
     * @param imageCacheParams
     */
    private void init(ImageCacheParams imageCacheParams){
        this.mParams = imageCacheParams;
        
        if(imageCacheParams.MEN_CACHE_ENABLED){
            initMemCache(imageCacheParams.MEM_CACHE_SIZE);
        }
        if(imageCacheParams.DISK_CACHE_ENABLED){
            initDiskCache(imageCacheParams.DISK_CACHE_SIZE,imageCacheParams.DISK_CACHE_NAME);
        }
    }

    /**
     * 初始化内存缓存
     * @param MEM_CACHE_SIZE
     */
    private void initMemCache(int MEM_CACHE_SIZE){
        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(MEM_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    /**
     * 初始化磁盘缓存
     * @param DISK_CACHE_SIZE
     * @param DISK_CACHE_NAME
     */
    private void initDiskCache(int DISK_CACHE_SIZE,String DISK_CACHE_NAME){
        // 初始化磁盘缓存类
        try {
            // 获取图片缓存路径
            File cacheDir = getDiskCacheDir(mContext, DISK_CACHE_NAME);
            if (!cacheDir.exists()) {
                if(!cacheDir.mkdirs()) return;
            }
            // 创建DiskLruCache实例，初始化缓存数据
            mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(mContext), 1, DISK_CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将一张图片存储到LruCache中
     *
     * @param key    LruCache的键，这里传入图片的URL地址。
     * @param bitmap LruCache的键，这里传入从网络上下载的Bitmap对象。
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    private Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }


    /**
     * 从LruCache中删除bitmap
     *
     * @param key
     */
    private void deleteBitmapFromMemoryCache(String key) {
        mMemoryCache.remove(key);
    }
    
    /**
     * 从DiskCache中添加一个bitmap
     * @param imageUrl
     * @param bitmap
     * @return
     */
    public boolean addBitmapToDiskCache(String imageUrl,Bitmap bitmap){
        // 生成图片URL对应的key
        String key = hashKeyForDisk(imageUrl);
        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskLruCache.edit(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (editor != null) {
            OutputStream outputStream = null;
            try {
                outputStream = editor.newOutputStream(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedOutputStream out = null;
            BufferedInputStream in = null;
            try {
                in = new BufferedInputStream(BitmapToInputStream(bitmap), 8 * 1024);
                out = new BufferedOutputStream(outputStream, 8 * 1024);

                int b;
                while ((b = in.read()) != -1) {
                    out.write(b);
                }
                editor.commit();
                return true;
            }catch (final IOException e) {
                Log.e(TAG, "add bitmap to DiskCache error");
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (final IOException ignored) {}
            }
            return false;
        }
        return false;
    }

    /**
     * 从DiskCache中获取bitmap
     * @param imageUrl
     * @return
     */
    private Bitmap getBitmapFromDiskCache(String imageUrl){
        if(mDiskLruCache == null) return null;
        
        Bitmap bitmap = null;
        //磁盘缓存中获取
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        DiskLruCache.Snapshot snapShot;
        // 生成图片URL对应的key
        String key = hashKeyForDisk(imageUrl);
        // 查找key对应的缓存
        try {
            snapShot = mDiskLruCache.get(key);
            if (snapShot == null)
                //如果磁盘缓存中没有，则返回null
                return null;
            else {
                fileInputStream = (FileInputStream) snapShot.getInputStream(0);
                fileDescriptor = fileInputStream.getFD();
                if (fileDescriptor != null) {
                    bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                }
                if (bitmap != null && mMemoryCache != null) {
                    // 将Bitmap对象添加到内存缓存当中
                    addBitmapToMemoryCache(imageUrl, bitmap);
                }
                return bitmap;
            }
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileDescriptor == null && fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

    /**
     * load bitmap
     * @param imageUrl
     * @return
     */
    public Bitmap loadBitmapFromCache(String imageUrl) {
        Bitmap bitmap = null;
        if(mMemoryCache != null){
            bitmap = getBitmapFromMemoryCache(imageUrl);
        }
        if(bitmap != null)
            return bitmap;
        if(mDiskLruCache != null){
            bitmap = getBitmapFromDiskCache(imageUrl);
        }
        return bitmap;
    }

    /**
     * 获取磁盘缓存的大小
     * @return
     */
    public long getDiskCacheSize(){
        if(mDiskLruCache == null) return 0;
        return mDiskLruCache.size();
    }

    /**
     * 将缓存记录同步到journal文件中。
     */
    public void flushCache() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * LruCache清空内存缓存
     *
     * @return void
     */
    public void clearMenCache() {
        if (mMemoryCache != null) {
            if (mMemoryCache.size() > 0) {
                Log.d(TAG, "mMemoryCache.size() " + mMemoryCache.size());
                mMemoryCache.evictAll();
                Log.d(TAG, "mMemoryCache.size()" + mMemoryCache.size());
            }
            if(this.mParams.MEN_CACHE_ENABLED)
                initMemCache(this.mParams.MEM_CACHE_SIZE);
        }
    }

    /**
     * DiskCache清空缓存
     */
    public void clearDiskCache(){
        if(mDiskLruCache == null) return ;
        try {
            mDiskLruCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(mDiskLruCache.isClosed() && this.mParams.DISK_CACHE_ENABLED){
            initDiskCache(this.mParams.DISK_CACHE_SIZE,this.mParams.DISK_CACHE_NAME);
        }
    }
    
    /**
     * ImageCache 相关参数
     */
    public static class ImageCacheParams{
        public  boolean MEN_CACHE_ENABLED = DEFAULT_MEN_CACHE_ENABLED;
        public  boolean DISK_CACHE_ENABLED = DEFAULT_DISK_CACHE_ENABLED;
        public  String DISK_CACHE_NAME = DEFAULT_DISK_CACHE_NAME;
        public  int MEM_CACHE_SIZE = DEFAULT_MEM_CACHE_SIZE;
        public  int DISK_CACHE_SIZE = DEFAULT_DISK_CACHE_SIZE;
    }

    /**
     * 根据传入的uniqueName获取硬盘缓存的路径地址。
     */
    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 获取当前应用程序的版本号。
     */
    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 使用MD5算法对传入的key进行加密并返回。
     */
    private String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }
    
    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * bitmap to InputStream
     * @param bm
     * @return InputStream
     */
    private InputStream BitmapToInputStream(Bitmap bm) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, arrayOutputStream);
        return new ByteArrayInputStream(arrayOutputStream.toByteArray());
    }
}
