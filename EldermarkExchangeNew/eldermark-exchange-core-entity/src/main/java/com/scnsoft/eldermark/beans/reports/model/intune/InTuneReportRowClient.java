package com.scnsoft.eldermark.beans.reports.model.intune;

import java.time.Instant;
import java.util.List;

public class InTuneReportRowClient {
    private String clientName;
    private List<Instant> analyzedAssessmentDates;
    private List<InTuneReportRowQuestion> questions;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public List<Instant> getAnalyzedAssessmentDates() {
        return analyzedAssessmentDates;
    }

    public void setAnalyzedAssessmentDates(List<Instant> analyzedAssessmentDates) {
        this.analyzedAssessmentDates = analyzedAssessmentDates;
    }

    public List<InTuneReportRowQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<InTuneReportRowQuestion> questions) {
        this.questions = questions;
    }
}

