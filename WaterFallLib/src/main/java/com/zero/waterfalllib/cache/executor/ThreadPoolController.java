package com.zero.waterfalllib.cache.executor;

import android.os.Handler;
import android.os.Looper;

/**
 * ThreadPool Controller
 * @author linzewu
 * @date 16-8-31
 */
public class ThreadPoolController {
    

    private static ThreadPoolController sInstance;
    
    public synchronized static ThreadPoolController getInstance() {
        return sInstance;
    }
    
    private ThreadPoolController() {
        mMainThreadHandler = new Handler(Looper.getMainLooper());
    }
    
    private PlusExecutor mPlusExecutor;    
    private Handler mMainThreadHandler;
    
}
