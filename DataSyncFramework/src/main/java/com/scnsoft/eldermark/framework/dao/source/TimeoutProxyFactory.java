package com.scnsoft.eldermark.framework.dao.source;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;

/**
 * Created by aduzhynskaya on 10-Mar-16.
 */
public class TimeoutProxyFactory {

    public static<T> T getProxy(Class<T> intf,
                                final T obj,
                                ExecutorService executor) {
        return (T)
                Proxy.newProxyInstance(obj.getClass().getClassLoader(),
                        new Class[] { intf },
                        new TimeoutInvocationHandler(obj, executor));
    }
}
