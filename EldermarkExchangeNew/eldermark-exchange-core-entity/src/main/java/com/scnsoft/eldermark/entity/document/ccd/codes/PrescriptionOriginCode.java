package com.scnsoft.eldermark.entity.document.ccd.codes;

import com.scnsoft.eldermark.entity.ConceptDescriptor;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;

import java.util.Optional;
import java.util.stream.Stream;

public enum PrescriptionOriginCode implements ConceptDescriptor {
    POC_0("0", "Not Specified"),
    POC_1("1", "Written"),
    POC_2("2", "Telephone"),
    POC_3("3", "Electronic"),
    POC_4("4", "Facsimile"),
    POC_5("5", "Pharmacy");

    private static final CodeSystem codeSystem = CodeSystem.PRESCRIPTION_ORIGIN;
    private final String code;
    private final String displayName;

    PrescriptionOriginCode(String code, String displayName) {
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
        return codeSystem.getOid();
    }

    @Override
    public String getCodeSystemName() {
        return codeSystem.getDisplayName();
    }


    public static Optional<PrescriptionOriginCode> fromCode(String code) {
        return Stream.of(PrescriptionOriginCode.values())
                .filter(c -> c.code.equals(code))
                .findFirst();
    }
}
