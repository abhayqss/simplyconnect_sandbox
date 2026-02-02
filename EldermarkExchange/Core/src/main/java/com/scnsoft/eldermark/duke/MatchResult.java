package com.scnsoft.eldermark.duke;

import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.shared.carecoordination.PatientDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by knetkachou on 1/19/2017.
 */

public class MatchResult {

    List<CareCoordinationResident> matchedRecords = new ArrayList<CareCoordinationResident>();
    List<CareCoordinationResident> probablyMatchedRecords = new ArrayList<CareCoordinationResident>();
    MatchResultType matchResultType;

    public enum MatchResultType {
        MATCH, MAYBE, NO_MATCH
    }

    public MatchResult() {

    }

    public MatchResult(List<CareCoordinationResident> matchedRecords, List<CareCoordinationResident> probablyMatchedRecords, MatchResultType matchResultType) {
        this.matchedRecords = matchedRecords;
        this.probablyMatchedRecords = probablyMatchedRecords;
        this.matchResultType = matchResultType;
    }

    public List<CareCoordinationResident> getMatchedRecords() {
        return matchedRecords;
    }

    public void setMatchedRecords(List<CareCoordinationResident> matchedRecords) {
        this.matchedRecords = matchedRecords;
    }

    public List<CareCoordinationResident> getProbablyMatchedRecords() {
        return probablyMatchedRecords;
    }

    public void setProbablyMatchedRecords(List<CareCoordinationResident> probablyMatchedRecords) {
        this.probablyMatchedRecords = probablyMatchedRecords;
    }

    public MatchResultType getMatchResultType() {
        return matchResultType;
    }

    public void setMatchResultType(MatchResultType matchResultType) {
        this.matchResultType = matchResultType;
    }
}
