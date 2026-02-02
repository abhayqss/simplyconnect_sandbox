package com.scnsoft.eldermark.beans.reports.model;

import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_1_PREFIX;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_2_PREFIX;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_3_PREFIX;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_4_PREFIX;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_5_PREFIX;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NorCalComprehensiveAssessmentHouseHoldMembers {

    @JsonUnwrapped(prefix = HOUSEHOLD_MEMBER_1_PREFIX)
    private HouseholdMemberAssessmentDto householdMember1;

    @JsonUnwrapped(prefix = HOUSEHOLD_MEMBER_2_PREFIX)
    private HouseholdMemberAssessmentDto householdMember2;

    @JsonUnwrapped(prefix = HOUSEHOLD_MEMBER_3_PREFIX)
    private HouseholdMemberAssessmentDto householdMember3;

    @JsonUnwrapped(prefix = HOUSEHOLD_MEMBER_4_PREFIX)
    private HouseholdMemberAssessmentDto householdMember4;

    @JsonUnwrapped(prefix = HOUSEHOLD_MEMBER_5_PREFIX)
    private HouseholdMemberAssessmentDto householdMember5;


    public HouseholdMemberAssessmentDto getHouseholdMember1() {
        return householdMember1;
    }

    public void setHouseholdMember1(final HouseholdMemberAssessmentDto householdMember1) {
        this.householdMember1 = householdMember1;
    }

    public HouseholdMemberAssessmentDto getHouseholdMember2() {
        return householdMember2;
    }

    public void setHouseholdMember2(final HouseholdMemberAssessmentDto householdMember2) {
        this.householdMember2 = householdMember2;
    }

    public HouseholdMemberAssessmentDto getHouseholdMember3() {
        return householdMember3;
    }

    public void setHouseholdMember3(final HouseholdMemberAssessmentDto householdMember3) {
        this.householdMember3 = householdMember3;
    }

    public HouseholdMemberAssessmentDto getHouseholdMember4() {
        return householdMember4;
    }

    public void setHouseholdMember4(final HouseholdMemberAssessmentDto householdMember4) {
        this.householdMember4 = householdMember4;
    }

    public HouseholdMemberAssessmentDto getHouseholdMember5() {
        return householdMember5;
    }

    public void setHouseholdMember5(final HouseholdMemberAssessmentDto householdMember5) {
        this.householdMember5 = householdMember5;
    }
}
