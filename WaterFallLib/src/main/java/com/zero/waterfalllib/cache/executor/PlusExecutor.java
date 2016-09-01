package com.zero.waterfalllib.cache.executor;

import com.zero.waterfalllib.util.LogUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author linzewu
 * @date 16-8-31
 */
public class PlusExecutor extends ThreadPoolExecutor implements PlusExecutorInterface {
    
    private static final String TAG = "PlusExecutor";
    
    private boolean mIsPause = false;

    private ReentrantLock mPauseLock = new ReentrantLock();
    private Condition unPauseCondition = mPauseLock.newCondition();
    
    public PlusExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public PlusExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public PlusExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public PlusExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void beforeExecute(Thread thread, Runnable r) {
        super.beforeExecute(thread, r);
        LogUtils.d(TAG, thread.getName() + "ready to execute.");
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        LogUtils.d(TAG, Thread.currentThread().getName() + "execute complete.");
    }

    @Override
    protected void terminated() {
        super.terminated();
        LogUtils.d(TAG, "Thread Pool finish.");
    }

    @Override
    public void initExecutor() {
        
    }

    @Override
    public void destroyExecutor() {

    }

    @Override
    public void stopExecutor() {

    }

    @Override
    public void restartExecutor() {

    }

    @Override
    public void execute(Runnable runnable, int priority) {

    }

    @Override
    public void execute(Runnable runnable, int priority, String runnableName) {

    }

    @Override
    public void cancel(Runnable runnable) {

    }

    @Override
    public void stop(Runnable runnable) {

    }

    @Override
    public void restart(Runnable runnable) {

    }
}
