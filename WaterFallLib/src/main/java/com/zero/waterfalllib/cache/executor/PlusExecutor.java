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
 * 功能扩充的线程池
 * @author linzewu
 * @date 16-8-31
 */
public class PlusExecutor extends ThreadPoolExecutor {
    
    private static final String TAG = "PlusExecutor";
    
    private boolean mIsPause = false;

    private ReentrantLock mPauseLock = new ReentrantLock();
    private Condition mUnPauseCondition = mPauseLock.newCondition();
    
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

    /**
     * 在子线程thread中调用，在run()方法体调用之前调用
     * @param thread
     * @param r
     */
    @Override
    protected void beforeExecute(Thread thread, Runnable r) {
        super.beforeExecute(thread, r);
        mPauseLock.lock();
        try {
            /* 如果线程池被暂停，让所有的准备运行的线程休眠 */
            if (mIsPause) mUnPauseCondition.await();
//            while (mIsPause) mUnPauseCondition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mPauseLock.unlock(); 
        }
        LogUtils.d(TAG, thread.getName() + "ready to execute.");
    }

    /**
     * 在子线程thread中调用，在run()方法体调用之后调用
     * @param r
     * @param t
     */
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

    /**
     * 暂停线程池
     * 备注：该方法只暂停还没开始运行的线程
     */
    public void pause() {
        mPauseLock.lock();
        try {
            mIsPause = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mPauseLock.unlock();
        }
    }

    /**
     * 重启线程池
     * 备注：恢复线程池中已经被休眠的线程
     */
    public void restart() {
        mPauseLock.lock();
        try {
            mIsPause = false;
            mUnPauseCondition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mPauseLock.unlock();
        }
    }
}
