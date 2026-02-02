package com.scnsoft.eldermark.exchange.fk;

import com.scnsoft.eldermark.exchange.model.IcdCodeSet;

public class ProblemForeignKeys implements ResidentIdAware {
    private Long residentId;

    private IcdCodeSet icdCodeSet;

    private Long problemValueCodeId;

    private Long recordedBy;

    public Long getProblemValueCodeId() {
        return problemValueCodeId;
    }

    public void setProblemValueCodeId(Long problemValueCodeId) {
        this.problemValueCodeId = problemValueCodeId;
    }

    public IcdCodeSet getIcdCodeSet() {
        return icdCodeSet;
    }

    public void setIcdCodeSet(IcdCodeSet icdCodeSet) {
        this.icdCodeSet = icdCodeSet;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Long getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(Long recordedBy) {
        this.recordedBy = recordedBy;
    }
}
