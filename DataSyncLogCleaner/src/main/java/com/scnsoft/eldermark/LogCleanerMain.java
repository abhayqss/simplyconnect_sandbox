package com.scnsoft.eldermark;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class LogCleanerMain {
    private static final Logger logger = LoggerFactory.getLogger(LogCleanerMain.class);

    @Value("${database.url}")
    private String url;

    @Value("${database.driverClass}")
    private String driverClass;

    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    @Value("${maxSyncLogAge}")
    private long maxSyncLogAge;

    @Value("${maxDataLogAge}")
    private long maxDataLogAge;

    @Value("${maxDeletionHistoryLogAge}")
    private long maxDeletionHistoryLogAge;

    @Value("${maxStatisticsAge}")
    private long maxStatisticsAge;

    private static final long BATCH_LIMIT = 100000;

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/appContext.xml");

        SchedulerFactoryBean schedulerFactoryBean = ctx.getBean(SchedulerFactoryBean.class);
        schedulerFactoryBean.start();
    }

    public void cleanLog() {
        logger.info("Cleaning DataSync logs...");
        BasicDataSource dataSource = null;
        try {
            dataSource = new BasicDataSource();
            dataSource.setUrl(url);
            dataSource.setDriverClassName(driverClass);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setInitialSize(1);

            JdbcOperations jdbcOperations = new JdbcTemplate(dataSource);

            Date now = new Date();

            logger.info("Cleaning status log...");
            jdbcOperations.query(
                    safeDeleteQuery("DataSyncLog", "date < ?"), new Object[] {new Date(now.getTime() - maxSyncLogAge)},
                    new SingleColumnRowMapper<String>(String.class));
            logger.info("Status log has been cleaned");

            logger.info("Cleaning data log...");
            jdbcOperations.query(
                    safeDeleteQuery("DataSyncDataLog", "date < ?"), new Object[] {new Date(now.getTime() - maxDataLogAge)},
                    new SingleColumnRowMapper<String>(String.class));
            logger.info("Data log has been cleaned");

            logger.info("Cleaning deletion history log...");
            jdbcOperations.query(
                    safeDeleteQuery("RecordDeletionHistory", "creation_date < ?"),
                    new Object[] {new Date(now.getTime() - maxDeletionHistoryLogAge)},
                    new SingleColumnRowMapper<String>(String.class));
            logger.info("Deletion history has been cleaned");

            //logger.info("Cleaning statistics...");
            //jdbcOperations.query(
              //      safeDeleteQuery("DataSyncStats", "COALESCE(completed, started) < ?"),
              //      new Object[] {new Date(now.getTime() - maxStatisticsAge,
              //      new SingleColumnRowMapper<String>(String.class));
            //logger.info("Statistics has been cleaned");

            logger.info("Cleaning DataSync log is completed");
        } catch (Exception e) {
             logger.error("Failed to clean DataSync log", e);
        } finally {
            close(dataSource);
        }
    }

    // a faster delete query for huge data sets
    // that deals with transaction log overflow
    private static String safeDeleteQuery(String table, String whereClause) {
        return  "SELECT 'start'; " +     // sets @@ROWCOUNT = 1
                "WHILE @@ROWCOUNT > 0" +
                "   DELETE TOP (" + BATCH_LIMIT + ") FROM [" + table + "] WHERE " + whereClause + ";";
    }

    private void close(BasicDataSource dataSource) {
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (Exception e) {
                logger.error("Failed to close database connection", e);
            }
        }
    }
}
