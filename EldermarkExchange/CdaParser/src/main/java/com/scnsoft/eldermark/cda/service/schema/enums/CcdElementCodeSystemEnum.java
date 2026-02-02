package com.scnsoft.eldermark.cda.service.schema.enums;

import com.scnsoft.eldermark.cda.service.schema.DocumentType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * enum which maps CCD element code system to document type
 */
public enum CcdElementCodeSystemEnum {

    PROBLEM_ACT_STATUS_CCDA_R1_1_CCD_V1(ValueSetEnum.PROBLEM_ACT_CODE_SYSTEM, new HashSet<DocumentType>(Arrays.asList(DocumentType.CCDA_R1_1_CCD_V1)));

    CcdElementCodeSystemEnum(final ValueSetEnum valueSetEnum, final Set<DocumentType> documentTypes) {
        this.valueSetEnum = valueSetEnum;
        this.documentTypes = documentTypes;
    }

    final ValueSetEnum valueSetEnum;
    final Set<DocumentType> documentTypes;

    public Set<DocumentType> getDocumentTypes() {
        return documentTypes;
    }

    public ValueSetEnum getValueSetEnum() {
        return valueSetEnum;
    }
}
