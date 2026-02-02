package com.scnsoft.eldermark.mobile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.scnsoft.eldermark")
public class MobileApiMain {

    public static void main(String[] args) {
        SpringApplication.run(MobileApiMain.class, args);
    }
}
