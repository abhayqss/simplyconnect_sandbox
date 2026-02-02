package com.scnsoft.eldermark.dump.model;

import java.time.LocalDateTime;

public class RawComprehensiveAssessmentDumpEntry extends RawAssessmentDumpEntry {

    private LocalDateTime dateCompleted;

    public LocalDateTime getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(LocalDateTime dateCompleted) {
        this.dateCompleted = dateCompleted;
    }
}
