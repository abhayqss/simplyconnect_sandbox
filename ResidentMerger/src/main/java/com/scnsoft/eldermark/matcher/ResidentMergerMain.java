package com.scnsoft.eldermark.matcher;

import no.priv.garshol.duke.*;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class ResidentMergerMain {

    //todo get rid of old portal Core dependency

    private static final int DEFAULT_SCHEDULE = 3_600_000;    //one hour
    private static final boolean RUN_ONCE = false;    //false if run forever

    public static void main(String[] args) {
//        if (true) {
//            testDao();
//            return;
//        }
        final ResidentMergerConfig config = buildConfig(args);

        try {
            // Grab the Scheduler instance from the Factory
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // and start it off
            scheduler.start();

            JobKey jobKey = JobKey.jobKey("mergerJob", "group1");
            JobDetail job = newJob(ResidentMergerJob.class)
                    .withIdentity(jobKey)
                    .build();

            SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInMilliseconds(config.getScheduleInMillis());

            if (config.isRunOnce()) {
                schedule = schedule.withRepeatCount(1);
            } else {
                schedule = schedule.repeatForever();
            }

            Trigger trigger = newTrigger()
                    .withIdentity("mergerTrigger")
                    .startNow()
                    .withSchedule(schedule)
                    .build();

            scheduler.scheduleJob(job, trigger);

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    private static ResidentMergerConfig buildConfig(String[] args) {
        final ResidentMergerConfig config = new ResidentMergerConfig(DEFAULT_SCHEDULE, RUN_ONCE);
        overrideWithArgs(config, args);
        return config;
    }

    private static void overrideWithArgs(ResidentMergerConfig config, String[] args) {
//        config.setRunOnce(true);
        //todo implement
        //<dependency>
        //    <groupId>commons-cli</groupId>
        //    <artifactId>commons-cli</artifactId>
        //    <version>1.4</version>
        //</dependency>
    }

//    private static void testDao() {
//        MpiDao mpiDao = new MpiDao();
//
//        Record record1 = new RecordImpl(Map.of("id", Collections.singletonList("60494")));
//        Record record2 = new RecordImpl(Map.of("id", Collections.singletonList("60495")));
//
//        mpiDao.insertMergedRecords(record1, record2, false, 0.99);
//    }
}