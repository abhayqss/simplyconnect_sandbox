package com.scnsoft.eldermark;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ResetSyncStatusMain {
    private static final Logger logger = LoggerFactory.getLogger(ResetSyncStatusMain.class);

    public static void main(String[] args) throws Exception {
        List<Exception> fails = new ArrayList<Exception>();

        Constants c = new Constants();
        c.loadProperties();

        for(String url : c.SOURCE_DATABASES_URLS) {
            logger.info(url + "...");

            BasicDataSource dataSource = null;
            try {
                dataSource = new BasicDataSource();
                dataSource.setUrl(url);
                dataSource.setDriverClassName("sun.jdbc.odbc.JdbcOdbcDriver");
                dataSource.setInitialSize(1);

                JdbcOperations jdbcOperations = new JdbcTemplate(dataSource);
                resetSyncStatus(jdbcOperations, Constants.TABLE_NAMES, Constants.SYNC_STATUS_COLUMN);
                verifySyncStatus(jdbcOperations, Constants.TABLE_NAMES, Constants.SYNC_STATUS_COLUMN);

            } catch (Exception e) {
                fails.add(new Exception(url, e));
            } finally {
                if (dataSource != null) {
                    dataSource.close();
                }
            }
        }

        if (!fails.isEmpty()) {
            for (Exception e : fails) {
                logger.error("URL: " + e.getMessage() + " " + e.getCause());
            }
            logger.info("Process is completed with errors.");
        } else {
            logger.info("Process is completed without errors.");
        }
    }

    private static void resetSyncStatus(JdbcOperations jdbcOperations, String[] tableNames, String syncStatusColumn) {
        for (String tableName: tableNames) {
            logger.info("..." + tableName);
            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE ").append(tableName).append(" SET ").append(syncStatusColumn).append("='' WHERE ")
                    .append(syncStatusColumn).append(" IN ('S', 'P')");
            executeUpdate(jdbcOperations, sb.toString());
        }
    }

    private static void verifySyncStatus(JdbcOperations jdbcOperations, String[] tableNames, String syncStatusColumn) {
        boolean passed = true;
        StringBuilder logMessageBuilder = new StringBuilder();

        for (String tableName: tableNames) {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT COUNT(*) FROM ").append(tableName).append(" WHERE ").append(syncStatusColumn)
                    .append(" IN ('S', 'P')");
            Integer numberOfRecords = jdbcOperations.queryForObject(queryBuilder.toString(), Integer.class);
            if (numberOfRecords == null) {
                numberOfRecords = 0;
            }

            logMessageBuilder.append('\t').append(tableName).append(": ").append(numberOfRecords).append(" records");
            if (numberOfRecords != 0) {
                passed = false;
                logMessageBuilder.append(" - SYNC STATUS RESET FAILED\n");
            }
        }
        if (!passed) {
            logger.error(logMessageBuilder.toString());
            throw new RuntimeException(logMessageBuilder.toString());
        }
    }

    private static void executeUpdate(JdbcOperations jdbcOperations, String updateQuery) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT {fn SQLFN_ExecuteSql('").append(base64Encode(updateQuery))
                .append("') AS VARCHAR} AS FunctionResult FROM OneRecordTable");
        String encodedSqlQuery = sb.toString();
        String queryInfo = "'" + updateQuery + "' (encoded as '" + encodedSqlQuery + "')";

        logger.debug("Executing query " + queryInfo);

        List<String> resultList = jdbcOperations.queryForList(encodedSqlQuery, String.class);
        final int expectedNumberOfRecords = 1;
        if (resultList.size() != expectedNumberOfRecords) {
            throw new RuntimeException("Unexpected return result for query " + queryInfo +
                    ": returned " + resultList.size() + " records, but expected " + expectedNumberOfRecords + " records");
        }

        String result = resultList.get(0);
        if (!"SUCCESS".equalsIgnoreCase(result)) {
            if (result.contains("1221: SQLS")) { //Code 1221 means locked record according to 4D documentation
                throw new RuntimeException("Locked record has been found while executing query " + queryInfo
                        + ": result is '" + result + "'");
            } else {
                throw new RuntimeException("Failed to execute query " + queryInfo + ": result is '" + result + "'");
            }
        }
    }

    private static String base64Encode(String text) {
        try {
            return Base64.encodeBase64String(text.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to base64 encode text: '" + text + "'", e);
        }
    }
}
