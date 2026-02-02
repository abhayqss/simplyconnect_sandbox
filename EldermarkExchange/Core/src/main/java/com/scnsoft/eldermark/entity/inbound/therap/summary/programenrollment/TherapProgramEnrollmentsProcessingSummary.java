package com.scnsoft.eldermark.entity.inbound.therap.summary.programenrollment;

import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapEntitiesProcessingSummary;

public class TherapProgramEnrollmentsProcessingSummary extends TherapEntitiesProcessingSummary<TherapProgramEnrollmentFileProcessingSummary> {

    @Override
    protected String getEntityType() {
        return "PROGRAM_ENROLLMENT";
    }
}
