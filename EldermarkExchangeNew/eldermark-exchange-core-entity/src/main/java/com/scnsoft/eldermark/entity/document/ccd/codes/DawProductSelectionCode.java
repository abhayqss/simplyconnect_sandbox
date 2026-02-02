package com.scnsoft.eldermark.entity.document.ccd.codes;

import com.scnsoft.eldermark.entity.ConceptDescriptor;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;

import java.util.Optional;
import java.util.stream.Stream;

public enum DawProductSelectionCode implements ConceptDescriptor {
    DAW_0("0", "No Product Selection Indicated (may also have missing values)"),
    DAW_1("1", "Substitution Not Allowed by Prescriber"),
    DAW_2("2", "Substitution Allowed - Patient Requested That Brand Product Be Dispensed"),
    DAW_3("3", "Substitution Allowed - Pharmacist Selected Product Dispensed"),
    DAW_4("4", "Substitution Allowed - Generic Drug Not in Stock"),
    DAW_5("5", "Substitution Allowed - Brand Drug Dispensed as Generic"),
    DAW_6("6", "Override"),
    DAW_7("7", "Substitution Not Allowed - Brand Drug Mandated by Law"),
    DAW_8("8", "Substitution Allowed - Generic Drug Not Available in Marketplace"),
    DAW_9("9", "Other");

    private static final CodeSystem codeSystem = CodeSystem.DAW_PRODUCT_SELECTION;
    private final String code;
    private final String displayName;

    DawProductSelectionCode(String code, String displayName) {
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


    public static Optional<DawProductSelectionCode> fromCode(String code) {
        return Stream.of(DawProductSelectionCode.values())
                .filter(c -> c.code.equals(code))
                .findFirst();
    }
}
