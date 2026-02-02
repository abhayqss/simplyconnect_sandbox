package com.scnsoft.eldermark.shared;

import java.util.EnumSet;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DocumentTypeXmlAdapter extends XmlAdapter<String, DocumentType> {

    final static Set<DocumentType> predefinedDocumentTypes = EnumSet.of(DocumentType.CCD, DocumentType.NWHIN,
            DocumentType.FACESHEET);

    @Override
    public DocumentType unmarshal(String docTypeStr) throws Exception {
        if (null == docTypeStr) {
            return null;
        } else if (isDocumentTypePredefined(DocumentType.valueOf(docTypeStr))) {
            // in case CDD/NHWIN/FACESHEET
            return DocumentType.valueOf(docTypeStr);
        } else {
            return DocumentType.CUSTOM;
        }
    }

    @Override
    public String marshal(DocumentType docTypeObj) throws Exception {
        if (null == docTypeObj) {
            return null;
        } else if (!isDocumentTypePredefined(docTypeObj)) {
            return DocumentType.CUSTOM.toString();
        } else {
            return docTypeObj.toString();
        }
    }

    private boolean isDocumentTypePredefined(DocumentType docTypeStr) {
        return predefinedDocumentTypes.contains(docTypeStr);
    }

}
