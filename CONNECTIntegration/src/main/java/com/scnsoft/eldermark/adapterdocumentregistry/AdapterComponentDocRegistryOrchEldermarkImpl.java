package com.scnsoft.eldermark.adapterdocumentregistry;

import gov.hhs.fha.nhinc.docregistry.adapter.AdapterComponentDocRegistryOrchImpl;
import gov.hhs.fha.nhinc.docrepository.adapter.model.Document;
import gov.hhs.fha.nhinc.docrepository.adapter.service.DocumentService;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.UUID;

public class AdapterComponentDocRegistryOrchEldermarkImpl extends AdapterComponentDocRegistryOrchImpl {

    private static final Logger LOG = Logger.getLogger(AdapterComponentDocRegistryOrchEldermarkImpl.class);

    private static final String EBXML_RESPONSE_REPOSITORY_UNIQUE_ID_SLOTNAME = "repositoryUniqueId";

    @Override
    protected DocumentService getDocumentService() {
        LOG.error("DocumentEldermarkService");
        return new EldermarkDocumentService();
    }

    @Override
    protected String retrieveHomeCommunityId() {
        return "urn:oid:2.16.840.1.113883.3.6492";
    }

    @Override
    public void loadResponseMessage(AdhocQueryResponse response, List<Document> docs) {
        super.loadResponseMessage(response, docs);

        RegistryObjectListType regObjList = response.getRegistryObjectList();

        for(JAXBElement<? extends IdentifiableType> regObj : regObjList.getIdentifiable()) {
            ExtrinsicObjectType extrinsicObjectType = (ExtrinsicObjectType) regObj.getValue();

            // update repositoryUniqueId from 1 to HCID
            updateSlotValue(extrinsicObjectType.getSlot(), EBXML_RESPONSE_REPOSITORY_UNIQUE_ID_SLOTNAME, "2.16.840.1.113883.3.6492");

            // update Ids of ExternalIdentifiers from "" to random UUID
            for (ExternalIdentifierType externalId : extrinsicObjectType.getExternalIdentifier()) {
                if("".equals(externalId.getId())) {
                    externalId.setId("urn:uuid:" + UUID.randomUUID().toString());
                }
            }

            // update Ids of Classifications from "" to random UUID
            for (ClassificationType classificationType : extrinsicObjectType.getClassification()) {
                if ("".equals(classificationType.getId())) {
                    classificationType.setId("urn:uuid:" + UUID.randomUUID().toString());
                }
            }
        }
    }

    private void updateSlotValue(List<SlotType1> slots, String slotName, String newValue) {
        if (slots != null) {
            for (SlotType1 slot : slots) {
                if (slotName.equals(slot.getName()) && (slot.getValueList() != null)
                        && (slot.getValueList().getValue() != null) && (slot.getValueList().getValue().size() > 0)) {

                    ValueListType oValueList = new ValueListType();
                    List<String> olValue = oValueList.getValue();
                    olValue.add(newValue);

                    slot.setValueList(oValueList);
                }
            }
        }
    }
}
