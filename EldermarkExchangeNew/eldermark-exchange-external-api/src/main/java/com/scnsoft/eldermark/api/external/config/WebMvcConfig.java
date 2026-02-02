package com.scnsoft.eldermark.api.external.config;

import org.dozer.DozerBeanMapper;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * @author phomal
 * Created on 1/11/2018.
 */
@Configuration
public class WebMvcConfig {

    @Bean
    public HttpMessageConverters customConverters() {
        return new HttpMessageConverters(
                // string
                new StringHttpMessageConverter(),
                // json
                new MappingJackson2HttpMessageConverter());
    }

    @Bean
    public DozerBeanMapper dozerBeanMapper() {
        var mappingFiles = Collections.singletonList(
                "dozer/dozer-external-api.xml"
        );

        DozerBeanMapper dozerBean = new DozerBeanMapper();
        dozerBean.setMappingFiles(mappingFiles);
        return dozerBean;
    }
}
