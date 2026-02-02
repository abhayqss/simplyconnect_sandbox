package com.scnsoft.eldermark.util;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

public final class ClasspathURIResolverFactory {

    private ClasspathURIResolverFactory() {
    }

    public static URIResolver inClassPathDirectory(String baseDir) {
        return new ClasspathURIResolver(baseDir);
    }

    private static class ClasspathURIResolver implements URIResolver {
        private static final Logger logger = LoggerFactory.getLogger(ClasspathURIResolver.class);
        private final String dir;

        ClasspathURIResolver(String dir) {
            this.dir = dir;
        }

        @Override
        public Source resolve(String href, String base) {
            try {
                InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(dir + href);
                return new StreamSource(inputStream);
            } catch (Exception ex) {
                logger.error(ExceptionUtils.getStackTrace(ex));
                throw ex;
            }
        }
    }
}
