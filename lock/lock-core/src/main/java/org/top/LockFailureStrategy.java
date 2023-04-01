package org.top;

import org.top.exception.LockFailureException;

import java.lang.reflect.Method;

public interface LockFailureStrategy {

    /**
     * 锁失败事件
     * @param key
     * @param method
     * @param arguments
     */
    void lockFail(String key, Method method, Object [] arguments) throws LockFailureException;
}
