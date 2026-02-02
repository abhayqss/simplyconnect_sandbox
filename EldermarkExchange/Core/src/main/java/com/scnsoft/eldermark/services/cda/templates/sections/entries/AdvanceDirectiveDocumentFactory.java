package com.scnsoft.eldermark.services.cda.templates.sections.entries;

import com.scnsoft.eldermark.entity.AdvanceDirective;
import com.scnsoft.eldermark.entity.AdvanceDirectiveDocument;
import com.scnsoft.eldermark.entity.Resident;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.Reference;
import org.eclipse.mdht.uml.hl7.datatypes.ED;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author phomal
 * Created on 4/24/2018.
 */
@Component
public class AdvanceDirectiveDocumentFactory {

    public static List<AdvanceDirectiveDocument> parseReferenceDocuments(EList<Reference> references, AdvanceDirective advanceDirective, Resident resident) {
        if (CollectionUtils.isEmpty(references) || resident == null) {
            return null;
        }

        final List<AdvanceDirectiveDocument> advanceDirectiveDocuments = new ArrayList<>();
        for (Reference reference : references) {
            if (reference.getExternalDocument() != null && reference.getExternalDocument().getText() != null) {
                final ED ccdDoc = reference.getExternalDocument().getText();
                AdvanceDirectiveDocument advanceDirectiveDocument = new AdvanceDirectiveDocument();
                advanceDirectiveDocument.setDatabase(resident.getDatabase());
                advanceDirectiveDocument.setMediaType(ccdDoc.getMediaType());
                if (ccdDoc.getReference() != null) {
                    advanceDirectiveDocument.setUrl(ccdDoc.getReference().getValue());
                }

                advanceDirectiveDocument.setAdvanceDirective(advanceDirective);
                advanceDirectiveDocuments.add(advanceDirectiveDocument);
            }
        }

        return advanceDirectiveDocuments;
    }

}
