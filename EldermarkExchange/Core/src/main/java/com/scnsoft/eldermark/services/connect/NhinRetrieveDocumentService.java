package com.scnsoft.eldermark.services.connect;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.shared.DocumentRetrieveDto;

public interface NhinRetrieveDocumentService {
    DocumentRetrieveDto retrieveDocument(String documentId, ExchangeUserDetails employeeInfo, String assigningAuthorityId);
}
