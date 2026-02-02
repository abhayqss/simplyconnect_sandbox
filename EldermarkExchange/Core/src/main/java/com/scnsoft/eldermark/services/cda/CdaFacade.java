package com.scnsoft.eldermark.services.cda;

import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.cda.service.schema.DocumentType;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author phomal
 * Created on 4/28/2018.
 */
public interface CdaFacade {
    enum ImportMode {
        /**
         * Overwrite health of existing resident data during import. If the resident doesn't exist, create new resident.
         */
        OVERWRITE,
        /**
         * Append health data to existing resident during import. If the resident doesn't exist, create new resident.
         */
        APPEND,
        /**
         * Always create new resident record and append health data during import.
         */
        CREATE
    }

    Resident importXml(InputStream is, Resident targetResident, Organization targetOrganization, ImportMode importMode) throws Exception;
    Resident importXml(InputStream is, Resident targetResident, Organization targetOrganization, ImportMode importMode, Document exchangeDocument) throws Exception;
    Resident importXmlAsNewResident(InputStream is, Organization targetOrganization) throws Exception;
    void exportXml(OutputStream os, Long sourceResidentId, DocumentType docType, boolean aggregated);
    void exportXml(OutputStream os, Long sourceResidentId, DocumentType docType, List<Long> residentIds);
    void exportHtml(OutputStream os, Long sourceResidentId, DocumentType docType, boolean aggregated);

    String getCdaHtmlViewForDocument(Long documentId);

}
