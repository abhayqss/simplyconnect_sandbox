package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.model.source.SettingsData;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import org.springframework.jdbc.core.JdbcOperations;

public interface SettingsSourceDao {
    SettingsData getSettings(Sql4DOperations sqlOperations);
}
