package com.zero.waterfalllib.cache.executor;

/**
 * @author linzewu
 * @date 16-8-31
 */
public interface PlusExecutorInterface {
    
    void initExecutor();
    
    void destroyExecutor();
    
    void stopExecutor();
    
    void restartExecutor();
    
    void execute(Runnable runnable);
    
    void execute(Runnable runnable, int priority);
    
    void execute(Runnable runnable, int priority, String runnableName);
    
    void cancel(Runnable runnable);
    
    void stop(Runnable runnable);
    
    void restart(Runnable runnable);
}
