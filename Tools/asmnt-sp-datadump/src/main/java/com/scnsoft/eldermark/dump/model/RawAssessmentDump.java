package com.scnsoft.eldermark.dump.model;

import com.scnsoft.eldermark.dump.model.assessment.AssessmentStructure;

import java.util.List;

public abstract class RawAssessmentDump<T extends RawAssessmentDumpEntry> extends Dump {

    private AssessmentStructure assessmentStructure;
    private List<T> entries;

    public AssessmentStructure getAssessmentStructure() {
        return assessmentStructure;
    }

    public void setAssessmentStructure(AssessmentStructure assessmentStructure) {
        this.assessmentStructure = assessmentStructure;
    }

    public List<T> getEntries() {
        return entries;
    }

    public void setEntries(List<T> entries) {
        this.entries = entries;
    }
}
