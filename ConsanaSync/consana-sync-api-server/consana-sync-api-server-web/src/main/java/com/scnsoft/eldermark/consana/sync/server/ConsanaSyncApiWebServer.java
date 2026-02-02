package com.scnsoft.eldermark.consana.sync.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.scnsoft.eldermark.consana.sync.server")
public class ConsanaSyncApiWebServer {

    public static void main(String[] args) {
        SpringApplication.run(ConsanaSyncApiWebServer.class, args);
    }
}
