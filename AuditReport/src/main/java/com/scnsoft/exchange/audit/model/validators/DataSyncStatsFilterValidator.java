package com.scnsoft.exchange.audit.model.validators;


import com.scnsoft.exchange.audit.model.filters.SyncStatsReportFilter;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Date;

@Component
public class DataSyncStatsFilterValidator implements Validator {

    @Override
    public boolean supports(Class<?> paramClass) {
        return SyncStatsReportFilter.class.equals(paramClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Date from = ((SyncStatsReportFilter) obj).getFrom();
        Date to = ((SyncStatsReportFilter) obj).getTo();
        if (from != null && to != null) {
            if(from.after(to)) {
                errors.rejectValue("from", "afterTo", new Object[]{}, null);
            }
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "from", "field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "to", "field.required");
    }
}