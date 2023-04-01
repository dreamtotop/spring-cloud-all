package org.top.lock.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.top.configuration.Lock4jProperties;
import org.top.lock.executor.RedisTemplateLockExecutor;

@Configuration
@ConditionalOnClass(RedisOperations.class)
public class RedisTemplateLockAutoConfiguration {

    @Bean
    @Order(200)
    public RedisTemplateLockExecutor redisTemplateLockExecutor(StringRedisTemplate stringRedisTemplate, Lock4jProperties lock4jProperties) {
        return new RedisTemplateLockExecutor(stringRedisTemplate,lock4jProperties);
    }
}
