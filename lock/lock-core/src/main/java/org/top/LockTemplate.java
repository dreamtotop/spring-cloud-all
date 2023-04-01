package org.top;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.top.aop.LockInterceptor;
import org.top.configuration.Lock4jProperties;
import org.top.exception.LockException;
import org.top.executor.LockExecutor;
import org.top.util.LockUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * lock 模板方法
 */
public class LockTemplate implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(LockTemplate.class);

    private static final Map<Class<? extends LockExecutor>, LockExecutor>
            executorMap = new LinkedHashMap<>();

    @Setter
    private Lock4jProperties lock4jProperties;

    @Setter
    private List<LockExecutor> executors;

    private LockExecutor primaryExecutor;

    public LockTemplate(){

    }

    public LockInfo lock(String key) throws LockException {
        return lock(key, 0, -1);
    }

    public LockInfo lock(String key, long expire, long acquireTimeout) throws LockException {
        return lock(key, expire, acquireTimeout, null);
    }


    /**
     *  加锁方法
     * @param key
     * @param expire
     * @param acquireTimeout
     * @param executor
     * @return
     * @throws LockException
     */
    public LockInfo lock(String key, long expire, long acquireTimeout, Class<? extends LockExecutor> executor) throws LockException {

        acquireTimeout = acquireTimeout < 0 ? lock4jProperties.getAcquireTimeout() : acquireTimeout;
        long retryInterval = lock4jProperties.getRetryInterval();
        LockExecutor lockExecutor = obtainExecutor(executor);
        logger.info(String.format("use lock class: %s", lockExecutor.getClass()));
        expire = !lockExecutor.renewal() && expire <= 0 ? lock4jProperties.getExpire() : expire;
        int acquireCount = 0;
        String value = LockUtil.simpleUUID();
        long start  = System.currentTimeMillis();
        try {
            do {
                acquireCount++;
                Object lockInstance = lockExecutor.acquire(key, value, expire, acquireTimeout);
                if (lockInstance != null) {
                    return new LockInfo(key, value, expire, acquireTimeout, acquireCount, lockInstance, lockExecutor);
                }
                TimeUnit.MILLISECONDS.sleep(retryInterval);
            } while (System.currentTimeMillis() - start < acquireTimeout);
        } catch (InterruptedException ex){
            logger.error("lock error", ex);
            throw new LockException();
        }
        return null;
    }


    /**
     * 释放 锁 的方法
     * @param lockInfo
     * @return
     */
    public boolean releaseLock(LockInfo lockInfo){

        if (null == lockInfo) {
            return false;
        }
        return lockInfo.getExecutor().releaseLock(lockInfo.getLockKey(), lockInfo.getLockValue(),
                lockInfo.getLockInstance());
    }

    protected LockExecutor obtainExecutor(Class<? extends LockExecutor> clazz) {
        if (null == clazz || clazz == LockExecutor.class) {
            return primaryExecutor;
        }
        final LockExecutor lockExecutor = executorMap.get(clazz);
        Assert.notNull(lockExecutor, String.format("can not get bean type of %s", clazz));
        return lockExecutor;
    }



    @Override
    public void afterPropertiesSet() throws Exception {

        Assert.isTrue(lock4jProperties.getAcquireTimeout() >= 0, "tryTimeout must least 0");
        Assert.isTrue(lock4jProperties.getExpire() >= -1, "expireTime must lease -1");
        Assert.isTrue(lock4jProperties.getRetryInterval() >= 0, "retryInterval must more than 0");
        Assert.hasText(lock4jProperties.getLockKeyPrefix(), "lock key prefix must be not blank");
        Assert.notEmpty(executors, "executors must have at least one");

        for (LockExecutor executor : executors) {
            executorMap.put(executor.getClass(), executor);
        }

        final Class<? extends LockExecutor> primaryExecutor = lock4jProperties.getPrimaryExecutor();
        if (null == primaryExecutor) {
            this.primaryExecutor = executors.get(0);
        } else {
            this.primaryExecutor = executorMap.get(primaryExecutor);
            Assert.notNull(this.primaryExecutor, "primaryExecutor must be not null");
        }
    }
}
