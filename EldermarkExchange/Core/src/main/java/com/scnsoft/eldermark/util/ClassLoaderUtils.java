package com.scnsoft.eldermark.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public final class ClassLoaderUtils {

    private ClassLoaderUtils() {
    }

    public static void addPathToSystemClassloader(String path) throws MalformedURLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final File pathAsFile = new File(path);
        final URL urlPath = pathAsFile.toURI().toURL();
        invokeAddUrl((URLClassLoader) ClassLoader.getSystemClassLoader(), urlPath);
    }

    private static void invokeAddUrl(URLClassLoader classLoader, URL argument) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(classLoader, argument);
        method.setAccessible(false);
    }
}
