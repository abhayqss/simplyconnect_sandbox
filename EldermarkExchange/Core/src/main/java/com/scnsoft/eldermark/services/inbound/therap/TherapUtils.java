package com.scnsoft.eldermark.services.inbound.therap;

import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapTotalProcessingSummary;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class TherapUtils {

    private TherapUtils() {
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("MMddyyyy_HHmmss_");

    public static String buildProcessedFileName(TherapTotalProcessingSummary summary) {
        return DATE_TIME_FORMATTER.print(summary.getProcessedAt().getTime()) + summary.getFileName();
    }
}
