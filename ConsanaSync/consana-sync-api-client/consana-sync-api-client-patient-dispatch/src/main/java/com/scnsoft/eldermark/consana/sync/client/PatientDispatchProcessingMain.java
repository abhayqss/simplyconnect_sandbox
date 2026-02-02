package com.scnsoft.eldermark.consana.sync.client;

import com.scnsoft.eldermark.consana.sync.client.config.PatientDispatchApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(PatientDispatchApplicationConfig.class)
public class PatientDispatchProcessingMain {

    public static void main(String[] args) {
        SpringApplication.run(PatientDispatchProcessingMain.class, args);
    }

}
