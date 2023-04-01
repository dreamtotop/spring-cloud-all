package org.top.aop;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.top.LockFailureStrategy;
import org.top.LockInfo;
import org.top.LockKeyBuilder;
import org.top.LockTemplate;
import org.top.annotation.Lock4j;
import org.top.configuration.Lock4jProperties;

@AllArgsConstructor
@NoArgsConstructor
public class LockInterceptor implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LockInterceptor.class);

    private LockTemplate lockTemplate;

    private LockKeyBuilder lockKeyBuilder;

    private LockFailureStrategy lockFailureStrategy;

    private Lock4jProperties lock4jProperties;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> cls = AopProxyUtils.ultimateTargetClass(invocation.getThis());
        if (!cls.equals(invocation.getThis().getClass())) {
            return invocation.proceed();
        }
        Lock4j lock4j = AnnotatedElementUtils.findMergedAnnotation(invocation.getMethod(), Lock4j.class);
        LockInfo lockInfo = null;
        try{
            String prefix = lock4jProperties.getLockKeyPrefix();
            prefix += StringUtils.hasText(lock4j.name()) ? lock4j.name() :
                    invocation.getMethod().getDeclaringClass().getName() + invocation.getMethod().getName();
            String key = prefix + "#" + lockKeyBuilder.buildKey(invocation, lock4j.keys());
            LockInfo lock = lockTemplate.lock(key, lock4j.expire(), lock4j.acquireTimeout());
            if(lock != null){
                return invocation.proceed();
            }
            //  lock failure
            lockFailureStrategy.lockFail(key, invocation.getMethod(), invocation.getArguments());
            return null;
        }
        finally {
            if (null != lockInfo && lock4j.autoRelease()) {
                final boolean releaseLock = lockTemplate.releaseLock(lockInfo);
                if (!releaseLock) {
                    logger.error("releaseLock fail,lockKey={},lockValue={}", lockInfo.getLockKey(),
                            lockInfo.getLockValue());
                }
            }
        }
    }
}
