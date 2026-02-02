package com.scnsoft.eldermark.services.connect;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.shared.DocumentDto;
import com.scnsoft.eldermark.shared.DocumentType;
import gov.hhs.fha.nhinc.common.nhinccommonentity.RespondingGatewayCrossGatewayQueryRequestType;
import gov.hhs.fha.nhinc.entitydocquery.EntityDocQuery;
import gov.hhs.fha.nhinc.entitydocquery.EntityDocQueryPortType;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.query._3.ResponseOptionType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;
import javax.xml.ws.BindingProvider;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class NhinQueryForDocumentsServiceImpl extends NhinAbstractService implements NhinQueryForDocumentsService {


    @Value("${connect.gateway.url.queryfordocuments}")
    private String wsUrl;

    private static final Logger logger = LoggerFactory.getLogger(com.scnsoft.eldermark.services.connect.NhinQueryForDocumentsServiceImpl.class);

    @Override
    public List<DocumentDto> queryForDocuments(String residentId, String assigningAuthorityId, ExchangeUserDetails employeeInfo) {
        RespondingGatewayCrossGatewayQueryRequestType request = createAdhocQueryRequest(residentId, assigningAuthorityId);
        request.setAssertion(ConnectUtil.createAssertion(employeeInfo));
        request.setNhinTargetCommunities(ConnectUtil.createNhinTargetCommunitiesType(assigningAuthorityId));

        EntityDocQuery entityDocQuery = new EntityDocQuery();
        EntityDocQueryPortType port = entityDocQuery.getEntityDocQueryPortSoap();
        BindingProvider provider = (BindingProvider) port;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsUrl);

        applySSLParameters(provider, employeeInfo);

        AdhocQueryResponse response = port.respondingGatewayCrossGatewayQuery(request);

        return parseResponse(response);
    }

    private RespondingGatewayCrossGatewayQueryRequestType createAdhocQueryRequest(String residentId, String assigningAuthorityId) {
        AdhocQueryType adhocQuery = new AdhocQueryType();
        adhocQuery.setHome("urn:oid:" + assigningAuthorityId);
        adhocQuery.setId("urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0d");

        // Set patient id
        SlotType1 patientIDSlot = new SlotType1();
        patientIDSlot.setName("$XDSDocumentEntryPatientId");
        ValueListType valueList = new ValueListType();
        StringBuilder universalPatientID = new StringBuilder();
        universalPatientID.append(residentId);
        universalPatientID.append("^^^&");
        universalPatientID.append(assigningAuthorityId);
        universalPatientID.append("&ISO");
        valueList.getValue().add(universalPatientID.toString());
        patientIDSlot.setValueList(valueList);
        adhocQuery.getSlot().add(patientIDSlot);

        // Populate $XDSDocumentEntryStatus slot to address Gateway-166
        SlotType1 documentEntryStatusSlot = new SlotType1();
        documentEntryStatusSlot.setName("$XDSDocumentEntryStatus");
        ValueListType valueEntryStatusList = new ValueListType();
        valueEntryStatusList.getValue().add("('urn:oasis:names:tc:ebxml-regrep:StatusType:Approved')");
        documentEntryStatusSlot.setValueList(valueEntryStatusList);
        adhocQuery.getSlot().add(documentEntryStatusSlot);

        ResponseOptionType responseOption = new ResponseOptionType();
        responseOption.setReturnType("LeafClass");
        responseOption.setReturnComposedObjects(Boolean.FALSE);

        AdhocQueryRequest adhocQueryRequest = new AdhocQueryRequest();
        adhocQueryRequest.setAdhocQuery(adhocQuery);
        adhocQueryRequest.setResponseOption(responseOption);

        RespondingGatewayCrossGatewayQueryRequestType request = new RespondingGatewayCrossGatewayQueryRequestType();
        request.setAdhocQueryRequest(adhocQueryRequest);

        return request;
    }

    private List<DocumentDto> parseResponse(AdhocQueryResponse response) {
        List<DocumentDto> documents = new ArrayList<DocumentDto>();

        RegistryObjectListType registryObjectListType = response.getRegistryObjectList();

        if (registryObjectListType != null) {
            List<JAXBElement<? extends IdentifiableType>> identifiable = registryObjectListType.getIdentifiable();
            if (NullChecker.isNotNullish(identifiable)) {
                for (JAXBElement jaxbElement : identifiable) {

                    DocumentDto documentDto = new DocumentDto();
                    documentDto.setDocumentType(DocumentType.NWHIN);

                    if (jaxbElement.getValue() != null) {
                        ExtrinsicObjectType extrinsicObjectType = (ExtrinsicObjectType) jaxbElement.getValue();

                        List<ExternalIdentifierType> externalIdentifierTypes = extrinsicObjectType.getExternalIdentifier();

                        if (NullChecker.isNotNullish(externalIdentifierTypes)) {
                            for(ExternalIdentifierType id : externalIdentifierTypes) {
                                if("urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab".equals(id.getIdentificationScheme()))
                                    documentDto.setId(id.getValue());
                            }
                        }

                        documentDto.setMimeType(extrinsicObjectType.getMimeType());

                        InternationalStringType internationalStringType = extrinsicObjectType.getName();
                        if (internationalStringType != null) {
                            List<LocalizedStringType> localizedStringTypes = internationalStringType.getLocalizedString();
                            if (NullChecker.isNotNullish(localizedStringTypes)) ;
                            LocalizedStringType localizedStringType = localizedStringTypes.get(0);
                            documentDto.setDocumentTitle(localizedStringType.getValue());
                        }

                        List<SlotType1> slotType1List = extrinsicObjectType.getSlot();
                        if (NullChecker.isNotNullish(slotType1List)) {
                            for (SlotType1 slotType1 : slotType1List) {
                                String name = slotType1.getName();
                                String value = getSlotValue(slotType1);
//                                ValueListType valueListType = slotType1.getValueList();
//                                if (valueListType != null) {
//                                    List<String> valStringList = valueListType.getValue();
//                                    if (NullChecker.isNotNullish(valStringList)) {
//                                        value = valStringList.get(0);
//                                    }
                                if (value != null) {
                                    if (name.equals("creationTime")) {

                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                                        try {
                                            documentDto.setCreationTime(formatter.parse(value));
                                        } catch (ParseException e) {
                                            logger.error("Wrong Creation Time format: " + value + " for Document: " + documentDto.getId());
                                        }
                                    } else if (name.equals("size")) {
                                        try {
                                            documentDto.setSize(Integer.parseInt(value));
                                        } catch (NumberFormatException e) {
                                            logger.error("Wrong Document Size format: " + value + " for Document: " + documentDto.getId());
                                        }
                                    } else if (name.equals("repositoryUniqueId")) {
                                        documentDto.setDatabaseId(value);
                                    }
                                }
                            }
                        }

                        List<ClassificationType> classificationTypes = extrinsicObjectType.getClassification();
                        if (NullChecker.isNotNullish(classificationTypes)) {
                            ClassificationType classificationType = classificationTypes.get(0);
                            List<SlotType1> slotList = classificationType.getSlot();
                            if (NullChecker.isNotNullish(slotList)) {
                                for (SlotType1 slotType1 : slotList) {
                                    String name = slotType1.getName();
                                    String value = getSlotValue(slotType1);
                                    if (value != null) {
                                        if (name.equals("authorPerson")) {
                                            documentDto.setAuthorPerson(value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    documents.add(documentDto);
                }
            }
        }
        return documents;
    }

    private String getSlotValue(SlotType1 slotType1) {
        String value = null;
        ValueListType valueListType = slotType1.getValueList();
        if (valueListType != null) {
            List<String> valStringList = valueListType.getValue();
            if (NullChecker.isNotNullish(valStringList)) {
                value = valStringList.get(0);
            }
        }
        return value;
    }
}
