package com.scnsoft.eldermark;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

public class DataDuplicateTool {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Missing argument: number of copies");
            System.exit(1);
        }

        int numberOfCopies = 0;
        try {
            numberOfCopies = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid argument '" + args[0] + "': an integer number is expected");
            System.exit(1);
        }

        if (numberOfCopies <= 0) {
            System.err.println("Invalid argument '" + args[0] + "': number must be positive");
            System.exit(1);
        }

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        DataDuplicationService dataDuplicationService = applicationContext.getBean(DataDuplicationService.class);

        long startTime = System.currentTimeMillis();
        dataDuplicationService.duplicateData(numberOfCopies);
        long totalTime = System.currentTimeMillis() - startTime;

        System.out.println("Total time: " + totalTime + " ms");

    }
}
