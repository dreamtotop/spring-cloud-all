package org.top.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.top.*;
import org.top.annotation.Lock4j;
import org.top.aop.LockAnnotationAdvisor;
import org.top.aop.LockInterceptor;
import org.top.executor.LockExecutor;

import java.util.List;

/**
 * 分布式锁配置中心
 */
@Configuration
@EnableConfigurationProperties(Lock4jProperties.class)
@RequiredArgsConstructor
public class LockAutoConfiguration {
    private final Lock4jProperties lock4jProperties;

    @Bean
    @ConditionalOnMissingBean
    public LockTemplate lockTemplate(List<LockExecutor> executors) {
        LockTemplate lockTemplate = new LockTemplate();
        lockTemplate.setLock4jProperties(lock4jProperties);
        lockTemplate.setExecutors(executors);
        return lockTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public LockKeyBuilder lockKeyBuilder(BeanFactory beanFactory) {
        return new DefaultLockKeyBuilder(beanFactory);
    }


    @Bean
    @ConditionalOnMissingBean
    public LockFailureStrategy lockFailureStrategy() {
        return new DefaultLockFailureStrategy();
    }


    @Bean
    @ConditionalOnMissingBean
    public LockInterceptor lockInterceptor(@Lazy LockTemplate lockTemplate, LockKeyBuilder lockKeyBuilder,
                                           LockFailureStrategy lockFailureStrategy){
        return new LockInterceptor(lockTemplate, lockKeyBuilder, lockFailureStrategy, lock4jProperties);
    }


    @Bean
    @ConditionalOnMissingBean
    public LockAnnotationAdvisor lockAnnotationAdvisor(LockInterceptor lockInterceptor){
        return new LockAnnotationAdvisor(lockInterceptor, Ordered.HIGHEST_PRECEDENCE);
    }

}
