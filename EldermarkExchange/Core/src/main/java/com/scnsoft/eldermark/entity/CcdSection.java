package com.scnsoft.eldermark.entity;

import com.google.common.base.Optional;

/**
 * Sections available in CCD Details on web.
 */
public enum CcdSection {
    ALLERGIES("allergies"),
    MEDICATIONS("medications"),
    PROBLEMS("problems"),
    PROCEDURES("procedures"),
    RESULTS("results"),
    ENCOUNTERS("encounters"),
    ADVANCE_DIRECTIVES("advanceDirectives"),
    FAMILY_HISTORY("familyHistory"),
    VITAL_SIGNS("vitalSigns"),
    IMMUNIZATIONS("immunizations"),
    PAYER_PROVIDERS("payerProviders"),
    MEDICAL_EQUIPMENT("medicalEquipment"),
    SOCIAL_HISTORY("socialHistory"),
    PLAN_OF_CARE("planOfCare");

    private final String name;

    CcdSection(String name) {
        this.name = name;
    }

    public static Optional<CcdSection> loadByName(String sectionName) {
        for (CcdSection ccdSection: CcdSection.values()) {
            if (ccdSection.name.equals(sectionName)) {
                return Optional.of(ccdSection);
            }
        }
        return Optional.absent();
    }

    public String getName() {
        return name;
    }
}
