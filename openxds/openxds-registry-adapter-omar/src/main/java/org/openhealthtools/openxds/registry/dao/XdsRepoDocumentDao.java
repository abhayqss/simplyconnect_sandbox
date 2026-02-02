package org.openhealthtools.openxds.registry.dao;

import org.openhealthtools.openxds.registry.Document;

public interface XdsRepoDocumentDao {
    Document findOne(Long patientRepoId);
    Document save(Document entity);
}
