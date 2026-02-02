package com.scnsoft.eldermark.adapterdocumentregistry;

import com.scnsoft.eldermark.ws.api.documents.*;
import gov.hhs.fha.nhinc.docrepository.adapter.model.Document;
import gov.hhs.fha.nhinc.docrepository.adapter.model.DocumentQueryParams;
import gov.hhs.fha.nhinc.docrepository.adapter.service.DocumentService;

import java.util.ArrayList;
import java.util.List;

public class EldermarkDocumentService extends DocumentService {

    @Override
    public List<Document> documentQuery(DocumentQueryParams params) {
        DocumentsEndpointImplService service = new DocumentsEndpointImplService();
        DocumentsEndpoint port = service.getDocumentsEndpointImplPort();

        Long residentId = null;
        try {
            String residentIdStr = params.getPatientId().substring(0, params.getPatientId().indexOf('^'));
            residentId = Long.parseLong(residentIdStr);
        } catch (Exception e) {
            e.printStackTrace();  //TODO
        }

        List<com.scnsoft.eldermark.ws.api.documents.Document> eldDocs = null;
        try {
            eldDocs = port.queryForDocuments(residentId);
        } catch (ResidentNotFoundFault e) {
            e.printStackTrace();
            //TODO;
        } catch (ResidentOptedOutFault e) {
            e.printStackTrace();
            //TODO;
        } catch (Exception e) {
            e.printStackTrace();
            //TODO;
        }

        return createConnectDocs(eldDocs, residentId);
    }

    private List<gov.hhs.fha.nhinc.docrepository.adapter.model.Document> createConnectDocs(List<com.scnsoft.eldermark.ws.api.documents.Document> documents, Long residentId) {

        List<gov.hhs.fha.nhinc.docrepository.adapter.model.Document> connectDocList = new ArrayList<gov.hhs.fha.nhinc.docrepository.adapter.model.Document>();

        for (com.scnsoft.eldermark.ws.api.documents.Document document : documents) {
            // include only CCD
            if (DocumentType.CCD.equals(document.getDocumentType())) {
                gov.hhs.fha.nhinc.docrepository.adapter.model.Document connectDoc = new gov.hhs.fha.nhinc.docrepository.adapter.model.Document();

                if (DocumentType.CCD.equals(document.getDocumentType()))     {
                    connectDoc.setDocumentUniqueId("ccd." + residentId);
                } else if (DocumentType.FACESHEET.equals(document.getDocumentType())) {
                    connectDoc.setDocumentUniqueId("facesheet." + residentId);
                } else {
                    connectDoc.setDocumentUniqueId(document.getId());
                }

                connectDoc.setAuthorPerson(document.getAuthorName());

                if (document.getCreationTime() != null)
                    connectDoc.setCreationTime(document.getCreationTime().toGregorianCalendar().getTime());

                connectDoc.setDocumentTitle(document.getDocumentTitle());

                connectDoc.setMimeType(document.getMimeType());

                connectDoc.setSize(document.getSize());

                connectDoc.setLanguageCode("en-US");

                connectDoc.setHash("-1");

                String patientId = residentId + "^^^&amp;2.16.840.1.113883.3.6492&amp;ISO";
                connectDoc.setSourcePatientId(patientId);
                connectDoc.setPatientId(patientId);

                connectDoc.setConfidentialityCode("N");
                connectDoc.setConfidentialityCodeScheme("2.16.840.1.113883.5.25");
                connectDoc.setConfidentialityCodeDisplayName("Normal");

                connectDoc.setFacilityCode("COMM");
                connectDoc.setFacilityCodeScheme("2.16.840.1.113883.5.111");
                connectDoc.setFacilityCodeDisplayName("Community Location");

                connectDoc.setPracticeSetting("394802001");
                connectDoc.setPracticeSettingScheme("2.16.840.1.113883.6.96");
                connectDoc.setPracticeSettingDisplayName("General Medicine");

                connectDoc.setClassCode("34133-9");
                connectDoc.setClassCodeDisplayName("2.16.840.1.113883.6.1");
                connectDoc.setClassCodeScheme("Continuity of Care Document");

                connectDoc.setTypeCode("34133-9");
                connectDoc.setTypeCodeDisplayName("2.16.840.1.113883.6.1");
                connectDoc.setTypeCodeScheme("Continuity of Care Document");

                connectDoc.setFormatCode("2.16.840.1.113883.10.20.1");
                connectDoc.setFormatCodeScheme("IHE");
                connectDoc.setFormatCodeDisplayName("HL7 CCD Document");

                connectDocList.add(connectDoc);
            }
        }

        return connectDocList;
    }
}
