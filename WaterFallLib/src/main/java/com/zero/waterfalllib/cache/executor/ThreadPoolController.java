package com.zero.waterfalllib.cache.executor;

import java.util.Queue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

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

    }
    
    public void initThreadPool(ThreadPoolControllerBuilder builder) {
        if (builder.getIsPriority()) {
            mPlusExecutor = new PlusExecutor(builder.getCoreThreadCount(), 
                    builder.getMaxThreadCount(), ); 
        } else {
            mPlusExecutor = new PlusExecutor(builder.getCoreThreadCount(), 
                    builder.getMaxThreadCount(), );
        }
    }
    
    private PlusExecutor mPlusExecutor;
    
    private Queue<Runnable> mWaitQueue = new Queue<Runnable>();
    private static final ThreadFactory mThreadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r);
        }
    };
    private static final RejectedExecutionHandler mRejectedExecutionHandler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            
        }
    };
}
