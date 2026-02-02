package com.scnsoft.eldermark.config;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class XslTransformationConfig {

    private static class XsltURIResolver implements URIResolver {

        private static final Logger logger = LoggerFactory.getLogger(XsltURIResolver.class);

        private final String dir;

        XsltURIResolver(String dir) {
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

    @Bean
    Templates lantanaCdaTransformationTemplate(@Value("classpath:${cda.transformation.lantana.xsl}") Resource lantanaXsl,
                                               @Value("${cda.transformation.lantana.base}") String baseDir) throws IOException, TransformerConfigurationException {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setURIResolver(new XsltURIResolver(baseDir));
        return transformerFactory.newTemplates(new StreamSource(lantanaXsl.getInputStream()));
    }

}
