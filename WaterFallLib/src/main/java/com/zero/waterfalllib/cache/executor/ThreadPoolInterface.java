package com.zero.waterfalllib.cache.executor;

/**
 * @author linzewu
 * @date 16-9-2
 */
public interface ThreadPoolInterface {
    
    void init();
    
    void execute();
    
    void execute(int p);
    
}
