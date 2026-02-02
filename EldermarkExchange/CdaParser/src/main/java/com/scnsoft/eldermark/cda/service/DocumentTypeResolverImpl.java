package com.scnsoft.eldermark.cda.service;

import com.google.common.base.Joiner;
import com.scnsoft.eldermark.cda.service.schema.DocumentType;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.ClinicalDocument;
import org.eclipse.mdht.uml.cda.util.CDAUtil;
import org.eclipse.mdht.uml.hl7.datatypes.II;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author phomal
 * Created on 4/9/2018.
 */
public class DocumentTypeResolverImpl implements DocumentTypeResolver {

    private static final Logger logger = LoggerFactory.getLogger(DocumentTypeResolverImpl.class);
    public static final String US_REALM_HEADER = "2.16.840.1.113883.10.20.22.1.1";

    @Override
    public Set<DocumentType> resolve(InputStream document) {
        final ClinicalDocument doc;
        try {
            doc = CDAUtil.load(document);
            return resolve(doc);
        } catch (Exception e) {
            logger.error("Can not resolve document type: failure to load the document.");
            e.printStackTrace();
        }
        return Collections.emptySet();
    }

    @Override
    public Set<DocumentType> resolve(ClinicalDocument doc) {
        final II id = doc.getId();
        final String docId;
        if (id != null) {
            docId = "root=\"" + id.getRoot() + "\" extension=\"" + id.getExtension() + "\"";
        } else {
            docId = "<null>";
        }
        logger.info("Resolving document type of {}.", docId);

        Set<DocumentType> types = new HashSet<>();
        final EList<II> templateIds = doc.getTemplateIds();
        for (II templateId : templateIds) {
            if (US_REALM_HEADER.equals(templateId.getRoot())) {
                continue;
            }
            final DocumentType type = DocumentType.from(templateId.getRoot(), templateId.getExtension());
            types.add(type);
        }

        final String typesAsString = Joiner.on(" + ").join(types);
        logger.info("Identified document {} as {}.", docId, typesAsString);

        return types;
    }

}
