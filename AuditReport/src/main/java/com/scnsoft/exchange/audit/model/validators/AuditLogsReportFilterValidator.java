package com.scnsoft.exchange.audit.model.validators;


import com.scnsoft.exchange.audit.model.filters.AuditLogsReportFilter;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Date;

public class AuditLogsReportFilterValidator implements Validator {

    @Override
    public boolean supports(Class<?> paramClass) {
        return AuditLogsReportFilter.class.equals(paramClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Date from = ((AuditLogsReportFilter) obj).getFrom();
        Date to = ((AuditLogsReportFilter) obj).getTo();
        if (from != null && to != null) {
            if(from.after(to)) {
                errors.rejectValue("from", "afterTo", new Object[]{}, null);
            }
        }
    }
}