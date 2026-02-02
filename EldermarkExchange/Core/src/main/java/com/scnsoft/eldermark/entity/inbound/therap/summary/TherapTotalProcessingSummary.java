package com.scnsoft.eldermark.entity.inbound.therap.summary;

import com.scnsoft.eldermark.entity.inbound.summary.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.event.TherapEventsProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.idf.TherapIdfsProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.programenrollment.TherapProgramEnrollmentsProcessingSummary;
import com.scnsoft.eldermark.util.ExchangeStringUtils;

import java.util.Date;

public class TherapTotalProcessingSummary extends ProcessingSummary {

    private String fileName;
    private Date processedAt;
    private TherapProgramEnrollmentsProcessingSummary enrollmentsProcessingSummary;
    private TherapIdfsProcessingSummary idfsProcessingSummary;
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

    public TherapProgramEnrollmentsProcessingSummary getEnrollmentsProcessingSummary() {
        return enrollmentsProcessingSummary;
    }

    public void setEnrollmentsProcessingSummary(TherapProgramEnrollmentsProcessingSummary enrollmentsProcessingSummary) {
        this.enrollmentsProcessingSummary = enrollmentsProcessingSummary;
    }

    public TherapIdfsProcessingSummary getIdfsProcessingSummary() {
        return idfsProcessingSummary;
    }

    public void setIdfsProcessingSummary(TherapIdfsProcessingSummary idfsProcessingSummary) {
        this.idfsProcessingSummary = idfsProcessingSummary;
    }

    public TherapEventsProcessingSummary getEventsProcessingSummary() {
        return eventsProcessingSummary;
    }

    public void setEventsProcessingSummary(TherapEventsProcessingSummary eventsProcessingSummary) {
        this.eventsProcessingSummary = eventsProcessingSummary;
    }

    @Override
    protected boolean shouldSetOkStatus() {
        return hasOkStatus.apply(eventsProcessingSummary) && hasOkStatus.apply(idfsProcessingSummary) && hasOkStatus.apply(enrollmentsProcessingSummary);
    }

    @Override
    protected String buildWarnMessage() {
        return ExchangeStringUtils.joinNotEmpty("; ",
                enrollmentsProcessingSummary.getMessage(),
                idfsProcessingSummary.getMessage(),
                eventsProcessingSummary.getMessage()
        );
    }
}
