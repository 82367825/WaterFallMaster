package com.zero.waterfalllib;

import android.content.Context;

import com.zero.waterfalllib.cache.ImageLoader;
import com.zero.waterfalllib.cache.executor.WFThreadPoolProxy;

/**
 * @author linzewu
 * @date 16/8/29
 */
public class WaterFallManager {
    
    private static WaterFallManager sWaterFallManager;
    public synchronized static WaterFallManager getInstance() {
        if (sWaterFallManager == null) {
            sWaterFallManager = new WaterFallManager();
        }
        return sWaterFallManager;
    }
    private WaterFallManager() {
    }
    
    
    public void init(Context context) {
        WFThreadPoolProxy.getInstance();
        ImageLoader.ImageLoaderBuilder imageLoaderBuilder = new ImageLoader.ImageLoaderBuilder()
                .setDISK_CACHE_ENABLED(true)
                .setMEN_CACHE_ENABLED(true)
                .setDISK_CACHE_NAME("wf_image_loader")
                .setLOAD_WAY_NOW(ImageLoader.LOAD_WAY.LOAD_NETWORK);
        ImageLoader.getInstance().init(context.getApplicationContext(), imageLoaderBuilder);
    }
}
