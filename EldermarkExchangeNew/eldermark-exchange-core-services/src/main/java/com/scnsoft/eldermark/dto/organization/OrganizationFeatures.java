package com.scnsoft.eldermark.dto.organization;

public interface OrganizationFeatures {
    Boolean getIsChatEnabled();
    Boolean getIsVideoEnabled();
    Boolean getIsSignatureEnabled();
    Boolean getAreComprehensiveAssessmentsEnabled();
    Boolean getIsPaperlessHealthcareEnabled();
    Boolean getAreAppointmentsEnabled();

    OrganizationFeatures DEFAULT = new OrganizationFeatures() {
        @Override
        public Boolean getIsChatEnabled() {
            return true;
        }

        @Override
        public Boolean getIsVideoEnabled() {
            return true;
        }

        @Override
        public Boolean getIsSignatureEnabled() {
            return false;
        }

        @Override
        public Boolean getAreComprehensiveAssessmentsEnabled() {
            return false;
        }

        @Override
        public Boolean getIsPaperlessHealthcareEnabled() {
            return false;
        }

        @Override
        public Boolean getAreAppointmentsEnabled() {
            return false;
        }
    };
}
