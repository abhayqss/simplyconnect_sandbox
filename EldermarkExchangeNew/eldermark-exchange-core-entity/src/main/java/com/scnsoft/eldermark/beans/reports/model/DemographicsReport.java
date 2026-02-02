package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class DemographicsReport extends Report {

    private List<AssessmentsGeneral> assessmentsGeneralList;
    private List<GAD7PHQ9Scoring> gad7PHQ9ScoringList;
    private List<ComprehensiveDetail> comprehensiveDetailList;
    private List<SPGeneral> spGeneralList;
    private List<SPIndividualTab> spIndividuals;
    private List<SPDetails> spDetailList;

    public List<AssessmentsGeneral> getAssessmentsGeneralList() {
        return assessmentsGeneralList;
    }

    public void setAssessmentsGeneralList(List<AssessmentsGeneral> assessmentsGeneralList) {
        this.assessmentsGeneralList = assessmentsGeneralList;
    }

    public List<GAD7PHQ9Scoring> getGad7PHQ9ScoringList() {
        return gad7PHQ9ScoringList;
    }

    public void setGad7PHQ9ScoringList(List<GAD7PHQ9Scoring> gad7PHQ9ScoringList) {
        this.gad7PHQ9ScoringList = gad7PHQ9ScoringList;
    }

    public List<ComprehensiveDetail> getComprehensiveDetailList() {
        return comprehensiveDetailList;
    }

    public void setComprehensiveDetailList(List<ComprehensiveDetail> comprehensiveDetailList) {
        this.comprehensiveDetailList = comprehensiveDetailList;
    }

    public List<SPGeneral> getSpGeneralList() {
        return spGeneralList;
    }

    public void setSpGeneralList(List<SPGeneral> spGeneralList) {
        this.spGeneralList = spGeneralList;
    }

    public List<SPDetails> getSpDetailList() {
        return spDetailList;
    }

    public void setSpDetailList(List<SPDetails> spDetailList) {
        this.spDetailList = spDetailList;
    }

    public List<SPIndividualTab> getSpIndividuals() {
        return spIndividuals;
    }

    public void setSpIndividuals(List<SPIndividualTab> spIndividuals) {
        this.spIndividuals = spIndividuals;
    }
}
