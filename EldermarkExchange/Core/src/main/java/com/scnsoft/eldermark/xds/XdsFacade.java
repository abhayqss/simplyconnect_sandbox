package com.scnsoft.eldermark.xds;

import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.services.SaveDocumentCallback;
import com.scnsoft.eldermark.services.beans.DocumentMetadata;

import java.io.IOException;

/**
 * Created by averazub on 9/27/2016.
 */
public interface XdsFacade {

    Document saveDocument(DocumentMetadata metadata, Long residentId, String uuid, String uniqueId , String hash, SaveDocumentCallback callback);

    /**
     * parse CDA document and save it
     * @return result of XdsFacade.saveDocument
     * @see com.scnsoft.eldermark.xds.XdsFacade#saveDocument(DocumentMetadata, Long, String, String, String, SaveDocumentCallback)
     */
    Long saveAndParseCDA(XdsDocument xdsDocument, DocumentMetadata documentMetadata,
                         Long residentId, String uuid, String uniqueId , String hash,
                         SaveDocumentCallback callback);

    Document getDocument(String documentUniqueId);

    void deleteDocument(String documentUniqueId) throws IOException;


}
