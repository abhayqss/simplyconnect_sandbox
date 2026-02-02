package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.services.connect.NhinPatientDiscoveryService;
import com.scnsoft.eldermark.services.connect.NhinQueryForDocumentsService;
import com.scnsoft.eldermark.services.connect.NhinRetrieveDocumentService;
import com.scnsoft.eldermark.shared.DocumentDto;
import com.scnsoft.eldermark.shared.DocumentRetrieveDto;
import com.scnsoft.eldermark.shared.ResidentDto;
import com.scnsoft.eldermark.shared.ResidentFilter;
import com.scnsoft.eldermark.shared.exceptions.NHINException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ConnectNhinGatewayImpl implements ConnectNhinGateway {

    private static final Logger logger = LoggerFactory.getLogger(ConnectNhinGatewayImpl.class);

    @Value("${nwhin.integration.enabled}")
    private boolean nwhinEnabled;

    @Autowired
    private NhinPatientDiscoveryService nhinPatientDiscoveryService;

    @Autowired
    private NhinQueryForDocumentsService nhinQueryForDocumentsService;

    @Autowired
    private NhinRetrieveDocumentService nhinRetrieveDocumentService;

    @Override
    public List<ResidentDto> patientDiscovery(ResidentFilter filter, String assigningAuthorityId, ExchangeUserDetails employeeInfo) {
        if (!nwhinEnabled) {
            return Collections.emptyList();
        }
        try {
            return nhinPatientDiscoveryService.patientDiscovery(filter, assigningAuthorityId, employeeInfo);
        } catch (Exception e) {
            logger.error("NwHIN PD error:", e);
            throw new NHINException();
        }
    }

    @Override
    public List<DocumentDto> queryForDocuments(String residentId, String assigningAuthorityId, ExchangeUserDetails employeeInfo) {
        if (!nwhinEnabled) {
            return Collections.emptyList();
        }
        try {
            return nhinQueryForDocumentsService.queryForDocuments(residentId, assigningAuthorityId, employeeInfo);
        } catch (Exception e) {
            logger.error("NwHIN QD error:", e);
            throw new NHINException(e.getMessage());
        }
    }

    @Override
    public DocumentRetrieveDto retrieveDocument(String documentId, String assigningAuthorityId, ExchangeUserDetails employeeInfo) {
        logger.info("ConnectNhinGatewayImpl.retrieveDocument");
        if (!nwhinEnabled) {
            throw new NHINException("Can't retrieve document: NWHIN integration is disabled");
        }
        try {
            return nhinRetrieveDocumentService.retrieveDocument(documentId, employeeInfo, assigningAuthorityId);
        } catch (Exception e) {
            logger.error("NwHIN DR error:", e);
            throw new NHINException(e.getMessage());
        }
    }
}


