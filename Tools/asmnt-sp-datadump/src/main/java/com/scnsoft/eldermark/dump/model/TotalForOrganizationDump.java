package com.scnsoft.eldermark.dump.model;

import java.util.List;

public class TotalForOrganizationDump extends Dump {

    private AssessmentsGeneral assessmentsGeneral;
    private List<GAD7PHQ9Scoring> gad7PHQ9ScoringList;
    private List<ComprehensiveDetail> comprehensiveDetailList;
    private SPGeneral spGeneral;
    private List<SPIndividual> spIndividualList;
    private List<SPDetails> spDetailList;

    public AssessmentsGeneral getAssessmentsGeneral() {
        return assessmentsGeneral;
    }

    public void setAssessmentsGeneral(AssessmentsGeneral assessmentsGeneral) {
        this.assessmentsGeneral = assessmentsGeneral;
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

    public SPGeneral getSpGeneral() {
        return spGeneral;
    }

    public void setSpGeneral(SPGeneral spGeneral) {
        this.spGeneral = spGeneral;
    }

    public List<SPIndividual> getSpIndividualList() {
        return spIndividualList;
    }

    public void setSpIndividualList(List<SPIndividual> spIndividualList) {
        this.spIndividualList = spIndividualList;
    }

    public List<SPDetails> getSpDetailList() {
        return spDetailList;
    }

    public void setSpDetailList(List<SPDetails> spDetailList) {
        this.spDetailList = spDetailList;
    }

    @Override
    public DumpType getDumpType() {
        return DumpType.TOTAL_FOR_ORGANIZATION;
    }
}
