package com.scnsoft.exchange.audit.model.filters;

import java.util.Calendar;
import java.util.Date;

public class AuditLogsReportFilter extends ReportFilterDto  {

    public void setFromAsMonthAgo() {
        Calendar monthAgo = Calendar.getInstance();
        monthAgo.add(Calendar.MONTH, -1);

        from = monthAgo.getTime();
    }

    public void setFromAsFirstDayOfPrevMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        from = calendar.getTime();
    }

    public void setToAsLastDayOfPrevMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        to = calendar.getTime();
    }

    public void setToAsToday() {
        to = new Date();
    }
}
