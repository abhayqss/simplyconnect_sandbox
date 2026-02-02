package com.scnsoft.exchange.audit.model.validators;

import com.scnsoft.exchange.audit.model.filters.SyncReportFilter;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Date;

//TODO create common validator for reports with "from" and "to" dates
@Component
public class DataSyncLogReportFilterValidator implements Validator {

    @Override
    public boolean supports(Class<?> paramClass) {
        return SyncReportFilter.class.equals(paramClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Date from = ((SyncReportFilter) obj).getFrom();
        Date to = ((SyncReportFilter) obj).getTo();
        if (from != null && to != null) {
            if(from.after(to)) {
                errors.rejectValue("from", "afterTo", new Object[]{}, null);
            }
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "from", "field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "to", "field.required");
    }
}
