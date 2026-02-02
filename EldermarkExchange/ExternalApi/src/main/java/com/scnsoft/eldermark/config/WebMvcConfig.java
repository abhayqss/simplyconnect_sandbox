package com.scnsoft.eldermark.config;

import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.logging.Logger;

/**
 * @author phomal
 * Created on 1/11/2018.
 */
@Configuration
public class WebMvcConfig {

    Logger logger = Logger.getLogger("WebMvcConfig");

    @Bean
    public HttpMessageConverters customConverters() {
        return new HttpMessageConverters(
                // string
                new StringHttpMessageConverter(),
                // json
                new MappingJackson2HttpMessageConverter());
    }

}
