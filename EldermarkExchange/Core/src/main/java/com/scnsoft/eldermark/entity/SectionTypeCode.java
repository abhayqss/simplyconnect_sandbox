package com.scnsoft.eldermark.entity;

/**
 * Section level codes
 *
 * @author averazub
 * @author phomal
 * Created on 1/23/2017.
 */
public enum SectionTypeCode implements ConceptDescriptor {
    AGE_OBSERVATION("445518008", "Age At Onset", CodeSystem.SNOMED_CT),
    HEALTH_STATUS_OBSERVATION("11323-3", "Health Status", CodeSystem.LOINC),
    STATUS_OBSERVATION("33999-4", "Status", CodeSystem.LOINC);

    private final String codeOid;
    private final String displayName;
    private final CodeSystem codeSystem;

    SectionTypeCode(String codeOid, String displayName, CodeSystem codeSystem) {
        this.codeOid = codeOid;
        this.displayName = displayName;
        this.codeSystem = codeSystem;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return codeOid;
    }

    public String getCodeSystem() {
        return codeSystem.getOid();
    }

    public String getCodeSystemName() {
        return codeSystem.getDisplayName();
    }

    public static SectionTypeCode getByCode(String code, String codeSystemOid) {
        if ((code == null) || (codeSystemOid == null)) {
            return null;
        }

        for (SectionTypeCode sectionType : SectionTypeCode.values()) {
            if (codeSystemOid.equalsIgnoreCase(sectionType.getCodeSystem()) && code.equalsIgnoreCase(sectionType.getCode())) {
                return sectionType;
            }
        }

        return null;
    }

}
