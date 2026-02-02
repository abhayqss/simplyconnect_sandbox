package com.scnsoft.eldermark;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CleanTargetDatabaseMain {
    private static final Logger logger = LoggerFactory.getLogger(CleanTargetDatabaseMain.class);
    private static final String URL = "--url";
    private static final String DRIVER = "--driver";
    private static final String DATABASE_ID = "--database-id";

    public static void main(String[] args) throws Exception {
        Map<String, String> argsMap = new HashMap<String, String>();

        int i = 0;
        while (i < args.length) {
            String arg = args[i];
            if (URL.equals(arg) || DRIVER.equals(arg) || DATABASE_ID.equals(arg)) {
                if (i + 1 >= args.length) {
                    System.err.println("Missing value for " + arg);
                    System.exit(1);
                }

                String argValue = args[i + 1];
                argsMap.put(arg, argValue);

                i += 2;
            }
        }

        for (String argName : Arrays.asList(URL, DRIVER, DATABASE_ID)) {
            if (!argsMap.containsKey(argName)) {
                System.err.println("Missing required argument: " + argName);
                System.exit(0);
            }
        }

        String username = null;
        while (username == null || username.isEmpty()) {
            System.out.println("Enter username for database connection:");
            username = readUsername();
        }

        String password = null;
        while (password == null || password.isEmpty()) {
            System.out.println("Enter password for database connection: ");
            password = readPassword();
        }

        long databaseId;
        try {
            databaseId = Long.valueOf(argsMap.get(DATABASE_ID));
        } catch (NumberFormatException e) {
            logger.error("Invalid database id format: integer number is expected", e);
            throw e;
        }

        BasicDataSource dataSource = null;
        try {
            dataSource = new BasicDataSource();
            dataSource.setUrl(argsMap.get(URL));
            dataSource.setDriverClassName(argsMap.get(DRIVER));
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setInitialSize(1);

            JdbcOperations jdbcOperations = new JdbcTemplate(dataSource);
            List<String> tablesOrdered = defineTablesCleanupOrder(jdbcOperations);

            removeExcludedTables(tablesOrdered, Constants.EXCLUDED_TABLES);
            cleanupTables(jdbcOperations, tablesOrdered, databaseId);
            boolean isSuccessful = verifyTablesCleaned(jdbcOperations, tablesOrdered, databaseId);
            if (isSuccessful) {
                logger.info("Successfully cleaned up database tables for database_id=" + databaseId
                     + "; ignored the following tables: " + Arrays.toString(Constants.EXCLUDED_TABLES));
            } else {
                logger.error("Some tables could not be cleaned up for database_id=" + databaseId);
            }

        } catch (Exception e) {
            logger.error("Database cleanup failed", e);
            throw e;
        } finally {
            if (dataSource != null) {
                dataSource.close();
            }
        }
    }

    private static void removeExcludedTables(List<String> tables, String[] excludedTables) {
        for (String excludedTable : excludedTables) {
            if (!tables.remove(excludedTable)) {
                throw new RuntimeException("Unknown table " + excludedTable);
            }
        }
    }

    private static String readUsername() throws IOException {
        String username;
        Console console = System.console();
        if (console != null) {
            username = console.readLine();
        } else {
            username = new BufferedReader(new InputStreamReader(System.in)).readLine();
        }
        return username;
    }

    private static String readPassword() throws IOException {
        String password;
        Console console = System.console();
        if (console != null) {
            char[] passwordArray = console.readPassword();
            password = new String(passwordArray);
        } else {
            password = new BufferedReader(new InputStreamReader(System.in)).readLine();
        }
        return password;
    }

    private static List<String> getTables(JdbcOperations jdbcOperations) {
        return jdbcOperations.queryForList("SELECT table_name FROM information_schema.tables", String.class);
    }

    private static List<String> getDependencyTables(JdbcOperations jdbcOperations, String tableName) {
        String sql = "EXEC sp_fkeys @fktable_name='" + tableName + "';";
        List<String> referencedTables = jdbcOperations.query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("PKTABLE_NAME");
            }
        });

        //Remove duplicates (there may be multiple foreign keys to the same table)
        return new ArrayList<String>(new HashSet<String>(referencedTables));
    }

    private static List<String> defineTablesCleanupOrder(JdbcOperations jdbcOperations) {
        Map<String, TableInfo> tableInfoMap = new HashMap<String, TableInfo>();
        for (String table : getTables(jdbcOperations)) {
            TableInfo tableInfo = new TableInfo(getDependencyTables(jdbcOperations, table));
            tableInfoMap.put(table, tableInfo);
        }

        Set<String> unprocessedTables = new HashSet<String>(tableInfoMap.keySet());
        Set<String> processedTables = new LinkedHashSet<String>();

        Set<String> currentIterationTables;
        do {
            currentIterationTables = new HashSet<String>();

            for (String tableName : unprocessedTables) {
                TableInfo tableInfo = tableInfoMap.get(tableName);
                Collection<String> dependencyTables = tableInfo.getDependencyTables();
                dependencyTables = removeSelfDependency(dependencyTables, tableName);
                if (dependencyTables.size() == 0 || processedTables.containsAll(dependencyTables)) {
                    currentIterationTables.add(tableName);
                }
            }

            unprocessedTables.removeAll(currentIterationTables);
            processedTables.addAll(currentIterationTables);
        } while (currentIterationTables.size() > 0);

        if (unprocessedTables.size() > 0) {
            throw new IllegalStateException("Failed to process tables " + unprocessedTables +
                    " probably because of cyclic references between them");
        }

        List<String> orderedTables = new ArrayList<String>(processedTables);
        Collections.reverse(orderedTables);
        return orderedTables;
    }

    private static Collection<String> removeSelfDependency(Collection<String> dependencyTables, String tableName) {
        dependencyTables.remove(tableName);
        return dependencyTables;
    }

    private static void cleanupTables(JdbcOperations jdbcOperations, List<String> tables, long databaseId) {
        for (String table : tables) {
            StringBuilder sb = new StringBuilder();
            sb.append("DELETE FROM ").append(table).append(" WHERE database_id=?");
            logger.info(sb.toString());
            jdbcOperations.update(sb.toString(), databaseId);
        }
    }

    private static boolean verifyTablesCleaned(JdbcOperations jdbcOperations, List<String> tables, long databaseId) {
        boolean isSuccessful = true;
        for (String table : tables) {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT COUNT(*) FROM ").append(table).append(" WHERE database_id=?");

            int numberOfRows = jdbcOperations.queryForObject(sb.toString(), new Object[]{databaseId}, Integer.class);
            if (numberOfRows != 0) {
                isSuccessful = false;
                logger.error("Failed to clean table " + table);
            }
        }
        return isSuccessful;
    }
}
