package com.scnsoft.eldermark.therap;

import com.scnsoft.eldermark.therap.service.TherapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


    private final TherapService therapFacade;

    @Autowired
    public Main(TherapService therapFacade) {
        this.therapFacade = therapFacade;
    }

    @Override
    public void run(String... args) throws Exception {
        therapFacade.processAvailableFiles();
    }
}
