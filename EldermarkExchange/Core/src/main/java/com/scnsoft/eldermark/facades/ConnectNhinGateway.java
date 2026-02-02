package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.shared.DocumentDto;
import com.scnsoft.eldermark.shared.DocumentRetrieveDto;
import com.scnsoft.eldermark.shared.ResidentDto;
import com.scnsoft.eldermark.shared.ResidentFilter;

import java.util.List;


public interface ConnectNhinGateway {
    public List<ResidentDto> patientDiscovery(ResidentFilter filter, String assigningAuthorityId, ExchangeUserDetails employeeInfo);

    public List<DocumentDto> queryForDocuments(String residentId, String assigningAuthorityId, ExchangeUserDetails employeeInfo);

    public DocumentRetrieveDto retrieveDocument(String documentId, String assigningAuthorityId, ExchangeUserDetails employeeInfo);
}
