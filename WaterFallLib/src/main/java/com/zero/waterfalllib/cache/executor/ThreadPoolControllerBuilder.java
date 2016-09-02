package com.zero.waterfalllib.cache.executor;

/**
 * @author linzewu
 * @date 16-9-2
 */
public class ThreadPoolControllerBuilder {

    //线程池默认参数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int DEFAULT_CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int DEFAULT_MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final boolean DEFAULT_IS_PRIORITY = false;
    
    private int mCoreThreadCount;
    private int mMaxThreadCount;
    private boolean mIsPriorityThreadPool;
    
    public ThreadPoolControllerBuilder() {
        this.mCoreThreadCount = DEFAULT_CORE_POOL_SIZE;
        this.mMaxThreadCount = DEFAULT_MAXIMUM_POOL_SIZE;
        this.mIsPriorityThreadPool = DEFAULT_IS_PRIORITY;
    }
    
    public ThreadPoolControllerBuilder buildCoreThreadCount(int coreThreadCount) {
        this.mCoreThreadCount = coreThreadCount;
        return this;
    }
    
    public ThreadPoolControllerBuilder buildMaxThreadCount(int maxThreadCount) {
        this.mMaxThreadCount = maxThreadCount;
        return this;
    }
    
    public ThreadPoolControllerBuilder buildIsPriority(boolean isPriorityThreadPool) {
        this.mIsPriorityThreadPool = isPriorityThreadPool;
        return this;
    }
    
    public int getCoreThreadCount() {
        return mCoreThreadCount;
    }
    
    public int getMaxThreadCount() {
        return mMaxThreadCount;
    }
    
    public boolean getIsPriority() {
        return mIsPriorityThreadPool;
    }
}
