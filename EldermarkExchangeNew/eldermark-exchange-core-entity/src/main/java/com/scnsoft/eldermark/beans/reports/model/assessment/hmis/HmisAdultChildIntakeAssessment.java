package com.scnsoft.eldermark.beans.reports.model.assessment.hmis;

import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.CASE_MANAGER;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_1_PREFIX;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_2_PREFIX;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_3_PREFIX;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_4_PREFIX;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_5_PREFIX;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_PREFIX;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HmisAdultChildIntakeAssessment {

    @JsonUnwrapped(prefix = FAMILY_MEMBER_PREFIX)
    private FamilyMember familyMember;

    @JsonUnwrapped(prefix = FAMILY_MEMBER_1_PREFIX)
    private FamilyMember familyMember1;

    @JsonUnwrapped(prefix = FAMILY_MEMBER_2_PREFIX)
    private FamilyMember familyMember2;

    @JsonUnwrapped(prefix = FAMILY_MEMBER_3_PREFIX)
    private FamilyMember familyMember3;

    @JsonUnwrapped(prefix = FAMILY_MEMBER_4_PREFIX)
    private FamilyMember familyMember4;

    @JsonUnwrapped(prefix = FAMILY_MEMBER_5_PREFIX)
    private FamilyMember familyMember5;

    @JsonProperty(CASE_MANAGER)
    private String caseManager;

    public FamilyMember getFamilyMember() {
        return familyMember;
    }

    public FamilyMember getFamilyMember1() {
        return familyMember1;
    }

    public FamilyMember getFamilyMember2() {
        return familyMember2;
    }

    public FamilyMember getFamilyMember3() {
        return familyMember3;
    }

    public FamilyMember getFamilyMember4() {
        return familyMember4;
    }

    public FamilyMember getFamilyMember5() {
        return familyMember5;
    }

    public String getCaseManager() {
        return caseManager;
    }

    public void setFamilyMember(final FamilyMember familyMember) {
        this.familyMember = familyMember;
    }

    public void setFamilyMember1(final FamilyMember familyMember1) {
        this.familyMember1 = familyMember1;
    }

    public void setFamilyMember2(final FamilyMember familyMember2) {
        this.familyMember2 = familyMember2;
    }

    public void setFamilyMember3(final FamilyMember familyMember3) {
        this.familyMember3 = familyMember3;
    }

    public void setFamilyMember4(final FamilyMember familyMember4) {
        this.familyMember4 = familyMember4;
    }

    public void setFamilyMember5(final FamilyMember familyMember5) {
        this.familyMember5 = familyMember5;
    }

    public void setCaseManager(final String caseManager) {
        this.caseManager = caseManager;
    }
}
