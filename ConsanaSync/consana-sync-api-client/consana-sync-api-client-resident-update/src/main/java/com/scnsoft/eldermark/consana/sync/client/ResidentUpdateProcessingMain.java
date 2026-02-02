package com.scnsoft.eldermark.consana.sync.client;

import com.scnsoft.eldermark.consana.sync.client.config.ResidentUpdateApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ResidentUpdateApplicationConfig.class)
public class ResidentUpdateProcessingMain {

    public static void main(String[] args) {
        SpringApplication.run(ResidentUpdateProcessingMain.class, args);
    }

}
