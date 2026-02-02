package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.CommunityAddress;
import com.scnsoft.eldermark.entity.document.ccd.Custodian;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.mdht.uml.cda.AssignedCustodian;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.CustodianOrganization;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An organization that maintains the document
 * <br/>
 * <p>
 * The custodian element represents the organization that is in charge of maintaining and is entrusted with the care of the document.
 * <br/><br/>
 * There is only one custodian per CDA document. Allowing that a CDA document may not represent the original
 * form of the authenticated document, the custodian represents the steward of the original source document.
 * The custodian may be the document originator, a health information exchange, or other responsible party.
 *
 * @see Custodian
 * @see Organization
 */
@Component
public class CustodianFactoryImpl extends OptionalTemplateFactory implements CustodianFactory {

    @Value("${header.custodian.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public org.eclipse.mdht.uml.cda.Custodian buildTemplateInstance(com.scnsoft.eldermark.entity.document.ccd.Custodian custodian) {
        if (custodian == null) {
            return buildNullCustodian();
        }

        org.eclipse.mdht.uml.cda.Custodian ccdCustodian = CDAFactory.eINSTANCE.createCustodian();

        AssignedCustodian assignedCustodian = CDAFactory.eINSTANCE.createAssignedCustodian();
        Community community = custodian.getCommunity();
        if (community != null) {
            CustodianOrganization custodianOrganization = CDAFactory.eINSTANCE.createCustodianOrganization();
            custodianOrganization.getIds().add(CcdUtils.getId(community.getId()));

            custodianOrganization.setAddr(chooseAndConvertAddress(community.getAddresses()));

            var tel = Optional.ofNullable(community.getTelecom())
                    .map(CcdUtils::convertTelecom)
                    .orElseGet(CcdUtils::getNullTelecom);
            custodianOrganization.setTelecom(tel);

            ON on = DatatypesFactory.eINSTANCE.createON();
            if (community.getName() != null) {
                on.addText(community.getName());
            } else {
                on.setNullFlavor(NullFlavor.NI);
            }
            custodianOrganization.setName(on);
            assignedCustodian.setRepresentedCustodianOrganization(custodianOrganization);
        } else {
            assignedCustodian.setNullFlavor(NullFlavor.NI);
        }
        ccdCustodian.setAssignedCustodian(assignedCustodian);

        return ccdCustodian;
    }

    //if addresses are present - give preference to WP address
    //otherwise if there is no address or no address was valid - create null address
    private AD chooseAndConvertAddress(Collection<CommunityAddress> addresses) {
        if (CollectionUtils.isNotEmpty(addresses)) {

            //search for valid WP
            for (CommunityAddress address : addresses) {
                if ("WP".equals(address.getPostalAddressUse())) {
                    AD ad = CcdUtils.convertAddress(address);
                    if (ad != null) {
                        return ad;
                    }
                }
            }

            //search for at least any valid address
            for (CommunityAddress address : addresses) {
                AD ad = CcdUtils.convertAddress(address);
                if (ad != null) {
                    return ad;
                }
            }
        }
        return CcdUtils.getNullAddress();
    }

    private org.eclipse.mdht.uml.cda.Custodian buildNullCustodian() {
        org.eclipse.mdht.uml.cda.Custodian ccdCustodian = CDAFactory.eINSTANCE.createCustodian();

        AssignedCustodian assignedCustodian = CDAFactory.eINSTANCE.createAssignedCustodian();
        CustodianOrganization custodianOrganization = CDAFactory.eINSTANCE.createCustodianOrganization();
        II id = DatatypesFactory.eINSTANCE.createII();
        id.setNullFlavor(NullFlavor.NI);
        custodianOrganization.getIds().add(id);
        custodianOrganization.setAddr(CcdUtils.getNullAddress());
        custodianOrganization.setTelecom(CcdUtils.getNullTelecom());
        ON on = DatatypesFactory.eINSTANCE.createON();
        on.setNullFlavor(NullFlavor.NI);
        custodianOrganization.setName(on);
        assignedCustodian.setRepresentedCustodianOrganization(custodianOrganization);
        ccdCustodian.setAssignedCustodian(assignedCustodian);

        return ccdCustodian;
    }

    @Override
    public Custodian parseSection(Client client, org.eclipse.mdht.uml.cda.Custodian srcCustodian) {
        if (!CcdParseUtils.hasContent(srcCustodian)) {
            return null;
        }
        checkNotNull(client);

        final Custodian resultCustodian = new Custodian();
        resultCustodian.setClient(client);
        resultCustodian.setOrganization(client.getOrganization());
        resultCustodian.setOrganizationId(client.getOrganizationId());
        resultCustodian.setLegacyId(0L);

        if (CcdParseUtils.hasContent(srcCustodian.getAssignedCustodian())) {
            final CustodianOrganization srcOrg = srcCustodian.getAssignedCustodian().getRepresentedCustodianOrganization();
            if (srcOrg != null) {
                Community community = CcdTransform.toCommunity(srcOrg, client.getOrganization(), "NWHIN_CUSTODIAN");
                // TODO added default organization
                resultCustodian.setCommunity(client.getCommunity());
            }
        }

        client.setCustodian(resultCustodian);
        return resultCustodian;
    }

}
