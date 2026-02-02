package com.scnsoft.eldermark.cda.service;

import com.scnsoft.eldermark.cda.service.schema.DocumentType;
import org.eclipse.mdht.uml.cda.ClinicalDocument;

import java.io.InputStream;
import java.util.Set;

public interface DocumentTypeResolver {
    Set<DocumentType> resolve(InputStream document);
    Set<DocumentType> resolve(ClinicalDocument doc);
}
