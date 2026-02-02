package com.scnsoft.exchange.audit.dao.mappers;

import com.scnsoft.exchange.audit.model.MpiReportEntry;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MpiReportRowMapper implements RowMapper<MpiReportEntry> {

    @Override
    public MpiReportEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        MpiReportEntry mpiReportEntry = new MpiReportEntry();

        mpiReportEntry.setStateName(rs.getString("state"));

        Long tmp = rs.getLong("clinical_transactions");
        if(!rs.wasNull()) mpiReportEntry.setGeneratedCCDNumber(tmp);
        tmp = rs.getLong("RLS_queries");
        if(!rs.wasNull()) mpiReportEntry.setPatientDiscoveryNumber(tmp);
        tmp = rs.getLong("resident_number");
        if(!rs.wasNull()) mpiReportEntry.setResidentNumber(tmp);

        return mpiReportEntry;
    }
}
