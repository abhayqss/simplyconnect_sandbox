package com.scnsoft.eldermark.service.document.cda;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.Document;

import java.io.InputStream;

public interface CdaImportService {
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

    Client importXml(InputStream is, Client targetResident, Community targetCommunity, ImportMode importMode) throws Exception;

    Client importXml(InputStream is, Client targetResident, Community targetCommunity, ImportMode importMode, Document exchangeDocument) throws Exception;

}
