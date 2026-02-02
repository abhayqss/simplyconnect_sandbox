package com.scnsoft.eldermark.entity.note;

import com.scnsoft.eldermark.beans.projection.IdAware;

import java.time.Instant;

public interface NoteDashboardItem extends IdAware {
    String getSubjective();
    String getObjective();
    String getAssessment();
    String getPlan();
    NoteType getType();
    String getSubTypeCode();
    String getSubTypeDescription();
    Instant getLastModifiedDate();
}
