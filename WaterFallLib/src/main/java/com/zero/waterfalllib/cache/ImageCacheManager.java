package com.zero.waterfalllib.cache;


/**
 * @author linzewu
 * @date 16-8-29
 */
public class ImageCacheManager {
    
    private static ImageCacheManager sImageCacheManager;
    public synchronized static ImageCacheManager getInstance() {
        if (sImageCacheManager == null) {
            sImageCacheManager = new ImageCacheManager();
        }
        return sImageCacheManager;
    }
    
}
