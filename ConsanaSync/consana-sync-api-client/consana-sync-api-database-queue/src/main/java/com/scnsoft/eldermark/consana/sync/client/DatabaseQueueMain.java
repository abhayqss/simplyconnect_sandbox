package com.scnsoft.eldermark.consana.sync.client;

import com.scnsoft.eldermark.consana.sync.client.config.DatabaseQueueApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(DatabaseQueueApplicationConfig.class)
public class DatabaseQueueMain {

    public static void main(String[] args) {
        SpringApplication.run(DatabaseQueueMain.class, args);
    }
}
