package com.zero.waterfalllib.cache.executor.runnable;

/**
 * @author linzewu
 * @date 16-9-1
 */
public abstract class PriorityRunnable implements Runnable, Comparable<PriorityRunnable> {
    
    private int mPriority;
    
    public PriorityRunnable(int priority) {
        if (priority < 0) {
            throw new IllegalArgumentException();
        }
        this.mPriority = priority;
    }
    
    @Override
    public void run() {
        
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
}
