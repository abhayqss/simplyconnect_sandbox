package com.scnsoft.exchange.audit.model.filters;


import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;
import java.util.Date;

public class ReportFilterDto implements ReportFilter {
    @DateTimeFormat(pattern="MM/dd/yyyy")
    protected Date from;

    @DateTimeFormat(pattern="MM/dd/yyyy")
    protected Date to;

    protected Long company;

    protected String state;

    public Long getCompany() {
        return company;
    }

    public void setCompany(Long company) {
        this.company = company;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public <V> V getCriteria(FilterBy filterBy, Class<V> valueClass) {
        switch (filterBy) {
            case COMPANY_ID:
                return valueClass.cast(getCompany());
            case DATE_FROM:
                return valueClass.cast(getFrom());
            case DATE_TO:
                return valueClass.cast(getTo());
            case STATE:
                return valueClass.cast(getState());
            default:
                return null;
        }
    }

    public void fixTimePart() {
        from = AuditLogsReportFilter.getStartOfDay(from);
        to = AuditLogsReportFilter.getEndOfDay(to);
    }

    public static Date getEndOfDay(Date date) {
        if (date == null) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getStartOfDay(Date date) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
