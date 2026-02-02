package com.scnsoft.exchange.audit.dao;

import com.scnsoft.exchange.audit.dao.mappers.AuditLogRowMapper;
import com.scnsoft.exchange.audit.model.LogDto;
import com.scnsoft.exchange.audit.model.filters.FilterBy;
import com.scnsoft.exchange.audit.model.filters.ReportFilter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class AuditLogsDaoImpl extends BaseDao implements ReportDao<LogDto> {

    @Override
    public List<LogDto> findAll(ReportFilter filter) {
        Date from = filter.getCriteria(FilterBy.DATE_FROM, Date.class);
        Date to = filter.getCriteria(FilterBy.DATE_TO, Date.class);
        Long companyId = filter.getCriteria(FilterBy.COMPANY_ID, Long.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ( ");
        sql.append("SELECT ");
        sql.append("    AuditLog.id as 'id', action, date, remote_address, ");
        sql.append("    Employee.Id as 'employee_id', login AS 'employee_login', ");
        sql.append("    Resident.Id AS 'resident_id', given AS 'resident_first_name', family AS 'resident_last_name', ");
        sql.append("    Document.Id AS 'document_id', document_title, ");
        sql.append("    SourceDatabase.Id AS 'database_id', SourceDatabase.Name AS 'database_name' ");
        sql.append("FROM AuditLog ");
        sql.append("    LEFT JOIN AuditLog_Residents ON AuditLog_Residents.audit_log_id = AuditLog.id ");
        sql.append("        LEFT JOIN Resident ON AuditLog_Residents.resident_id = Resident.id ");
        sql.append("        LEFT JOIN Name ON Name.person_id = Resident.person_id ");
        sql.append("    LEFT JOIN AuditLog_Documents ON AuditLog_Documents.audit_log_id = AuditLog.id ");
        sql.append("        LEFT JOIN Document ON Document.id = AuditLog_Documents.document_id ");
        sql.append("    LEFT JOIN Employee ON Employee.id = AuditLog.employee_id ");
        sql.append("        LEFT JOIN SourceDatabase ON Employee.database_id = SourceDatabase.id ");
        sql.append("WHERE AuditLog.action NOT LIKE '%DOCUMENT%' ");
        sql.append("UNION ");
        sql.append("SELECT ");
        sql.append("    AuditLog.id as 'id', action, date, remote_address, ");
        sql.append("    Employee.Id as 'employee_id', login AS 'employee_login', ");
        sql.append("    Resident.Id AS 'resident_id', given AS 'resident_first_name', family AS 'resident_last_name', ");
        sql.append("    Document.Id AS 'document_id', document_title, ");
        sql.append("    employee_db.Id AS 'database_id', employee_db.Name AS 'database_name' ");
        sql.append("FROM AuditLog ");
        sql.append("    LEFT JOIN Employee ON Employee.id = AuditLog.employee_id ");
        sql.append("        LEFT JOIN SourceDatabase employee_db ON Employee.database_id = employee_db.id ");
        sql.append("    LEFT JOIN AuditLog_Documents ON AuditLog_Documents.audit_log_id = AuditLog.id ");
        sql.append("        LEFT JOIN Document ON Document.id = AuditLog_Documents.document_id ");
        sql.append("            LEFT JOIN SourceDatabase on Document.res_db_alt_id = SourceDatabase.alternative_id ");
        sql.append("            LEFT JOIN Resident on (Resident.legacy_id = Document.res_legacy_id and Resident.database_id = SourceDatabase.id) ");
        sql.append("                LEFT JOIN Name ON Name.person_id = Resident.person_id ");
        sql.append("WHERE AuditLog.action LIKE '%DOCUMENT%' ");
        sql.append(") res_table ");

        List<Object> params = new ArrayList<Object>();

        if(from != null) {
            sql.append(" WHERE res_table.date >= ? ");
            params.add(from);
        }

        if(to != null) {
            if(params.isEmpty()) {
                sql.append(" WHERE res_table.date < ? ");
            } else {
                sql.append(" AND res_table.date < ? ");
            }
            params.add(to);
        }

        if(companyId != null) {
            if(params.isEmpty()) {
                sql.append(" WHERE res_table.database_id = ? ");
            } else {
                sql.append(" AND res_table.database_id = ? ");
            }
            params.add(companyId);
        }

        sql.append(" ORDER BY res_table.date ASC ");

        return getJdbcTemplate().query(sql.toString(), params.toArray(), new AuditLogRowMapper());
    }

    @Override
    public List<LogDto> findAll(int offset, int limit, ReportFilter filter) {
        Date from = filter.getCriteria(FilterBy.DATE_FROM, Date.class);
        Date to = filter.getCriteria(FilterBy.DATE_TO, Date.class);
        Long companyId = filter.getCriteria(FilterBy.COMPANY_ID, Long.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ( ");
        sql.append("SELECT *, ROW_NUMBER() OVER (ORDER BY date DESC) AS RowNum FROM ( ");
        sql.append("    SELECT ");
        sql.append("        AuditLog.id as 'id', action, date, remote_address, ");
        sql.append("        Employee.Id as 'employee_id', login AS 'employee_login', ");
        sql.append("        Resident.Id AS 'resident_id', given AS 'resident_first_name', family AS 'resident_last_name', ");
        sql.append("        Document.Id AS 'document_id', document_title, ");
        sql.append("        SourceDatabase.Id AS 'database_id', SourceDatabase.Name AS 'database_name' ");
        sql.append("    FROM AuditLog ");
        sql.append("        LEFT JOIN AuditLog_Residents ON AuditLog_Residents.audit_log_id = AuditLog.id ");
        sql.append("            LEFT JOIN Resident ON AuditLog_Residents.resident_id = Resident.id ");
        sql.append("            LEFT JOIN Name ON Name.person_id = Resident.person_id ");
        sql.append("        LEFT JOIN AuditLog_Documents ON AuditLog_Documents.audit_log_id = AuditLog.id ");
        sql.append("            LEFT JOIN Document ON Document.id = AuditLog_Documents.document_id ");
        sql.append("        LEFT JOIN Employee ON Employee.id = AuditLog.employee_id ");
        sql.append("            LEFT JOIN SourceDatabase ON Employee.database_id = SourceDatabase.id ");
        sql.append("    WHERE AuditLog.action NOT LIKE '%DOCUMENT%' ");
        sql.append("    UNION ");
        sql.append("    SELECT ");
        sql.append("        AuditLog.id as 'id', action, date, remote_address, ");
        sql.append("        Employee.Id as 'employee_id', login AS 'employee_login', ");
        sql.append("        Resident.Id AS 'resident_id', given AS 'resident_first_name', family AS 'resident_last_name', ");
        sql.append("        Document.Id AS 'document_id', document_title, ");
        sql.append("        employee_db.Id AS 'database_id', employee_db.Name AS 'database_name' ");
        sql.append("    FROM AuditLog ");
        sql.append("        LEFT JOIN Employee ON Employee.id = AuditLog.employee_id ");
        sql.append("            LEFT JOIN SourceDatabase employee_db ON Employee.database_id = employee_db.id ");
        sql.append("        LEFT JOIN AuditLog_Documents ON AuditLog_Documents.audit_log_id = AuditLog.id ");
        sql.append("            LEFT JOIN Document ON Document.id = AuditLog_Documents.document_id ");
        sql.append("                LEFT JOIN SourceDatabase on Document.res_db_alt_id = SourceDatabase.alternative_id ");
        sql.append("                LEFT JOIN Resident on (Resident.legacy_id = Document.res_legacy_id and Resident.database_id = SourceDatabase.id) ");
        sql.append("                    LEFT JOIN Name ON Name.person_id = Resident.person_id ");
        sql.append("    WHERE AuditLog.action LIKE '%DOCUMENT%' ");
        sql.append("    ) AS res_table ");

        List<Object> params = new ArrayList<Object>();

        if(from != null) {
            sql.append("WHERE res_table.date >= ? ");
            params.add(from);
        }

        if(to != null) {
            if(params.isEmpty()) {
                sql.append("WHERE res_table.date < ? ");
            } else {
                sql.append("AND res_table.date < ? ");
            }
            params.add(to);
        }

        if(companyId != null) {
            if(params.isEmpty()) {
                sql.append("WHERE res_table.database_id = ? ");
            } else {
                sql.append("AND res_table.database_id = ? ");
            }
            params.add(companyId);
        }

        sql.append(") AS enumeratedTable ");
        sql.append("WHERE enumeratedTable.RowNum BETWEEN ? AND ? ");

        params.add(offset + 1);
        params.add(offset + limit);

        return getJdbcTemplate().query(sql.toString(), params.toArray(), new AuditLogRowMapper());
    }

    @Override
    public int count(ReportFilter filter) {
        Date from = filter.getCriteria(FilterBy.DATE_FROM, Date.class);
        Date to = filter.getCriteria(FilterBy.DATE_TO, Date.class);
        Long companyId = filter.getCriteria(FilterBy.COMPANY_ID, Long.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT (*) ");
        sql.append("FROM AuditLog ");
        sql.append("    LEFT JOIN AuditLog_Residents ON AuditLog_Residents.audit_log_id = AuditLog.id ");
        sql.append("    LEFT JOIN AuditLog_Documents ON AuditLog_Documents.audit_log_id = AuditLog.id ");
        sql.append("    LEFT JOIN Employee ON Employee.id = AuditLog.employee_id ");
        sql.append("        LEFT JOIN SourceDatabase ON Employee.database_id = SourceDatabase.id ");

        List<Object> params = new ArrayList<Object>();

        if(from != null) {
            sql.append(" WHERE AuditLog.date >= ? ");
            params.add(from);
        }

        if(to != null) {
            if(params.isEmpty()) {
                sql.append(" WHERE AuditLog.date < ? ");
            } else {
                sql.append(" AND AuditLog.date < ? ");
            }
            params.add(to);
        }

        if(companyId != null) {
            if(params.isEmpty()) {
                sql.append(" WHERE SourceDatabase.id = ?");
            } else {
                sql.append(" AND SourceDatabase.id = ?");
            }
            params.add(companyId);
        }

        return getJdbcTemplate().queryForInt(sql.toString(), params.toArray());
    }
}
