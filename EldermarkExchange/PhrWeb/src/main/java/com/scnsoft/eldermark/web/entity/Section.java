package com.scnsoft.eldermark.web.entity;

import com.scnsoft.eldermark.shared.exception.PhrException;

/**
 * @author phomal
 * Created on 05/24/2017.
 */
public enum Section {
    ALLERGIES("allergies"),
    MEDICATIONS("medications"),
    PROBLEMS("problems"),
    VITAL_SIGNS("vitalSigns"),
    IMMUNIZATIONS("immunizations"),
    PAYERS("payers"),
    DOCUMENTS("documents"),
    NOTES("notes"),
    PROCEDURES("procedures"),
    ENCOUNTERS("encounters"),
    PLAN_OF_CARE("planOfCare"),
    ADVANCED_DIRECTIVES("advancedDirectives"),
    SOCIAL_HISTORY("socialHistory"),
    RESULTS("results"),
    FAMILY_HISTORY("familyHistory"),
    MEDICAL_EQUIPMENT("medicalEquipment");

    private final String name;

    Section(final String name) {
        this.name = name;
    }

    public static Section fromName(String text) {
        for (Section b : Section.values()) {
            if (String.valueOf(b.name).equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new PhrException("Unknown Section (name = " + text + ")");
    }

}
