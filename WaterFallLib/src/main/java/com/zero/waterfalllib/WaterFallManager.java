package com.zero.waterfalllib;

import android.content.Context;
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
        //private
    }
    
    
    public void init(Context context) {
        WFThreadPoolProxy.getInstance();
//        ImageLoader.ImageLoaderBuilder imageLoaderBuilder = new ImageLoader.ImageLoaderBuilder()
//                .setDISK_CACHE_ENABLED()
//                .setDISK_CACHE_NAME()
//                .setDISK_CACHE_SIZE()
//                .setMEM_CACHE_SIZE()
//                .setMEN_CACHE_ENABLED()
//                .setLOAD_WAY_NOW();
//        ImageLoader.getInstance().init(context.getApplicationContext(), imageLoaderBuilder);
    }
}
