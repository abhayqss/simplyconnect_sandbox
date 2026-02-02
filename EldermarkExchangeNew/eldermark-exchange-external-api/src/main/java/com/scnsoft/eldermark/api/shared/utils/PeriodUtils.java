package com.scnsoft.eldermark.api.shared.utils;

import com.scnsoft.eldermark.api.shared.web.dto.ReportPeriod;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by averazub on 1/6/2017.
 */
public class PeriodUtils {
    public static Pair<Date, Date> getPeriodRange(ReportPeriod period, int page) {
        GregorianCalendar calendarFrom = new GregorianCalendar();
        GregorianCalendar calendarTo = new GregorianCalendar();
        calendarFrom.setTime(new Date());
        calendarTo.setTime(calendarFrom.getTime());
        switch (period) {
            case YEAR:
                calendarFrom.add(Calendar.YEAR, page-1);
                calendarTo.add(Calendar.YEAR, page);
                break;
            case MONTH:
                calendarFrom.add(Calendar.MONTH, page-1);
                calendarTo.add(Calendar.MONTH, page);
                break;
            case WEEK:
                calendarFrom.add(Calendar.DAY_OF_MONTH, (page-1)*7);
                calendarTo.add(Calendar.DAY_OF_MONTH, page*7);
                break;
        }
        return new Pair<>(calendarFrom.getTime(),calendarTo.getTime());
    }
}
