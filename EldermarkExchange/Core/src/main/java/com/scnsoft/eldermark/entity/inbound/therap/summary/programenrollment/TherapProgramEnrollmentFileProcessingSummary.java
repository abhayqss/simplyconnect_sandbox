package com.scnsoft.eldermark.entity.inbound.therap.summary.programenrollment;

import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapEntityFileProcessingSummary;

public class TherapProgramEnrollmentFileProcessingSummary extends TherapEntityFileProcessingSummary<TherapProgramEnrollmentRecordProcessingSummary> {

    @Override
    protected String getEntityType() {
        return "PROGRAM_ENROLLMENT";
    }

}
