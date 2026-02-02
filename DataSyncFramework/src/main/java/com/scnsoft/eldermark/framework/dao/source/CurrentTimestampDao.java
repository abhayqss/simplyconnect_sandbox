package com.scnsoft.eldermark.framework.dao.source;


import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.exceptions.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CurrentTimestampDao {
    private static final Logger logger = LoggerFactory.getLogger(CurrentTimestampDao.class);

    public Long getCurrentTimeStamp(Sql4DOperations jdbcOperations) {
        String query = "Select {fn Epoch_OfDateTime() as Numeric}  AS TheEpoch from OneRecordTable";
        logger.info("Executing current timestamp query " + query);

        List<Long> resultList = jdbcOperations.queryForList(query, Long.class);
        final int expectedNumberOfRecords = 1;
        if (resultList.size() != expectedNumberOfRecords) {
            throw new DataAccessException("Unexpected return result for query " + query +
                    ": returned " + resultList.size() + " records, but expected " + expectedNumberOfRecords + " records");
        }

        Long result = resultList.get(0);
        return result;
    }
}
