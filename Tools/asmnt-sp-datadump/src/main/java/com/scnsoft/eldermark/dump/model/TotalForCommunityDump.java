package com.scnsoft.eldermark.dump.model;

import java.util.List;

public class TotalForCommunityDump extends Dump {


    private List<CommunityAssessmentsGeneral> communityAssessmentsGeneralList;
    private List<GAD7PHQ9Scoring> gad7PHQ9ScoringList;
    private List<ComprehensiveDetail> comprehensiveDetailList;
    private List<CommunitySPGeneral> communitySPGeneralList;
    private List<SPIndividual> spIndividualList;
    private List<SPDetails> spDetailList;
    private List<AssessedClientInsuranceInfo> assessedClientInsuranceInfoList;

    public List<CommunityAssessmentsGeneral> getCommunityAssessmentsGeneralList() {
        return communityAssessmentsGeneralList;
    }

    public void setCommunityAssessmentsGeneralList(List<CommunityAssessmentsGeneral> communityAssessmentsGeneralList) {
        this.communityAssessmentsGeneralList = communityAssessmentsGeneralList;
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

    public List<CommunitySPGeneral> getCommunitySPGeneralList() {
        return communitySPGeneralList;
    }

    public void setCommunitySPGeneralList(List<CommunitySPGeneral> communitySPGeneralList) {
        this.communitySPGeneralList = communitySPGeneralList;
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

    public List<AssessedClientInsuranceInfo> getAssessedClientInsuranceInfoList() {
        return assessedClientInsuranceInfoList;
    }

    public void setAssessedClientInsuranceInfoList(List<AssessedClientInsuranceInfo> assessedClientInsuranceInfoList) {
        this.assessedClientInsuranceInfoList = assessedClientInsuranceInfoList;
    }

    @Override
    public DumpType getDumpType() {
        return DumpType.TOTAL_FOR_COMMUNITY;
    }



}
