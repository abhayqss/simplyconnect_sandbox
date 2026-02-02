package com.scnsoft.eldermark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

@Service
public class DataDuplicationServiceImpl implements DataDuplicationService {
    @Value("${source.database.name}")
    private String sourceDatabaseName;

    @Value("${target.database.name}")
    private String targetDatabaseName;

    @Autowired
    private TablesInfoDao tablesInfoDao;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public void duplicateData(int numberOfCopies) {
        Map<String, TableInfo> tablesInfoMap = buildTablesInfoMap();
        List<String> tablesCopyOrder = defineTablesCopyOrder(tablesInfoMap);
        System.out.println("Tables copy order: " + tablesCopyOrder);

        for (String table : tablesCopyOrder) {
            TableInfo tableInfo = tablesInfoMap.get(table);
            copyTable(tableInfo, tablesInfoMap, numberOfCopies);
        }
    }

    private Map<String, TableInfo> buildTablesInfoMap() {
        final Map<String, TableInfo> tablesInfoMap = new HashMap<String, TableInfo>();

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                tablesInfoDao.useDatabase(sourceDatabaseName);
                Collection<String> tables = tablesInfoDao.getTables();
                for (String tableName : tables) {
                    int tableSize = tablesInfoDao.getTableSize(tableName);
                    List<ColumnInfo> columnsInfo = tablesInfoDao.getColumnsInfo(tableName);
                    List<ForeignKey> foreignKeys = tablesInfoDao.getForeignKeys(tableName);

                    validateTable(tableName, tableSize);

                    TableInfo tableInfo = new TableInfo(tableName, tableSize, columnsInfo, foreignKeys);
                    tablesInfoMap.put(tableName, tableInfo);

                    System.out.println(tableName + "->" + tableInfo.getDependencyTables());
                }
            }
        });
        return Collections.unmodifiableMap(tablesInfoMap);
    }

    private List<String> defineTablesCopyOrder(Map<String, TableInfo> tablesInfoMap) {
        Set<String> unprocessedTables = new HashSet<String>(tablesInfoMap.keySet());
        Set<String> processedTables = new LinkedHashSet<String>();

        Set<String> currentIterationTables;
        do {
            currentIterationTables = new HashSet<String>();

            for (String tableName : unprocessedTables) {
                TableInfo tableInfo = tablesInfoMap.get(tableName);
                Collection<String> dependencyTables = tableInfo.getDependencyTables();
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

        return new ArrayList<String>(processedTables);
    }

    private void validateTable(String tableName, int tableSize) {
        List<String> primaryKeys = tablesInfoDao.getPrimaryKeyColumns(tableName);
        if (primaryKeys.size() > 1) {
            throw new IllegalStateException("Only single primary key for table is supported: table '" + tableName + "'");
        }

        if (primaryKeys.size() == 1) {
            if (!Constants.EXPECTED_PRIMARY_KEY_NAME.equalsIgnoreCase(primaryKeys.get(0))) {
                throw new IllegalStateException("Expected primary key name is '" +
                        Constants.EXPECTED_PRIMARY_KEY_NAME + "': table '" + tableName + "'");
            }

            //The assumption to manage foreign keys correctly is that all primary keys in original
            //database are sequential and start with 1. We should ensure this condition, otherwise,
            //data duplication isn't possible and a runtime exception will be thrown.
            long primaryKeySum = tablesInfoDao.getPrimaryKeySum(tableName);
            long expectedSum = (1L + tableSize) * tableSize / 2;
            System.out.println("Checking table primary keys: '" + tableName + "': primaryKeySum=" + primaryKeySum +
                ", expected sum=" + expectedSum);


            if (primaryKeySum != expectedSum) {
                throw new IllegalStateException("Primary keys in table '" + tableName + "' are not sequential");
            }
        }
    }

    private void copyTable(final TableInfo tableInfo, final Map<String, TableInfo> tablesInfoMap, int numberOfCopies) {
        final String tableName = tableInfo.getTableName();

        for (int i = 1; i <= numberOfCopies; i++) {
            final int copyNumber = i;

            System.out.println("Copying table " + tableName + " copy #" + copyNumber + "...");
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    tablesInfoDao.useDatabase(targetDatabaseName);
                    if (tableInfo.hasPrimaryKey()) {
                        tablesInfoDao.enableIdentityInsertion(tableName);
                    }

                    tablesInfoDao.copyTable(tableInfo, tablesInfoMap, sourceDatabaseName,
                            targetDatabaseName, copyNumber);

                    if (tableInfo.hasPrimaryKey()) {
                        tablesInfoDao.disableIdentityInsertion(tableName);
                    }
                }
            });
        }
    }
}
