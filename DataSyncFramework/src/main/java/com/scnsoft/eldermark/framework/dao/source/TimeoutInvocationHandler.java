package com.scnsoft.eldermark.framework.dao.source;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.*;


public class TimeoutInvocationHandler<T> implements InvocationHandler {
    final T underlying;
    final ExecutorService executor;

    public TimeoutInvocationHandler(T underlying, ExecutorService executor) {
        this.underlying = underlying;
        this.executor = executor;
    }

    public Object invoke(Object proxy, final Method method, final
                         Object[] args) throws Throwable {
        Future<Object> future = executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return method.invoke(underlying, args);
            }
        });

        return future.get(240, TimeUnit.SECONDS);
    }
}