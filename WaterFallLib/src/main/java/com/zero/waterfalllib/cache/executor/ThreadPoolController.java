package com.zero.waterfalllib.cache.executor;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPool Controller
 * @author linzewu
 * @date 16-8-31
 */
public class ThreadPoolController implements ThreadPoolInterface {
    /* 可以有多个线程池实例 */
    private static HashMap<String, ThreadPoolController> sInstanceMaps = 
            new HashMap<String, ThreadPoolController>();
    
    public synchronized static ThreadPoolController getInstance(String threadPoolName) {
        ThreadPoolController threadPoolController = sInstanceMaps.get(threadPoolName);
        if (threadPoolController == null) {
            threadPoolController = new ThreadPoolController();
            threadPoolController.mExecutorName = threadPoolName;
            sInstanceMaps.put(threadPoolName, threadPoolController);
        }
        return threadPoolController;
    }
    
    private ThreadPoolController() {
        initScheduledExecutorService();
    }

    /**
     * 初始化线程池
     * @param builder
     */
    public void initThreadPool(ThreadPoolControllerBuilder builder) {
        if (builder.getIsPriority()) {
            mWorkQueue = new PriorityBlockingQueue<Runnable>(WORK_QUEUE_SIZE);
        } else {
            mWorkQueue = new LinkedBlockingDeque<Runnable>(WORK_QUEUE_SIZE);
        }
        mPlusExecutor = new PlusExecutor(
                builder.getCoreThreadCount(), builder.getMaxThreadCount(), 
                KEEP_ALIVE_TIME, TimeUnit.SECONDS, mWorkQueue,
                mThreadFactory, mRejectedExecutionHandler);
    }

    /**
     * 销毁线程池
     */
    public void destroyThreadPool() {
        cancelAll();
        if (mPlusExecutor != null) {
            sInstanceMaps.remove(mExecutorName);
            mPlusExecutor = null;
        }
    }

    private final static int KEEP_ALIVE_TIME = 60;
    private final static int WORK_QUEUE_SIZE = 20;
    private Object mLockObject = new Object();
    private String mExecutorName;
    private PlusExecutor mPlusExecutor;
    private BlockingQueue<Runnable> mWorkQueue;
    private Queue<Runnable> mWaitRunnableQueue;

    /**
     * 线程池生成线程调用
     */
    private final ThreadFactory mThreadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            if (r instanceof PriorityRunnable) {
                Thread thread = new Thread(r, "Thread #" + System.currentTimeMillis());
                thread.setPriority(((PriorityRunnable) r).getPriority());
                /* 这里补充设置线程级优先级,不一定有效 */
                return thread;
            }
            return new Thread(r, "Thread #" + System.currentTimeMillis());
        }
    };
    
    /**
     * 当我们创建线程池并且提交任务失败时，
     * 线程池会回调RejectedExecutionHandler接口的rejectedExecution(Runnable task, ThreadPoolExecutor executor)方法来处理线程池处理失败的任务，
     * 其中task 是用户提交的任务，而executor是当前执行的任务的线程池。
     */
    private final RejectedExecutionHandler mRejectedExecutionHandler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
            synchronized (mLockObject) {
                /* 把线程存进等待队列 */
                mWaitRunnableQueue.offer(task);
            }
        }
    };
    
    private void scheduledRejectedRunnable() {
        synchronized (mLockObject) {
            if (mWaitRunnableQueue.isEmpty()) return ;
            mWaitRunnableQueue.poll();
        }
    }
    
    

    /**
     * 定时调度服务<线程池>
     * 定时重新调用被拒绝提交的任务
     */
    private static ScheduledExecutorService sScheduledExecutorService = null;
    private static ScheduledRejectedRunnable sScheduledRejectedRunnable;
    private static final long SCHEDULED_INIT_DELAY_TIME = 0;
    private static final long SCHEDULED_PERIOD_TIME = 2000;
    
    private void initScheduledExecutorService() {
        if (sScheduledExecutorService != null) return ;
        sScheduledRejectedRunnable = new ScheduledRejectedRunnable();
        sScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        sScheduledExecutorService.scheduleAtFixedRate(sScheduledRejectedRunnable, 
                SCHEDULED_INIT_DELAY_TIME, SCHEDULED_PERIOD_TIME, TimeUnit.MILLISECONDS);
    }
    
    public static class ScheduledRejectedRunnable implements Runnable {

        @Override
        public void run() {
            /* 设置任务调度线程为后台运行,减少在运行占用的CPU比例 */
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        }
    }

    /**
     * 执行任务
     * @param runnable 线程的执行体
     */
    @Override
    public void execute(Runnable runnable) {
        synchronized (mLockObject) {
            mPlusExecutor.execute(
                    PriorityRunnable.parsePriorityRunnable(runnable, Thread.NORM_PRIORITY)
            );
        }
    }

    /**
     * 执行任务
     * @param runnable 线程的执行体
     * @param priority 线程的优先级 决定了线程从准备队列到执行的先后顺序,不会影响线程执行占用资源的优先级
     *                 如果要设置占用资源的优先级,可调用Process.setThreadPriority
     */
    @Override
    public void execute(Runnable runnable, int priority) {
        synchronized (mLockObject) {
            mPlusExecutor.execute(
                    PriorityRunnable.parsePriorityRunnable(runnable, priority)
            );
        }
    }

    @Override
    public void cancel(Runnable runnable) {
        synchronized (mLockObject) {
            mPlusExecutor.remove(runnable);
            if (mWaitRunnableQueue.contains(runnable)) {
                mWaitRunnableQueue.remove(runnable);
            }
        }
    }

    @Override
    public void cancelAll() {
        synchronized (mLockObject) {
            if (!mPlusExecutor.isShutdown()) {
                mPlusExecutor.shutdownNow();
            }
            if (mWaitRunnableQueue != null && !mWaitRunnableQueue.isEmpty()) {
                mWaitRunnableQueue.clear();
            }
        }
    }
    
    @Override
    public void pause() {
        synchronized (mLockObject) {
            mPlusExecutor.pause();
        }
    }

    @Override
    public void restart() {
        synchronized (mLockObject) {
            mPlusExecutor.restart();
        }
    }

}
