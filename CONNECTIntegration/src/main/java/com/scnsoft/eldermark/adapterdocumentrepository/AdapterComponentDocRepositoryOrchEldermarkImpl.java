package com.scnsoft.eldermark.adapterdocumentrepository;

import com.scnsoft.eldermark.ws.api.download.*;
import gov.hhs.fha.nhinc.docrepository.adapter.AdapterComponentDocRepositoryOrchImpl;
import gov.hhs.fha.nhinc.docrepository.adapter.model.Document;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryErrorList;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AdapterComponentDocRepositoryOrchEldermarkImpl extends AdapterComponentDocRepositoryOrchImpl {

    private static final Logger LOG = Logger.getLogger(AdapterComponentDocRepositoryOrchEldermarkImpl.class);
    private static final String REPOSITORY_UNIQUE_ID = "2.16.840.1.113883.3.6492";

    /**
     * @param repositoryIdMatched is ignored because AdapterComponentDocRepositoryOrchImpl.REPOSITORY_UNIQUE_ID was hardcoded to "1"
     */
    @Override
    protected void retrieveDocuments(boolean repositoryIdMatched, List<String> documentUniqueIds,
                                     ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType response, String homeCommunityId,
                                     RegistryErrorList regerrList) {
        List<Document>connectDocuments = new ArrayList<Document>();

        DocumentsDownloadEndpointImplService service = new DocumentsDownloadEndpointImplService();
        DocumentsDownloadEndpoint port = service.getDocumentsDownloadEndpointImplPort();

        List<Long> documentIds = new ArrayList<Long>();

        for (String documentUniqueId: documentUniqueIds) {
            if (documentUniqueId.toLowerCase().contains("ccd")) {

                Long residentId = Long.parseLong(documentUniqueId.substring(4));
                try {
                    DocumentRetrieve res = port.generateCcd(residentId);
                    Document connectDoc = new Document();
                    connectDoc.setDocumentUniqueId(documentUniqueId);
                    connectDoc.setMimeType(res.getMimeType());
                    connectDoc.setRawData(res.getData());
                    connectDoc.setDocumentTitle(res.getDocumentTitle());
                    connectDoc.setNewRepositoryUniqueId(REPOSITORY_UNIQUE_ID);
                    connectDocuments.add(connectDoc);

                } catch (ResidentNotFoundFault residentNotFoundFault) {
                    residentNotFoundFault.printStackTrace();
                            setRegistryError("Retreive CCD for residentId" + residentId, "AdapterComponentDocRepositoryOrchEldermarkImpl.retrieveDocuments", ResidentNotFoundFault.class.getName(),
                                    residentNotFoundFault.getMessage());
                    residentNotFoundFault.printStackTrace();
                } catch (ResidentOptedOutFault residentOptedOutFault) {
                    setRegistryError("Retreive CCD for residentId" + residentId, "AdapterComponentDocRepositoryOrchEldermarkImpl.retrieveDocuments", ResidentOptedOutFault.class.getName(),
                            residentOptedOutFault.getMessage());
                    residentOptedOutFault.printStackTrace();
                }
            } else if (documentUniqueId.toLowerCase().contains("facesheet")){
                Long residentId = Long.parseLong(documentUniqueId.substring(10));
                try {
                    DocumentRetrieve res = port.generateFacesheet(residentId);
                    Document connectDoc = new Document();
                    connectDoc.setDocumentUniqueId(documentUniqueId);
                    connectDoc.setMimeType(res.getMimeType());
                    connectDoc.setRawData(res.getData());
                    connectDoc.setDocumentTitle(res.getDocumentTitle());
                    connectDocuments.add(connectDoc);

                } catch (ResidentNotFoundFault residentNotFoundFault) {
                    residentNotFoundFault.printStackTrace();
                    setRegistryError("Retreive FACESHEET for residentId" + residentId, "AdapterComponentDocRepositoryOrchEldermarkImpl.retrieveDocuments", ResidentNotFoundFault.class.getName(),
                            residentNotFoundFault.getMessage());
                    residentNotFoundFault.printStackTrace();
                } catch (ResidentOptedOutFault residentOptedOutFault) {
                    setRegistryError("Retreive FACESHEET for residentId" + residentId, "AdapterComponentDocRepositoryOrchEldermarkImpl.retrieveDocuments", ResidentOptedOutFault.class.getName(),
                            residentOptedOutFault.getMessage());
                    residentOptedOutFault.printStackTrace();
                }
            } else {
                documentIds.add(Long.parseLong(documentUniqueId));
            }
        }

        for(Long customDocId : documentIds) {
            try {
                DocumentRetrieve res = port.downloadDocument(customDocId);
                Document connectDoc = new Document();
                connectDoc.setDocumentUniqueId(customDocId.toString());
                connectDoc.setMimeType(res.getMimeType());
                connectDoc.setRawData(res.getData());
                connectDoc.setDocumentTitle(res.getDocumentTitle());
                connectDocuments.add(connectDoc);

            } catch (DocumentNotFoundFault documentNotFoundFault) {
                documentNotFoundFault.printStackTrace();
                setRegistryError("Retreive CCD for documentID" + customDocId, "AdapterComponentDocRepositoryOrchEldermarkImpl.retrieveDocuments", DocumentNotFoundFault.class.getName(),
                        documentNotFoundFault.getMessage());
            } catch (ResidentOptedOutFault residentOptedOutFault) {
                residentOptedOutFault.printStackTrace();
                setRegistryError("Retreive CCD for documentID" + customDocId, "AdapterComponentDocRepositoryOrchEldermarkImpl.retrieveDocuments", ResidentOptedOutFault.class.getName(),
                        residentOptedOutFault.getMessage());
            }
        }

        loadDocumentResponses(response, connectDocuments, homeCommunityId, documentUniqueIds, regerrList);
    }

    @Override
    protected void loadDocumentResponses(RetrieveDocumentSetResponseType response, List<Document> docs, String homeCommunityId, List<String> documentUniqueId, RegistryErrorList regerrList) {
        super.loadDocumentResponses(response, docs, homeCommunityId, documentUniqueId, regerrList);

        // update repositoryUniqueId from 1 to HCID
        for(RetrieveDocumentSetResponseType.DocumentResponse docResponse : response.getDocumentResponse()) {
            docResponse.setRepositoryUniqueId(REPOSITORY_UNIQUE_ID);
        }
    }

    private void setRegistryError(String codeContext, String location, String errorCode,
                                  String value){
        RegistryError error = new oasis.names.tc.ebxml_regrep.xsd.rs._3.ObjectFactory().createRegistryError();
        error.setCodeContext(codeContext);
        error.setLocation(location);
        error.setErrorCode(errorCode);
        error.setSeverity(NhincConstants.XDS_REGISTRY_ERROR_SEVERITY_ERROR);
        error.setValue(value);

        LOG.error("Error Location: " + error.getLocation() + "; \n" + "Error Severity: " + error.getSeverity()
                + "; \n" + "Error ErrorCode: " + error.getErrorCode() + "; \n" + "Error CodeContext: "
                + error.getCodeContext());
    }

}
