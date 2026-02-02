package com.scnsoft.eldermark.framework.dao.source;

import com.scnsoft.eldermark.framework.connector4d.ColumnType4D;
import com.scnsoft.eldermark.framework.connector4d.ResultListParameters;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.dao.source.filters.DeletedKeysReadFilter;
import com.scnsoft.eldermark.framework.model.source.DeletedKeysData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DeletedKeysSourceDaoImpl implements DeletedKeysSourceDao {
    private final static Logger logger = LoggerFactory.getLogger(DeletedKeysSourceDaoImpl.class);

    private final static String DELETED_KEYS_TABLE = "DeleteKeys_Exch_Sync";

    private final static String SEQUENCE_NUM = "Sequence_Num";
    private final static String UUID = "UUID";
    private final static String TABLE_NAME = "Table_Name";
    private final static String KEY_NAME = "Key_Name";
    private final static String KEY_VALUE = "Key_Value";
    private final static String DATE_TIME = "DateTime";
    private final static String RECYCLE_BIN_REC_NUM = "Recycle_Bin_Rec_Num";
    private final static String EXCHANGE_SYNC_STATUS = "exchange_sync_status";
    private final static String SYNC_EPOCH = "lastmod_stamp";

    @Override
    public List<DeletedKeysData> read(Sql4DOperations sql4DOperations, DeletedKeysReadFilter filter) {
        logger.info("Reading deleted records log from table " + DELETED_KEYS_TABLE + "...");
        //No need to sync deleted records when  initial sync is running
        if (filter.getLastSyncEpoch() == 0) {
            return new ArrayList<DeletedKeysData>();
        }
        Long currentSyncEpoch = filter.getCurrentSyncEpoch();
        Long lastSyncEpoch = filter.getLastSyncEpoch();
        String tableName = filter.getTableName();
        Long idLowerBoundExclusive = filter.getIdLowerBoundExclusive();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ")
            .append(SEQUENCE_NUM).append(", ")
            .append("[").append(UUID).append("], ")
            .append(TABLE_NAME).append(", ")
            .append(KEY_NAME).append(", ")
            .append(KEY_VALUE).append(", ")
            .append("[").append(DATE_TIME).append("], ")
            .append(RECYCLE_BIN_REC_NUM).append(", ")
            .append(EXCHANGE_SYNC_STATUS)
        .append(" FROM ").append(DELETED_KEYS_TABLE);

        sb.append(" WHERE ");
        sb.append(SYNC_EPOCH).append(" >= ").append(lastSyncEpoch.toString());
        sb.append(" AND ");
        sb.append(SYNC_EPOCH).append(" < ").append(currentSyncEpoch.toString());


        if (tableName != null) {
            sb.append(" AND ");
            sb.append(buildSql(tableName));
        }

        if (idLowerBoundExclusive != null) {
            sb.append(" AND ");
            sb.append(SEQUENCE_NUM).append(" > ").append(idLowerBoundExclusive);
        }

        if (filter.isOrderById()) {
            sb.append(" ORDER BY ").append(SEQUENCE_NUM);
        }

        if (filter.getLimit() != null) {
            sb.append(" LIMIT ").append(filter.getLimit());
        }

        String query = sb.toString();

        logger.info("Executing query: " + query + "...");
        List<DeletedKeysData> result = sql4DOperations.query(query,
                new ResultListParameters(
                        new ResultListParameters.Parameter(SEQUENCE_NUM, ColumnType4D.LONG),
                        new ResultListParameters.Parameter(UUID, ColumnType4D.STRING),
                        new ResultListParameters.Parameter(TABLE_NAME, ColumnType4D.STRING),
                        new ResultListParameters.Parameter(KEY_NAME, ColumnType4D.STRING),
                        new ResultListParameters.Parameter(KEY_VALUE, ColumnType4D.STRING),
                        new ResultListParameters.Parameter(DATE_TIME, ColumnType4D.STRING),
                        new ResultListParameters.Parameter(RECYCLE_BIN_REC_NUM, ColumnType4D.LONG),
                        new ResultListParameters.Parameter(EXCHANGE_SYNC_STATUS, ColumnType4D.STRING)
                ),
                new RowMapper<DeletedKeysData>() {
                    @Override
                    public DeletedKeysData mapRow(ResultSet rs, int rowNum) throws SQLException {
                        DeletedKeysData recordDeletion = new DeletedKeysData();
                        recordDeletion.setUuid(rs.getString(UUID));
                        recordDeletion.setTableName(rs.getString(TABLE_NAME));
                        recordDeletion.setKeyName(rs.getString(KEY_NAME));
                        recordDeletion.setKeyValue(rs.getString(KEY_VALUE));
                        recordDeletion.setDateTime(rs.getDate(DATE_TIME));
                        recordDeletion.setSequenceNum(rs.getLong(SEQUENCE_NUM));
                        recordDeletion.setRecycleBinRecNum(rs.getLong(RECYCLE_BIN_REC_NUM));
                        return recordDeletion;
                    }
                }
        );
        logger.info("Query " + query + " has been executed.");
        return result;
    }

    private String buildSql(String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append(TABLE_NAME).append("='").append(tableName).append("'");
        return sb.toString();
    }

}
