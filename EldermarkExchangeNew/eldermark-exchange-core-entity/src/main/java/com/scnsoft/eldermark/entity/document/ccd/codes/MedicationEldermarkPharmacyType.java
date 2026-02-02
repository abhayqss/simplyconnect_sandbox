package com.scnsoft.eldermark.entity.document.ccd.codes;

import com.scnsoft.eldermark.entity.ConceptDescriptor;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;

public enum MedicationEldermarkPharmacyType implements ConceptDescriptor {
    PHARMACY("1", "Pharmacy"),
    DISPENSING_PHARMACY("2", "Dispensing pharmacy");

    private final String code;
    private final String displayName;

    MedicationEldermarkPharmacyType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String getCodeSystem() {
        return CodeSystem.MEDICATION_PHARMACY_TYPE.getOid();
    }

    @Override
    public String getCodeSystemName() {
        return CodeSystem.MEDICATION_PHARMACY_TYPE.getDisplayName();
    }
}
