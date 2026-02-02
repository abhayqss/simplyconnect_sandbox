package com.scnsoft.eldermark.therap.bean.summary;

import com.scnsoft.eldermark.therap.bean.summary.event.TherapEventsProcessingSummary;

import java.util.Date;
import java.util.List;

public class TherapTotalProcessingSummary extends ProcessingSummary {

    private String fileName;
    private Date processedAt;
    private TherapEventsProcessingSummary eventsProcessingSummary;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
    }

    public TherapEventsProcessingSummary getEventsProcessingSummary() {
        return eventsProcessingSummary;
    }

    public void setEventsProcessingSummary(TherapEventsProcessingSummary eventsProcessingSummary) {
        this.eventsProcessingSummary = eventsProcessingSummary;
    }
//
//    @Override
//    protected boolean shouldSetOkStatus() {
//        return hasOkStatus.apply(eventsProcessingSummary);
//    }
//
//    @Override
//    protected String buildWarnMessage() {
//        return StringUtils.join(filterEmpty(eventsProcessingSummary.getMessage(), null, ""), ", ");
//    }
//
//    private String[] filterEmpty(String... strings) {
//        final List<String> result = new ArrayList<>();
//        for (String string : strings) {
//            if (StringUtils.isNotEmpty(string)) {
//                result.add(string);
//            }
//        }
//        return result.toArray(new String[0]);
//    }
}
