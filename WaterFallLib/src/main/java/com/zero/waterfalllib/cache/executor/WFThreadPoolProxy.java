package com.zero.waterfalllib.cache.executor;

/**
 * WaterFall线程池代理类
 * @author linzewu
 * @date 16-9-2
 */
public class WFThreadPoolProxy {
    
    private static final String WF_THREAD_NAME = "wf_thread_name";

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int WF_THREAD_POOL_CORE_NUM = CPU_COUNT + 1;
    private static final int WF_THREAD_POOL_MAX_NUM = CPU_COUNT * 2 + 1;
    private static final boolean WF_THREAD_POOL_IS_PRIORITY = false;
    
    private static WFThreadPoolProxy sWfThreadPoolProxy;
    private ThreadPoolController mThreadPoolController;
    
    private WFThreadPoolProxy() {
        mThreadPoolController = ThreadPoolController.getInstance(WF_THREAD_NAME);
        mThreadPoolController.initThreadPool(
                new ThreadPoolControllerBuilder().
                        buildCoreThreadCount(WF_THREAD_POOL_CORE_NUM).
                        buildMaxThreadCount(WF_THREAD_POOL_MAX_NUM).
                        buildIsPriority(WF_THREAD_POOL_IS_PRIORITY)
        );
    }
    
    public synchronized static WFThreadPoolProxy getInstance() {
        if (sWfThreadPoolProxy == null) {
            sWfThreadPoolProxy = new WFThreadPoolProxy();
        }
        return sWfThreadPoolProxy;
    }
    
    public void execute(Runnable runnable) {
        mThreadPoolController.execute(runnable);
    }
    
    public void execute(Runnable runnable, int priority) {
        mThreadPoolController.execute(runnable, priority);
    }
    
    public void cancel(Runnable runnable) {
        mThreadPoolController.cancel(runnable);
    }
    
    public void cancelAll() {
        mThreadPoolController.cancelAll();
    }
    
    public void pause() {
        mThreadPoolController.pause();
    }
    
    public void restart() {
        mThreadPoolController.restart();
    }
    
}
