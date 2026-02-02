package com.scnsoft.eldermark.consana.sync.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ReceiveConsanaPatient {

    public static void main(String[] args) {
        SpringApplication.run(ReceiveConsanaPatient.class, args);
    }

}
