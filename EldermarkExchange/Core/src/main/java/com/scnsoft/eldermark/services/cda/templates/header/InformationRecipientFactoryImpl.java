package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.PersonFactory;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.templates.OptionalTemplateFactory;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.IntendedRecipient;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.datatypes.ON;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A person who should get a copy
 *
 * @see InformationRecipient
 */
@Component
public class InformationRecipientFactoryImpl extends OptionalTemplateFactory implements InformationRecipientFactory {

    @Value("${header.informationRecipients.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public List<org.eclipse.mdht.uml.cda.InformationRecipient> buildTemplateInstance(Collection<InformationRecipient> informationRecipients) {
        final List<org.eclipse.mdht.uml.cda.InformationRecipient> ccdInformationRecipients = new ArrayList<>();

        for (com.scnsoft.eldermark.entity.InformationRecipient informationRecipient : informationRecipients) {
            final org.eclipse.mdht.uml.cda.InformationRecipient ccdInformationRecipient =
                    CDAFactory.eINSTANCE.createInformationRecipient();
            final IntendedRecipient intendedRecipient = CDAFactory.eINSTANCE.createIntendedRecipient();

            final Person ccdPerson = informationRecipient.getPerson();
            if (ccdPerson != null) {
                org.eclipse.mdht.uml.cda.Person person = CDAFactory.eINSTANCE.createPerson();
                if (ccdPerson.getNames() != null) {
                    for (Name name : ccdPerson.getNames()) {
                        CcdUtils.addConvertedName(person.getNames(), name);
                    }
                } else {
                    person.getNames().add(CcdUtils.getNullName());
                }
                intendedRecipient.setInformationRecipient(person);
            }

            final Organization ccdOrganization = informationRecipient.getOrganization();
            if (ccdOrganization != null) {
                org.eclipse.mdht.uml.cda.Organization organization = CDAFactory.eINSTANCE.createOrganization();
                ON on = DatatypesFactory.eINSTANCE.createON();
                if(ccdOrganization.getName() != null) {
                    on.addText(ccdOrganization.getName());
                } else {
                    on.setNullFlavor(NullFlavor.NI);
                }
                organization.getNames().add(on);
                intendedRecipient.setReceivedOrganization(organization);
            }

            ccdInformationRecipient.setIntendedRecipient(intendedRecipient);
            ccdInformationRecipients.add(ccdInformationRecipient);
        }

        return ccdInformationRecipients;
    }

    @Override
    public List<InformationRecipient> parseSection(Resident resident, Collection<org.eclipse.mdht.uml.cda.InformationRecipient> informationRecipients) {
        if (CollectionUtils.isEmpty(informationRecipients)) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final List<InformationRecipient> resultList = new ArrayList<>();
        for (org.eclipse.mdht.uml.cda.InformationRecipient srcRecipient : informationRecipients) {
            final InformationRecipient resultRecipient = new InformationRecipient();
            resultList.add(resultRecipient);
            resultRecipient.setResident(resident);
            resultRecipient.setDatabase(resident.getDatabase());
            resultRecipient.setDatabaseId(resident.getDatabaseId());

            final IntendedRecipient assignedEntity = srcRecipient.getIntendedRecipient();
            final Person person = PersonFactory.parse(assignedEntity, resident.getDatabase(), "NWHIN_RECIPIENT");
            resultRecipient.setPerson(person);

            org.eclipse.mdht.uml.cda.Organization srcOrg = assignedEntity.getReceivedOrganization();
            if (srcOrg != null) {
                Organization org = CcdTransform.toOrganization(srcOrg, resident.getDatabase(), "NWHIN_RECIPIENT");
                // TODO added default organization
                resultRecipient.setOrganization(resident.getFacility());
            }
        }

        return resultList;
    }

}
