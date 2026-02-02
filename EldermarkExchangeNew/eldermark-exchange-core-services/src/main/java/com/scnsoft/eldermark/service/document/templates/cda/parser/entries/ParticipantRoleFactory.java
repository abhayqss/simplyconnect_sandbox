package com.scnsoft.eldermark.service.document.templates.cda.parser.entries;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.DrugVehicle;
import com.scnsoft.eldermark.entity.community.CommunityAddress;
import com.scnsoft.eldermark.entity.community.CommunityTelecom;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.ProductInstance;
import com.scnsoft.eldermark.entity.document.ccd.ServiceDeliveryLocation;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.Entity;
import org.eclipse.mdht.uml.cda.Participant2;
import org.eclipse.mdht.uml.cda.ParticipantRole;
import org.eclipse.mdht.uml.cda.PlayingEntity;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author phomal
 * Created on 4/13/2018.
 */
@Component
public class ParticipantRoleFactory {

    private final CcdCodeFactory ccdCodeFactory;

    @Autowired
    public ParticipantRoleFactory(CcdCodeFactory ccdCodeFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
    }

    private DrugVehicle parseDrugVehicle(ParticipantRole participantRole, Client client) {
        if (!CcdParseUtils.hasContent(participantRole) || client == null || participantRole.getPlayingEntity() == null) {
            return null;
        }

        DrugVehicle drugVehicle = new DrugVehicle();
        drugVehicle.setOrganization(client.getOrganization());

        PlayingEntity ccdPlayingEntity = participantRole.getPlayingEntity();
        drugVehicle.setCode(ccdCodeFactory.convert(ccdPlayingEntity.getCode()));
        PN pn = CcdParseUtils.getFirstNotEmptyValue(ccdPlayingEntity.getNames(), PN.class);
        if (pn != null) {
            drugVehicle.setName(pn.getText());
        }

        return drugVehicle;
    }

    public List<DrugVehicle> parseDrugVehicles(EList<Participant2> participants, Client client) {
        if (!CollectionUtils.isEmpty(participants)) {
            List<DrugVehicle> drugVehicles = new ArrayList<>();
            for (Participant2 ccdParticipant : participants) {
                DrugVehicle drugVehicle = parseDrugVehicle(ccdParticipant.getParticipantRole(), client);
                if (drugVehicle != null) {
                    drugVehicles.add(drugVehicle);
                }
            }
            return drugVehicles;
        }
        return null;
    }

    public ServiceDeliveryLocation parseServiceDeliveryLocation(ParticipantRole ccdParticipantRole, Client client, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdParticipantRole) || client == null) {
            return null;
        }

        ServiceDeliveryLocation serviceDeliveryLocation = new ServiceDeliveryLocation();
        serviceDeliveryLocation.setOrganization(client.getOrganization());
        serviceDeliveryLocation.setCode(ccdCodeFactory.convert(ccdParticipantRole.getCode()));
        if (ccdParticipantRole.getPlayingEntity() != null && !CollectionUtils.isEmpty(ccdParticipantRole.getPlayingEntity().getNames())) {
            PN name = ccdParticipantRole.getPlayingEntity().getNames().get(0);
            serviceDeliveryLocation.setName(name.getText());
            ED desc = ccdParticipantRole.getPlayingEntity().getDesc();
            serviceDeliveryLocation.setDescription(CcdTransform.EDtoString(desc));
        }
        if (!CollectionUtils.isEmpty(ccdParticipantRole.getAddrs())) {
            List<CommunityAddress> addresses = new ArrayList<>();
            for (AD ccdAddress : ccdParticipantRole.getAddrs()) {
                if (CcdParseUtils.hasContent(ccdAddress)) {
                	CommunityAddress address = CcdParseUtils.createAddress(ccdAddress, client.getOrganization(), client.getCommunity(), legacyTable);
                    addresses.add(address);
                }
            }
            serviceDeliveryLocation.setAddresses(addresses);
        }
        if (!CollectionUtils.isEmpty(ccdParticipantRole.getTelecoms())) {
            List<CommunityTelecom> telecoms = new ArrayList<>();
            for (TEL ccdTelecom : ccdParticipantRole.getTelecoms()) {
                if (CcdParseUtils.hasContent(ccdTelecom)) {
                	CommunityTelecom telecom = CcdParseUtils.createTelecom(ccdTelecom, client.getOrganization(), client.getCommunity(), legacyTable);
                    telecoms.add(telecom);
                }
            }
            serviceDeliveryLocation.setTelecoms(telecoms);
        }

        return serviceDeliveryLocation;
    }

    public ProductInstance parseProductInstance(ParticipantRole sourceProduct, Client client) {
        ProductInstance targetInstance = new ProductInstance();
        targetInstance.setOrganization(client.getOrganization());
        targetInstance.setLegacyId(CcdParseUtils.getFirstIdExtension(sourceProduct.getIds()));

        // TODO according to specs the UDI should be sent in the participantRole/id. Why is it ignored here?
        // see http://ccda.art-decor.org/ccda-html-20150727T182455/tmp-2.16.840.1.113883.10.20.22.4.37-2013-01-31T000000.html

        if (sourceProduct.getPlayingDevice() != null) {
            targetInstance.setDeviceCode(ccdCodeFactory.convert(sourceProduct.getPlayingDevice().getCode()));
        }

        final Entity scopingEntity = sourceProduct.getScopingEntity();
        if (scopingEntity != null) {
            II scopeEntityId = CcdParseUtils.getFirstNotEmptyValue(scopingEntity.getIds(), II.class);
            if (scopeEntityId != null) {
                // TODO The `root` alone may be the entire instance identifier. What if `extension` is null here?
                targetInstance.setScopingEntityId(scopeEntityId.getExtension());
            }
            if (CcdParseUtils.hasContent(scopingEntity.getDesc())) {
                targetInstance.setScopingEntityDescription(scopingEntity.getDesc().getText());
            }
            if (CcdParseUtils.hasContent(scopingEntity.getCode())) {
                final CcdCode scopingEntityCode = ccdCodeFactory.convert(scopingEntity.getCode());
                targetInstance.setScopingEntityCode(scopingEntityCode);
            }
        }

        return targetInstance;
    }

}
