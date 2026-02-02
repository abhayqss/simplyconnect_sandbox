package com.scnsoft.eldermark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class TablesInfoDaoImpl implements TablesInfoDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${columns.that.need.generated.prefix}")
    private String[] columnsThatNeedGeneratedPrefix;

    @Override
    public void useDatabase(String databaseName) {
        jdbcTemplate.execute("USE " + databaseName);
    }

    @Override
    public List<String> getTables() {
        String sql = "SELECT table_name FROM information_schema.tables";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    public int getTableSize(String tableName) {
        String sql = "SELECT count(*) FROM " + tableName;
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public List<String> getPrimaryKeyColumns(String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT column_name FROM information_schema.table_constraints AS tc")
                .append(" INNER JOIN information_schema.key_column_usage AS kcu")
                .append(" ON tc.constraint_type = 'PRIMARY KEY' AND")
                .append(" tc.constraint_name = kcu.constraint_name")
                .append(" AND kcu.table_name='").append(tableName).append("'");
        String sql = sb.toString();
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    public long getPrimaryKeySum(String tableName) {
        String sql = "SELECT sum(" + Constants.EXPECTED_PRIMARY_KEY_NAME + ") FROM " + tableName;
        Long sum = jdbcTemplate.queryForObject(sql, Long.class);
        if (sum == null) {
            sum = 0L;
        }
        return sum;
    }

    @Override
    public List<ForeignKey> getForeignKeys(String tableName) {
        String sql = "EXEC sp_fkeys @fktable_name='" + tableName + "';";
        return jdbcTemplate.query(sql, new RowMapper<ForeignKey>() {
            @Override
            public ForeignKey mapRow(ResultSet rs, int rowNum) throws SQLException {
                String referencingColumn = rs.getString("FKCOLUMN_NAME");
                String referencedTable = rs.getString("PKTABLE_NAME");
                return new ForeignKey(referencingColumn, referencedTable);
            }
        });
    }

    @Override
    public List<ColumnInfo> getColumnsInfo(String tableName) {
        String sql = "select COLUMN_NAME, DATA_TYPE from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME='"
                + tableName + "';";
        return jdbcTemplate.query(sql, new RowMapper<ColumnInfo>() {
            @Override
            public ColumnInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                String columnName = rs.getString("COLUMN_NAME");
                String columnType = rs.getString("DATA_TYPE");

                ColumnInfo columnInfo = new ColumnInfo(columnName, columnType);
                return columnInfo;
            }
        });
    }

    @Override
    public void copyTable(TableInfo tableInfo, Map<String, TableInfo> tableInfoMap, String sourceDatabase, String targetDatabase, int copyNumber) {
        String tableName = tableInfo.getTableName();

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(targetDatabase).append(".dbo.").append(tableName).append(" (");

        List<ColumnInfo> columnsInfo = tableInfo.getColumnsInfo();
        for (int i = 0; i < columnsInfo.size(); i ++) {
            ColumnInfo columnInfo = columnsInfo.get(i);
            sb.append(columnInfo.getColumnName());
            if (i < columnsInfo.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append(") SELECT ");

        for (int i = 0; i < columnsInfo.size(); i ++) {
            ColumnInfo columnInfo = columnsInfo.get(i);
            String columnName = columnInfo.getColumnName();

            //Adjust primary and foreign keys accordingly
            if (tableInfo.isPrimaryKey(columnName)) {
                sb.append(columnName).append(" + (").append(copyNumber).append("-1) * ")
                        .append(tableInfo.getTableSize());
            } else if (tableInfo.isForeignKey(columnName)) {
                String referencedTable = tableInfo.getReferencedTable(columnName);
                TableInfo referencedTableInfo = tableInfoMap.get(referencedTable);
                sb.append(columnName).append(" + (").append(copyNumber).append("-1) * ")
                        .append(referencedTableInfo.getTableSize());
            } else if (isGeneratedPrefixNeeded(tableName, columnName)) {
                sb.append("'copy").append(copyNumber).append("' + ").append(columnName);
            } else {
                sb.append(columnName);
            }

            if (i < columnsInfo.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append(" FROM ").append(sourceDatabase).append(".dbo.").append(tableName);
        String sql = sb.toString();
        jdbcTemplate.execute(sql);
    }

    private boolean isGeneratedPrefixNeeded(String tableName, String columnName) {
        StringBuilder sb = new StringBuilder();
        sb.append(tableName).append(".").append(columnName);
        String fullColumnName = sb.toString();
        return columnsThatNeedGeneratedPrefix != null &&
                Arrays.asList(columnsThatNeedGeneratedPrefix).contains(fullColumnName);
    }

    @Override
    public void enableIdentityInsertion(String tableName) {
        setIdentityInsert(tableName, true);
    }

    @Override
    public void disableIdentityInsertion(String tableName) {
        setIdentityInsert(tableName, false);
    }

    private void setIdentityInsert(String tableName, boolean isEnabled) {
        StringBuilder sb = new StringBuilder();
        sb.append("SET IDENTITY_INSERT ").append(tableName).append(" ");
        sb.append(isEnabled ? "ON" : "OFF");

        final String sql = sb.toString();
        jdbcTemplate.execute(sql);
    }
}
