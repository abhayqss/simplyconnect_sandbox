package com.scnsoft.exchange.audit.dao;

import com.scnsoft.exchange.audit.dao.mappers.MpiReportRowMapper;
import com.scnsoft.exchange.audit.model.MpiReportEntry;
import com.scnsoft.exchange.audit.model.filters.FilterBy;
import com.scnsoft.exchange.audit.model.filters.ReportFilter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MpiReportDaoImpl extends BaseDao implements ReportDao<MpiReportEntry> {

    @Override
    public List<MpiReportEntry> findAll(ReportFilter filter) {
        String state = filter.getCriteria(FilterBy.STATE, String.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT [state], ");
        sql.append("    COUNT(r.id) as 'resident_number', ");
        sql.append("    SUM(CASE WHEN AuditLog.action LIKE '%CCD_GENERATE%' THEN 1 ELSE 0 END) as 'clinical_transactions', ");
        sql.append("    SUM(CASE WHEN AuditLog.action = 'PATIENT_DISCOVERY' AND r.id IS NOT NULL  THEN 1 ELSE 0 END) as 'RLS_queries' ");
        sql.append("FROM [Organization] ");
        sql.append("    LEFT JOIN [Resident] r on [Organization].[id] = r.[facility_id] ");
        sql.append("    LEFT JOIN AuditLog_Residents on r.id = AuditLog_Residents.resident_id ");
        sql.append("    LEFT JOIN AuditLog on AuditLog_Residents.audit_log_id = AuditLog.id ");
        sql.append("    LEFT JOIN [OrganizationAddress] on [Organization].[id] = [OrganizationAddress].[org_id] ");
        sql.append("WHERE ([Organization].[legacy_table] = 'Company' OR [Organization].[legacy_table] IS NULL) ");
        sql.append("    AND ([Organization].[testing_training] = 0 OR [Organization].[testing_training] IS NULL) ");

        List<Object> params = new ArrayList<Object>();

        if(state != null && !"all".equals(state)) {
            if ("null".equals(state)) {
                sql.append("AND [OrganizationAddress].[state] IS NULL ");
            } else {
                sql.append("AND [OrganizationAddress].[state] = ? ");
                params.add(state);
            }
        }

        sql.append("GROUP BY [OrganizationAddress].[state] ");
        sql.append("ORDER BY [OrganizationAddress].[state] ");

        return getJdbcTemplate().query(sql.toString(), params.toArray(), new MpiReportRowMapper());
    }

    @Override
    public List<MpiReportEntry> findAll(int offset, int limit, ReportFilter filter) {
        String state = filter.getCriteria(FilterBy.STATE, String.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ( ");
        sql.append("    SELECT [state], ");
        sql.append("        COUNT(r.id) as 'resident_number', ");
        sql.append("        SUM(CASE WHEN AuditLog.action LIKE '%CCD_GENERATE%' THEN 1 ELSE 0 END) as 'clinical_transactions', ");
        sql.append("        SUM(CASE WHEN AuditLog.action = 'PATIENT_DISCOVERY' AND r.id IS NOT NULL  THEN 1 ELSE 0 END) as 'RLS_queries', ");
        sql.append("        ROW_NUMBER() OVER (ORDER BY [state] ASC) AS RowNum ");
        sql.append("    FROM [Organization] ");
        sql.append("        LEFT JOIN [Resident] r on [Organization].[id] = r.[facility_id] ");
        sql.append("        LEFT JOIN AuditLog_Residents on r.id = AuditLog_Residents.resident_id ");
        sql.append("        LEFT JOIN AuditLog on AuditLog_Residents.audit_log_id = AuditLog.id ");
        sql.append("        LEFT JOIN [OrganizationAddress] on [Organization].[id] = [OrganizationAddress].[org_id] ");
        sql.append("    WHERE ([Organization].[legacy_table] = 'Company' OR [Organization].[legacy_table] IS NULL) ");
        sql.append("        AND ([Organization].[testing_training] = 0 OR [Organization].[testing_training] IS NULL) ");

        List<Object> params = new ArrayList<Object>();


        if(state != null && !"all".equals(state)) {
            if ("null".equals(state)) {
                sql.append("AND [OrganizationAddress].[state] IS NULL ");
            } else {
                sql.append("AND [OrganizationAddress].[state] = ? ");
                params.add(state);
            }
        }

        sql.append("GROUP BY [OrganizationAddress].[state] ");

        sql.append(") AS enumeratedTable WHERE (enumeratedTable.RowNum BETWEEN ? AND ?)");
        params.add(offset + 1);
        params.add(offset + limit);

        return getJdbcTemplate().query(sql.toString(), params.toArray(), new MpiReportRowMapper());
    }

    @Override
    public int count(ReportFilter filter) {
        String state = filter.getCriteria(FilterBy.STATE, String.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(DISTINCT [state]) ");
        sql.append("FROM [Organization] ");
        sql.append("    LEFT JOIN [OrganizationAddress] on [Organization].[id] = [OrganizationAddress].[org_id] ");
        sql.append("WHERE ([Organization].[legacy_table] = 'Company' OR [Organization].[legacy_table] IS NULL) ");
        sql.append("    AND ([Organization].[testing_training] = 0 OR [Organization].[testing_training] IS NULL) ");

        List<Object> params = new ArrayList<Object>();

        if(state != null && !"all".equals(state)) {
            if ("null".equals(state)) {
                sql.append("AND [OrganizationAddress].[state] IS NULL ");
            } else {
                sql.append("AND [OrganizationAddress].[state] = ? ");
                params.add(state);
            }
        }

        return getJdbcTemplate().queryForInt(sql.toString(), params.toArray());
    }
}
