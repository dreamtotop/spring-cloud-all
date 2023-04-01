package org.top.executor;


public abstract class AbstractLockExecutor<T> implements LockExecutor<T>{

    protected T obtainLockInstance(boolean locked, T lockInstance) {
        return locked ? lockInstance : null;
    }
}
