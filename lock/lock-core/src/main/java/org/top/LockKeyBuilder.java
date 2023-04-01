package org.top;

import org.aopalliance.intercept.MethodInvocation;

/**
 * 锁 key 生成器
 */
public interface LockKeyBuilder {

    /**
     * 构建 key
     * @param methodInvocation
     * @param definitionKeys
     * @return
     */
    String buildKey(MethodInvocation methodInvocation, String [] definitionKeys);

}
