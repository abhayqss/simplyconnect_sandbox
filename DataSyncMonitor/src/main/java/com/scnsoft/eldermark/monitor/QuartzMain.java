package com.scnsoft.eldermark.monitor;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;


public class QuartzMain {

    private static final String INTERVAL_PROPERTY = "interval";
    private static final String EMAIL_PROPERTY = "email";
    private static final String DB_USERNAME_PROPERTY = "dbUsername";
    private static final String DB_PASSWORD_PROPERTY = "dbPassword";
    private static final String DB_URL_PROPERTY = "dbUrl";
    private static final String MAIL_HOST_PROPERTY = "mailHost";
    private static final String MAIL_HOST_IP_PROPERTY = "mailHostIp";
    private static final String MAIL_LOGIN_PROPERTY = "mailLogin";
    private static final String MAIL_PASSWORD_PROPERTY = "mailPassword";
    private static final String MAIL_PORT_PROPERTY = "mailPort";
    private static final String DEBUG_PROPERTY = "mailDebug";
    private static final String PLATFORM_PROPERTY = "platform";
    private static final String PATH_TO_LOG_FILE = "pathToLogFile";
    private static final String NOT_MONITORED = "listOfIgnoredDatabaseNames";
    private static final String ATTACH_LOGS_INTERVAL = "attachLogsInterval";

    private static final String CONFIG_FILE = "config.properties";

    private static Logger logger = LoggerFactory.getLogger(QuartzMain.class);

    public static void main(String[] args) {

        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            JobDataMap jobDataMap = loadConfiguration();

            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            JobDetail job = newJob(DataSyncMonitorJob.class)
                    .withIdentity("monitor", "group1")
                    .usingJobData(jobDataMap)
                    .build();

            Calendar tomorrowMidnight = Calendar.getInstance();
            tomorrowMidnight.setTime(new Date());
            tomorrowMidnight.add(Calendar.DATE, 1);
            tomorrowMidnight.set(Calendar.HOUR, 0);
            tomorrowMidnight.set(Calendar.MINUTE, 0);
            tomorrowMidnight.set(Calendar.AM_PM, Calendar.AM);


            Trigger trigger = newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startAt(tomorrowMidnight.getTime())
                    .withSchedule(simpleSchedule()
                            .withIntervalInMinutes(jobDataMap.getInt( INTERVAL_PROPERTY))
                            .repeatForever())
                    .build();

            scheduler.scheduleJob(job, trigger);

            scheduler.start();

            //scheduler.shutdown();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Data monitor error: " + e.getMessage(), e);
        }
    }

    private static JobDataMap loadConfiguration() throws IOException {
        InputStream is = null;
        JobDataMap jobDataMap = new JobDataMap();
        try {
            is = new BufferedInputStream(new FileInputStream(CONFIG_FILE));

            Properties p = new Properties();
            p.load(is);

            String intervalPropertyStr = p.getProperty(INTERVAL_PROPERTY);
            if (intervalPropertyStr == null) {
                throw new RuntimeException("Missing required property '" + INTERVAL_PROPERTY + "'");
            }
            try {
                Integer intervalPropery = Integer.parseInt(intervalPropertyStr);
                jobDataMap.put(INTERVAL_PROPERTY,intervalPropery);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid property '" + INTERVAL_PROPERTY +
                        "': integer value is expected");
            }

            String emailProperty = p.getProperty(EMAIL_PROPERTY);
            if (emailProperty == null) {
                throw new RuntimeException("Missing required property '" + EMAIL_PROPERTY + "'");
            }
            jobDataMap.put(EMAIL_PROPERTY,emailProperty);

            String usernameProperty = p.getProperty(DB_USERNAME_PROPERTY);
            if (usernameProperty == null) {
                throw new RuntimeException("Missing required property '" + DB_USERNAME_PROPERTY + "'");
            }
            jobDataMap.put(DB_USERNAME_PROPERTY,usernameProperty);


            String passwordProperty = p.getProperty(DB_PASSWORD_PROPERTY);
            if (passwordProperty == null) {
                throw new RuntimeException("Missing required property '" + DB_PASSWORD_PROPERTY + "'");
            }
            jobDataMap.put(DB_PASSWORD_PROPERTY,passwordProperty);

            String urlProperty = p.getProperty(DB_URL_PROPERTY);
            if (urlProperty == null) {
                throw new RuntimeException("Missing required property '" + DB_URL_PROPERTY + "'");
            }
            jobDataMap.put(DB_URL_PROPERTY,urlProperty);

            String mailHost = p.getProperty(MAIL_HOST_PROPERTY);
            if (mailHost == null) {
                throw new RuntimeException("Missing required property '" + MAIL_HOST_PROPERTY + "'");
            }
            jobDataMap.put(MAIL_HOST_PROPERTY,mailHost);

            String mailHostIp = p.getProperty(MAIL_HOST_IP_PROPERTY,mailHost);
            jobDataMap.put(MAIL_HOST_IP_PROPERTY,mailHostIp);

            String mailLogin = p.getProperty(MAIL_LOGIN_PROPERTY);
            if (mailLogin == null) {
                throw new RuntimeException("Missing required property '" + MAIL_LOGIN_PROPERTY + "'");
            }
            jobDataMap.put(MAIL_LOGIN_PROPERTY,mailLogin);

            String mailPassword = p.getProperty(MAIL_PASSWORD_PROPERTY);
            if (mailPassword == null) {
                throw new RuntimeException("Missing required property '" + MAIL_PASSWORD_PROPERTY + "'");
            }
            jobDataMap.put(MAIL_PASSWORD_PROPERTY,mailPassword);

            String mailPort = p.getProperty(MAIL_PORT_PROPERTY);
            if (mailPort == null) {
                throw new RuntimeException("Missing required property '" + MAIL_PORT_PROPERTY + "'");
            }
            jobDataMap.put(MAIL_PORT_PROPERTY,mailPort);

            String platform = p.getProperty(PLATFORM_PROPERTY);
            if (platform == null) {
                throw new RuntimeException("Missing required property '" + PLATFORM_PROPERTY + "'");
            }
            jobDataMap.put(PLATFORM_PROPERTY,platform);

            String pathToLogFile = p.getProperty(PATH_TO_LOG_FILE);
            if (pathToLogFile == null) {
                throw new RuntimeException("Missing required property '" + PATH_TO_LOG_FILE + "'");
            }
            jobDataMap.put(PATH_TO_LOG_FILE, pathToLogFile);

            String listOfIgnoredDatabaseNames = p.getProperty(NOT_MONITORED);
            if (listOfIgnoredDatabaseNames == null) {
                throw new RuntimeException("Missing required property '" + PATH_TO_LOG_FILE + "'");
            }
            jobDataMap.put(NOT_MONITORED, listOfIgnoredDatabaseNames);

            String attachLogsInterval = p.getProperty(ATTACH_LOGS_INTERVAL);
            try {
                Integer attachLogsIntervalInt = Integer.parseInt(attachLogsInterval);
                jobDataMap.put(ATTACH_LOGS_INTERVAL, attachLogsIntervalInt);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid property '" + ATTACH_LOGS_INTERVAL +
                        "': integer value is expected");
            }

            String mailDebug = p.getProperty(DEBUG_PROPERTY,"false");
            jobDataMap.put(DEBUG_PROPERTY,mailDebug);

            return jobDataMap;
        } catch (IOException e) {
            logger.error("I/O error", e);
            throw e;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error("Failed to close input stream", e);
                }
            }
        }
    }
}

