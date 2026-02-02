package com.scnsoft.eldermark.web.listeners;

import com.scnsoft.eldermark.services.cda.util.CustomCDAUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * was created to load cda jar library files into memory
 * Created by ggavrysh on 6/11/2018.
 * @see com.scnsoft.eldermark.services.cda.util.CustomCDAUtil#loadPackages(resourcejarAbsolutePaths);
 */
//@WebListener does not work with Servlet 2.5 on demo on Tomcat 6
public class LoadCDALibsServletContextListener implements ServletContextListener {

    //TODO remove temp logging
    private static final Logger LOGGER = Logger.getLogger(LoadCDALibsServletContextListener.class.getName());
    private static final String logPrefix = "CCN-1411-temp ";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        final ServletContext servletContext = servletContextEvent.getServletContext();
        final String realRootPath = servletContext.getRealPath("/");
        LOGGER.info(String.format("%s realRootPath=[%s]", logPrefix, realRootPath));
        final Set<String> resourceJarRelativePaths = servletContext.getResourcePaths("/WEB-INF/lib");
        LOGGER.info(String.format("%s resourceJarRelativePaths=[%s]", logPrefix, resourceJarRelativePaths.toString()));
        final Set<String> resourcejarAbsolutePaths = new LinkedHashSet<>(resourceJarRelativePaths.size());

        for (String relativePath : resourceJarRelativePaths) {
            resourcejarAbsolutePaths.add(Paths.get(realRootPath, relativePath).toString());
        }

        LOGGER.info(String.format("%s resourcejarAbsolutePaths=[%s]", logPrefix, resourcejarAbsolutePaths.toString()));

        CustomCDAUtil.loadPackages(resourcejarAbsolutePaths);

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
