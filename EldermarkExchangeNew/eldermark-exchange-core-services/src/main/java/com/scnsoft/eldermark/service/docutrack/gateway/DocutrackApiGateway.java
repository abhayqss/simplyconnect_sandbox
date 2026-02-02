package com.scnsoft.eldermark.service.docutrack.gateway;

public interface DocutrackApiGateway {

    String insertDocument(DocutrackApiClient apiClient, String sourceId, String sourceName,
                          String mimeType, byte[] document, String businessUnitCode, String documentText);

    byte[] getDocument(DocutrackApiClient apiClient, Long documentId);
}
