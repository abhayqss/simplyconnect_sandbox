package com.scnsoft.exchange.audit.model.validators;

import com.scnsoft.exchange.audit.model.filters.ReportFilterDto;
import com.scnsoft.exchange.audit.model.filters.SyncReportFilter;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component
public class SyncReportFilterValidator implements Validator {

    @Override
    public boolean supports(Class<?> paramClass) {
        return SyncReportFilter.class.equals(paramClass) || ReportFilterDto.class.equals(paramClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "from", "field.required");
    }
}