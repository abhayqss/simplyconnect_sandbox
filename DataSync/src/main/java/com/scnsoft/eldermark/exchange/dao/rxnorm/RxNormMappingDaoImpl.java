package com.scnsoft.eldermark.exchange.dao.rxnorm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RxNormMappingDaoImpl implements RxNormMappingDao {
    @Autowired
    @Qualifier("rxnormDatabaseJdbcTemplate")
    private JdbcOperations jdbcOperations;

    @Override
    public String getRxNormCode(String ndcCode) {
        if (!ndcCode.matches("[0-9]{11}")) {
            return null;
        }

        String query = "select CODE from RXNSAT where CHECKSUM('ndcCodeParam')=cs_ATV and ATV='ndcCodeParam'";

        // CHECKSUM(:ndcCodeParam) returns incorrect value on prod
        query = query.replaceAll("ndcCodeParam", ndcCode);

        List<String> rxnormCodes = jdbcOperations.queryForList(query, new Object[]{}, String.class);

        if (rxnormCodes.isEmpty()) {
            return null;
        } else if (rxnormCodes.size() == 1) {
            return rxnormCodes.get(0);
        } else {
            throw new RuntimeException("NDC to RxNorm mapping is not unique for NDC code '" + ndcCode + "'");
        }
    }
}
