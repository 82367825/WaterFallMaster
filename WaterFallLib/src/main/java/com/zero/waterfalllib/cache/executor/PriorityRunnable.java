package com.zero.waterfalllib.cache.executor;

/**
 * @author linzewu
 * @date 16-9-1
 */
public class PriorityRunnable implements Runnable, Comparable<PriorityRunnable> {
    
    private int mPriority;
    private Runnable mRunnable;
    
    public PriorityRunnable(int priority) {
        if (priority < 0) {
            throw new IllegalArgumentException();
        }
        this.mPriority = priority;
    }
    
    public PriorityRunnable(Runnable runnable, int priority) {
        if (priority < 0) {
            throw new IllegalArgumentException();
        }
        this.mPriority = priority;
        this.mRunnable = runnable;
    }
    
    @Override
    public void run() {
        if (mRunnable != null) {
            mRunnable.run();
        }
    }

    public int getPriority() {
        return mPriority;
    }

    @Override
    public int compareTo(PriorityRunnable priorityRunnable) {
        if (priorityRunnable.getPriority() == mPriority)
            return 0;
        return priorityRunnable.getPriority() > mPriority ? 1 : -1;
    }
    
    public static Runnable parsePriorityRunnable(Runnable runnable, int priority) {
        if (runnable instanceof PriorityRunnable) return runnable;
        return new PriorityRunnable(runnable, priority);
    }
}
