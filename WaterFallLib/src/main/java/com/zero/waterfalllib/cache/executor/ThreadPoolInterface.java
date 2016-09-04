package com.zero.waterfalllib.cache.executor;

/**
 * @author linzewu
 * @date 16-9-2
 */
public interface ThreadPoolInterface {
    
    void execute(Runnable runnable);
    
    void execute(Runnable runnable, int priority);
    
    void cancel(Runnable runnable);
    
    void cancelAll();
    
    void pause();
    
    void restart();
}
