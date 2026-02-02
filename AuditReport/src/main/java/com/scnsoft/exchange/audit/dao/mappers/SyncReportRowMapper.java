package com.scnsoft.exchange.audit.dao.mappers;

import com.scnsoft.exchange.audit.model.SyncReportEntry;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SyncReportRowMapper implements RowMapper<SyncReportEntry> {

    @Override
    public SyncReportEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        SyncReportEntry entry = new SyncReportEntry();

        entry.setDatabaseName(rs.getString("database_name"));

        long tmp = rs.getLong("error_count");
        if(!rs.wasNull())
            entry.setErrorCount(tmp);

        entry.setStatus(rs.getBoolean("result"));

        entry.setLastSyncDate(rs.getTimestamp("last_success_sync_date"));

        return entry;
    }
}
