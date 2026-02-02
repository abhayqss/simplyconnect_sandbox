package com.scnsoft.exchange.audit.dao.mappers;

import com.scnsoft.exchange.audit.model.SyncRange;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SyncReportRangeRowMapper implements RowMapper<SyncRange> {

    @Override
    public SyncRange mapRow(ResultSet rs, int rowNum) throws SQLException {
        SyncRange entry = new SyncRange();

        entry.setFrom(rs.getTimestamp("started"));
        entry.setTo(rs.getTimestamp("completed"));

        return entry;
    }
}
