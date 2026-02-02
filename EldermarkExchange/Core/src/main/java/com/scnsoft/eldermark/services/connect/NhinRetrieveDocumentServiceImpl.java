package com.scnsoft.eldermark.services.connect;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.facades.ConnectNhinGatewayImpl;
import com.scnsoft.eldermark.shared.DocumentRetrieveDto;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetCommunitiesType;
import gov.hhs.fha.nhinc.common.nhinccommonentity.RespondingGatewayCrossGatewayRetrieveRequestType;
import gov.hhs.fha.nhinc.entitydocretrieve.EntityDocRetrieve;
import gov.hhs.fha.nhinc.entitydocretrieve.EntityDocRetrievePortType;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.ws.BindingProvider;
import java.util.List;

@Service
public class NhinRetrieveDocumentServiceImpl extends NhinAbstractService implements NhinRetrieveDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(NhinRetrieveDocumentService.class);
    @Value("${connect.gateway.url.documentretrieve}")
    private String wsUrl;

    @Override
    public DocumentRetrieveDto retrieveDocument(String documentId, ExchangeUserDetails employeeInfo, String assigningAuthorityId) {

        logger.info("NhinRetrieveDocumentServiceImpl.retrieveDocument");
        EntityDocRetrieve entityDocRetrieve = new EntityDocRetrieve();
        EntityDocRetrievePortType port = entityDocRetrieve.getEntityDocRetrievePortSoap();

        BindingProvider provider = (BindingProvider) port;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsUrl);

        applySSLParameters(provider, employeeInfo);

        RespondingGatewayCrossGatewayRetrieveRequestType request = createRequest(documentId, assigningAuthorityId);

        request.setAssertion(ConnectUtil.createAssertion(employeeInfo));
        request.setNhinTargetCommunities(ConnectUtil.createNhinTargetCommunitiesType(assigningAuthorityId));

        RetrieveDocumentSetResponseType response = port.respondingGatewayCrossGatewayRetrieve(request);

        List<RetrieveDocumentSetResponseType.DocumentResponse> documentResponseList = response.getDocumentResponse();

        DocumentRetrieveDto documentRetrieveDto = new DocumentRetrieveDto();

        if (NullChecker.isNotNullish(documentResponseList)) {
            RetrieveDocumentSetResponseType.DocumentResponse documentResponse = documentResponseList.get(0);
            documentRetrieveDto.setMimeType(documentResponse.getMimeType());
            documentRetrieveDto.setDocumentTitle(documentResponse.getDocumentUniqueId());
            documentRetrieveDto.setData(documentResponse.getDocument());
        }

        return documentRetrieveDto;
    }

    private RespondingGatewayCrossGatewayRetrieveRequestType createRequest(String documentId, String assigningAuthorityId) {
        RespondingGatewayCrossGatewayRetrieveRequestType request = new RespondingGatewayCrossGatewayRetrieveRequestType();

        RetrieveDocumentSetRequestType retrieveDocumentSetRequest = new RetrieveDocumentSetRequestType();

        RetrieveDocumentSetRequestType.DocumentRequest docRequest = new RetrieveDocumentSetRequestType.DocumentRequest();
        docRequest.setHomeCommunityId("urn:oid:" + assigningAuthorityId);
        docRequest.setRepositoryUniqueId(assigningAuthorityId);
        docRequest.setDocumentUniqueId(documentId);

        retrieveDocumentSetRequest.getDocumentRequest().add(docRequest);

        request.setRetrieveDocumentSetRequest(retrieveDocumentSetRequest);

        return request;
    }
}
