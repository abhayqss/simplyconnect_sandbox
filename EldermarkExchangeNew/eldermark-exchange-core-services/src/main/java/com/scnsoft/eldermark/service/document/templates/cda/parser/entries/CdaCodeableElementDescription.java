package com.scnsoft.eldermark.service.document.templates.cda.parser.entries;


import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.cda.service.schema.CdaDocumentType;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * This enum represents cda element description for specific document types. Description might be
 * different for different document types (for example, another value set)
 *
 * If element should be within specific code system, then codeSystem property is set
 * If element should be within specific value set, then valueSet property is set.
 * Description includes code s
 */
public enum CdaCodeableElementDescription {

    PROBLEM_ACT_STATUS_CCDA_R1_1_CCD_V1(
            EnumSet.of(CdaDocumentType.CCDA_R1_1_CCD_V1),
            CodeSystem.HL7_ACT_STATUS,
            ValueSetEnum.PROBLEM_ACT_STATUS_CODE)
    ;


    final Set<CdaDocumentType> documentTypes;
    final CodeSystem codeSystem; //If belongs to specific code system
    final ValueSetEnum valueSetEnum; //if belongs to specific enum

    CdaCodeableElementDescription(Set<CdaDocumentType> documentTypes, CodeSystem codeSystem, ValueSetEnum valueSetEnum) {
        this.documentTypes = documentTypes;
        this.codeSystem = codeSystem;
        this.valueSetEnum = valueSetEnum;
    }

    public Set<CdaDocumentType> getDocumentTypes() {
        return documentTypes;
    }

    public Optional<CodeSystem> getCodeSystem() {
        return Optional.ofNullable(codeSystem);
    }

    public CodeSystem getRequiredCodeSystem() {
        return getCodeSystem().orElseThrow();
    }

    public Optional<ValueSetEnum> getValueSetEnum() {
        return Optional.ofNullable(valueSetEnum);
    }

    public ValueSetEnum getRequiredValueSetEnum() {
        return getValueSetEnum().orElseThrow();
    }
}
