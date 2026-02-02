package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.Custodian;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.OrganizationAddress;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.OptionalTemplateFactory;
import org.eclipse.mdht.uml.cda.AssignedCustodian;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.CustodianOrganization;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An organization that maintains the document
 * <br/>
 *
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
    public org.eclipse.mdht.uml.cda.Custodian buildTemplateInstance(com.scnsoft.eldermark.entity.Custodian custodian) {
        if (custodian == null) {
            return null;
        }

        org.eclipse.mdht.uml.cda.Custodian ccdCustodian = CDAFactory.eINSTANCE.createCustodian();

        AssignedCustodian assignedCustodian = CDAFactory.eINSTANCE.createAssignedCustodian();
        Organization organization = custodian.getOrganization();
        if (organization != null) {
            CustodianOrganization custodianOrganization = CDAFactory.eINSTANCE.createCustodianOrganization();
            II id = DatatypesFactory.eINSTANCE.createII();
            if (organization.getId() != null) {
                id.setRoot("2.16.840.1.113883.4.6");
                id.setExtension(organization.getId().toString());
            } else {
                id.setNullFlavor(NullFlavor.NI);
            }
            custodianOrganization.getIds().add(id);
            if (organization.getAddresses() != null) {
                if (organization.getAddresses().size() > 1) {
                    for (OrganizationAddress address : organization.getAddresses()) {
                        if ("WP".equals(address.getPostalAddressUse())) {
                            AD ad = CcdUtils.convertAddress(address);
                            if (ad != null) {
                                custodianOrganization.setAddr(ad);
                            }
                        }
                    }
                } else if (organization.getAddresses().size() == 1) {
                    AD ad = CcdUtils.convertAddress(organization.getAddresses().get(0));
                    if (ad != null) {
                        custodianOrganization.setAddr(ad);
                    }
                }
            } else {
                custodianOrganization.setAddr(CcdUtils.getNullAddress());
            }
            if (organization.getTelecom() != null) {
                TEL tel = CcdUtils.convertTelecom(organization.getTelecom());
                if (tel != null) {
                    custodianOrganization.setTelecom(tel);
                }
            } else {
                custodianOrganization.setTelecom(CcdUtils.getNullTelecom());
            }
            ON on = DatatypesFactory.eINSTANCE.createON();
            if (organization.getName() != null) {
                on.addText(organization.getName());
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

    @Override
    public Custodian parseSection(Resident resident, org.eclipse.mdht.uml.cda.Custodian srcCustodian) {
        if (!CcdParseUtils.hasContent(srcCustodian)) {
            return null;
        }
        checkNotNull(resident);

        final Custodian resultCustodian = new Custodian();
        resultCustodian.setResident(resident);
        resultCustodian.setDatabase(resident.getDatabase());
        resultCustodian.setDatabaseId(resident.getDatabaseId());
        resultCustodian.setLegacyId(0L);

        if (CcdParseUtils.hasContent(srcCustodian.getAssignedCustodian())) {
            final CustodianOrganization srcOrg = srcCustodian.getAssignedCustodian().getRepresentedCustodianOrganization();
            if (srcOrg != null) {
                Organization org = CcdTransform.toOrganization(srcOrg, resident.getDatabase(), "NWHIN_CUSTODIAN");
                // TODO added default organization
                resultCustodian.setOrganization(resident.getFacility());
            }
        }

        resident.setCustodian(resultCustodian);
        return resultCustodian;
    }

}
