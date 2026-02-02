package com.scnsoft.eldermark.framework.dao.source.operations;

import com.scnsoft.eldermark.framework.Utils;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.exceptions.DataAccessException;
import com.scnsoft.eldermark.framework.exceptions.LockedRecordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;

import java.util.List;

public class UpdateExecutor {
    private static final Logger logger = LoggerFactory.getLogger(UpdateExecutor.class);

    public void executeUpdate(Sql4DOperations sql4DOperations, String updateQuery) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT {fn SQLFN_ExecuteSql('").append(Utils.base64Encode(updateQuery))
                .append("') AS VARCHAR} AS FunctionResult FROM OneRecordTable");
        String encodedSqlQuery = sb.toString();
        String queryInfo = "'" + updateQuery + "' (encoded as '" + encodedSqlQuery + "')";

        logger.info("Executing query " + queryInfo);

        List<String> resultList = sql4DOperations.queryForList(encodedSqlQuery, String.class);
        final int expectedNumberOfRecords = 1;
        if (resultList.size() != expectedNumberOfRecords) {
            throw new DataAccessException("Unexpected return result for query " + queryInfo +
                    ": returned " + resultList.size() + " records, but expected " + expectedNumberOfRecords + " records");
        }

        String result = resultList.get(0);
        if (!"SUCCESS".equalsIgnoreCase(result)) {
            if (result.contains("1221: SQLS")) { //Code 1221 means locked record according to 4D documentation
                throw new LockedRecordException("Locked record has been found while executing query " + queryInfo
                        + ": result is '" + result + "'");
            } else {
                throw new DataAccessException("Failed to execute query " + queryInfo + ": result is '" + result + "'");
            }
        }
    }
}
