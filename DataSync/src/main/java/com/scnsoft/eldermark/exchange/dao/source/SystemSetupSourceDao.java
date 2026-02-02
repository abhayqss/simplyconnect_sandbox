package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.model.source.SystemSetupData;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import org.springframework.jdbc.core.JdbcOperations;

public interface SystemSetupSourceDao {
    SystemSetupData getSystemSetup(Sql4DOperations sqlOperations);
}
