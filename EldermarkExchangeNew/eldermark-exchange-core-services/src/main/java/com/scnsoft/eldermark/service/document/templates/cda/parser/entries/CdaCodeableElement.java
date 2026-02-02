package com.scnsoft.eldermark.service.document.templates.cda.parser.entries;

import com.google.common.collect.ImmutableSet;
import com.scnsoft.eldermark.cda.service.schema.CdaDocumentType;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * This enum represents cda codeable element. Element contains specific descriptions for specific cda document types
 *
 * @see CdaCodeableElementDescription
 */
public enum CdaCodeableElement {

    PROBLEM_ACT_STATUS(
            ImmutableSet.of(CdaCodeableElementDescription.PROBLEM_ACT_STATUS_CCDA_R1_1_CCD_V1)
    );

    private final Set<CdaCodeableElementDescription> elementDescriptions;

    CdaCodeableElement(Set<CdaCodeableElementDescription> possibleDescriptions) {
        this.elementDescriptions = possibleDescriptions;
    }


    public Optional<CdaCodeableElementDescription> findDescriptionByDocumentType(final CdaDocumentType cdaDocumentType) {
        return elementDescriptions.stream()
                .filter(description -> description.getDocumentTypes().contains(cdaDocumentType))
                .findFirst();

    }

    public Optional<CdaCodeableElementDescription> findDescriptionByDocumentTypes(Collection<CdaDocumentType> documentTypesParam) {

        return documentTypesParam.stream()
                .map(this::findDescriptionByDocumentType)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}
