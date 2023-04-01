package org.top;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.top.executor.LockExecutor;

/**
 * 锁信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LockInfo {

    /**
     * 锁的key
     */
    private String lockKey;

    /**
     * 锁 value
     */
    private String lockValue;

    /**
     * 锁过期时间
     */
    private Long expire;

    /**
     * 获取锁超时时间
     */
    private Long acquireTimeOut;

    /**
     * 获取锁的次数
     */
    private Integer acquireCount;

    /**
     * 锁实例额
     */
    private Object lockInstance;

    /**
     * 锁执行器
     */
    private LockExecutor executor;

}
