package com.scnsoft.eldermark.cda.service.schema.enums;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.scnsoft.eldermark.cda.service.schema.DocumentType;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public enum CcdElementEnum {

    PROBLEM_ACT_STATUS(
            ImmutableSet.of(CcdElementCodeSystemEnum.PROBLEM_ACT_STATUS_CCDA_R1_1_CCD_V1)
    );

    private final Set<CcdElementCodeSystemEnum> ccdElementCodeSystemEnumSet;

    CcdElementEnum(Set<CcdElementCodeSystemEnum> ccdElementCodeSystemEnumSet) {
        this.ccdElementCodeSystemEnumSet = ccdElementCodeSystemEnumSet;
    }

    public Optional<CcdElementCodeSystemEnum> findCodeSystemByDocumentType(final DocumentType documentTypeParam) {
        for (CcdElementCodeSystemEnum ccdElementCodeSystemEnum: ccdElementCodeSystemEnumSet) {
            for (DocumentType documentType : ccdElementCodeSystemEnum.getDocumentTypes()) {
                if (documentType.equals(documentTypeParam)) {
                    return Optional.of(ccdElementCodeSystemEnum);
                }
            }
        }
        return Optional.absent();
    }

    public Optional<CcdElementCodeSystemEnum> findCodeSystemByDocumentTypes(final Collection<DocumentType> documentTypesParam) {
        for (DocumentType documentType: documentTypesParam) {
            final Optional<CcdElementCodeSystemEnum> ccdElementCodeSystemEnumOptional = this.findCodeSystemByDocumentType(documentType);
            if (ccdElementCodeSystemEnumOptional.isPresent()) {
                return ccdElementCodeSystemEnumOptional;
            }
        }
        return Optional.absent();
    }

}
