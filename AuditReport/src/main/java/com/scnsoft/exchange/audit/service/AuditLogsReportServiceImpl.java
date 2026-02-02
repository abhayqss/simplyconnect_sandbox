package com.scnsoft.exchange.audit.service;

import com.scnsoft.exchange.audit.dao.AuditLogsDaoImpl;
import com.scnsoft.exchange.audit.model.LogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "auditLogsReportService")
public class AuditLogsReportServiceImpl extends BaseReportService<LogDto> implements ReportService<LogDto> {

    @Autowired
    public AuditLogsReportServiceImpl(AuditLogsDaoImpl auditDao) {
        super(auditDao);
    }
}
