package com.scnsoft.eldermark.config;

import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.logging.Logger;

/**
 * @author averazub
 * @author phomal
 * Created on 12/27/2016.
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class WebMvcConfig {

    Logger logger = Logger.getLogger("WebMvcConfig");

    @Bean
    public HttpMessageConverters customConverters() {
        return new HttpMessageConverters(
                // string
                new StringHttpMessageConverter(),
                // html form
                new FormHttpMessageConverter(),
                // json
                new MappingJackson2HttpMessageConverter());
    }

}
