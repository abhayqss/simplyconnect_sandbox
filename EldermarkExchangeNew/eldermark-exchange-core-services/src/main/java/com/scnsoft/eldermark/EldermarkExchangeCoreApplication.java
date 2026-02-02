package com.scnsoft.eldermark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableAsync
@EnableScheduling
public class EldermarkExchangeCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(EldermarkExchangeCoreApplication.class, args);
    }
}
