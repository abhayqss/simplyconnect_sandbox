package com.scnsoft.exchange.audit.dao.mappers;

import com.scnsoft.exchange.audit.model.DataSyncStatsDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DataSyncStatsRowMapper implements RowMapper<DataSyncStatsDto> {

    @Override
    public DataSyncStatsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        DataSyncStatsDto entry = new DataSyncStatsDto();

        long tmp = rs.getLong("iteration_number");
        if(!rs.wasNull())
            entry.setIterationNumber(tmp);

        entry.setStarted(rs.getTimestamp("started"));
        entry.setCompleted(rs.getTimestamp("completed"));
        entry.setDatabaseName(rs.getString("database_name"));
        entry.setDuration(rs.getString("duration"));
        entry.setSyncServiceName(rs.getString("sync_service_name"));

        return entry;
    }
}