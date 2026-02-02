package com.scnsoft.eldermark.dto.assessment;

import java.util.Set;

public class ClientAssessmentResultServicePlanNeedIdentificationDto {

    private Long assessmentResultId;

    private Set<String> excludedQuestions;

    private Set<String> excludedSections;

    public Long getAssessmentResultId() {
        return assessmentResultId;
    }

    public void setAssessmentResultId(Long assessmentResultId) {
        this.assessmentResultId = assessmentResultId;
    }

    public Set<String> getExcludedQuestions() {
        return excludedQuestions;
    }

    public void setExcludedQuestions(Set<String> excludedQuestions) {
        this.excludedQuestions = excludedQuestions;
    }

    public Set<String> getExcludedSections() {
        return excludedSections;
    }

    public void setExcludedSections(Set<String> excludedSections) {
        this.excludedSections = excludedSections;
    }
}
