package com.scnsoft.eldermark.monitoring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {

    @Value("${mail.server.host}")
    private String host;

    @Value("${mail.server.port}")
    private int port;

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        return mailSender;
    }
}
