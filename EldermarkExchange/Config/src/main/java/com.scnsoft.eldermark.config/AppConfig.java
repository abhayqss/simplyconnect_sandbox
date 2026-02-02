package com.scnsoft.eldermark.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
//pass application.properties so that properties are available in environment.getProperty()
@PropertySource("classpath:application.properties")
public class AppConfig {

}
