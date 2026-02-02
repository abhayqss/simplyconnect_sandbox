package com.scnsoft.exchange.audit.dao.mappers;

import com.scnsoft.exchange.audit.model.LogDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuditLogRowMapper implements RowMapper<LogDto> {

    @Override
    public LogDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        LogDto log = new LogDto();

        log.setAction(rs.getString("action"));
        log.setDate(rs.getTimestamp("date"));
        log.setIpAddress(rs.getString("remote_address"));
        log.setEmployeeLogin(rs.getString("employee_login"));
        log.setResidentFirstName(rs.getString("resident_first_name"));
        log.setResidentLastName(rs.getString("resident_last_name"));
        log.setDocumentTitle(rs.getString("document_title"));
        log.setDatabaseName(rs.getString("database_name"));

        Long tmp = rs.getLong("id");
        if(!rs.wasNull()) log.setId(tmp);
        tmp = rs.getLong("employee_id");
        if(!rs.wasNull()) log.setEmployeeId(tmp);
        tmp = rs.getLong("resident_id");
        if(!rs.wasNull()) log.setResidentId(tmp);
        tmp = rs.getLong("document_id");
        if(!rs.wasNull()) log.setDocumentId(tmp);
        tmp = rs.getLong("database_id");
        if(!rs.wasNull()) log.setDatabaseId(tmp);

        return log;
    }
}
