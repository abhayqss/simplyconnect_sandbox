package com.scnsoft.exchange.audit.dao.mappers;

import com.scnsoft.exchange.audit.model.DataSyncLogDto;
import com.scnsoft.exchange.audit.model.SyncReportEntry;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DataSyncLogReportRowMapper implements RowMapper<DataSyncLogDto> {

    @Override
    public DataSyncLogDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        DataSyncLogDto entry = new DataSyncLogDto();

        long tmp = rs.getLong("id");
        if(!rs.wasNull())
            entry.setId(tmp);
        entry.setDate(rs.getTimestamp("date"));
        entry.setType(rs.getString("type_name"));
        entry.setDescription(rs.getString("description"));
        entry.setTableName(rs.getString("table_name"));
        entry.setStackTrace(rs.getString("stack_trace"));
        entry.setDatabaseName(rs.getString("database_name"));

        tmp = rs.getLong("iteration_number");
        if(!rs.wasNull())
            entry.setIterationNumber(tmp);

        return entry;
    }
}
