package org.top.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.top.executor.LockExecutor;

@Getter
@Setter
@ConfigurationProperties(prefix = "lock4j")
public class Lock4jProperties {

    /**
     * 过期时间，单位毫秒
     */
    private Long expire = 30000L;

    /**
     * 获取锁超时时间，单位 毫秒
     */
    private Long acquireTimeout = 3000L;

    /**
     * 获取锁失败时重试时间间隔 单位：毫秒
     */
    private Long retryInterval = 100L;


    /**
     * 默认执行器，不设置默认取容器第一个(默认注入顺序，redisson>redisTemplate>zookeeper)
     */
    private Class<? extends LockExecutor> primaryExecutor;

    /**
     * 锁key前缀
     */
    private String lockKeyPrefix = "lock4j:";




}
