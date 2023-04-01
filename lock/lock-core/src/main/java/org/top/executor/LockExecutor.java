package org.top.executor;

/**
 * 分布式锁核心处理器
 */
public interface LockExecutor<T> {


    /**
     * 续期，目前只有redisson支持，且expire参数为-1才会续期
     *
     * @return 是否续期
     */
    default boolean renewal(){
        return false;
    }


    /**
     * 加锁
     * @param lockKey
     * @param lockValue
     * @param expire
     * @param acquireTimeout
     * @return
     */
    T acquire(String lockKey, String lockValue, long expire, long acquireTimeout);


    /**
     * 释放锁
     * @param lockKey
     * @param lockValue
     * @param lockInstance
     * @return
     */
    boolean releaseLock(String lockKey, String lockValue, T lockInstance);

}
