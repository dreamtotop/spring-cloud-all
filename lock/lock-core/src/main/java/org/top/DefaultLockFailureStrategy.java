package org.top;

import org.top.exception.LockFailureException;

import java.lang.reflect.Method;

public class DefaultLockFailureStrategy implements LockFailureStrategy{


    protected static String DEFAULT_MESSAGE = "request failed,please retry it.";

    @Override
    public void lockFail(String key, Method method, Object[] arguments) throws LockFailureException {
        throw new LockFailureException(DEFAULT_MESSAGE);
    }
}
