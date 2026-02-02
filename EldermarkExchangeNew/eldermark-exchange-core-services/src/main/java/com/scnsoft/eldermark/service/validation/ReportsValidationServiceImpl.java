package com.scnsoft.eldermark.service.validation;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.ReportFilter;
import com.scnsoft.eldermark.exception.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class ReportsValidationServiceImpl implements ReportsValidationService {

    @Override
    public void validateFilter(ReportFilter reportFilter) {
        if (reportFilter.getReportType() != ReportType.IN_TUNE) {
            validateDates(reportFilter.getFromDate(), reportFilter.getToDate());
        }
    }

    private void validateDates(Long dateFrom, Long dateTo) {
        if (dateFrom == null || dateTo == null) {
            throw new ValidationException("Date can not be empty");
        }
        if (dateFrom > dateTo) {
            throw new ValidationException("Date range is invalid: date start is bigger than date end");
        }
    }
}
