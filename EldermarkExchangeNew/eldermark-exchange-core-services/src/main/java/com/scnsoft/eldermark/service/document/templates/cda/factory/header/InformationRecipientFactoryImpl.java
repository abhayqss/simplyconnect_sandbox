package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.ccd.InformationRecipient;
import com.scnsoft.eldermark.service.document.templates.cda.factory.entries.CdaSectionEntryFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.PersonFactory;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.IntendedRecipient;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.datatypes.ON;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    private static CdaSectionEntryFactory cdaSectionEntryFactory = CdaSectionEntryFactory.INSTANCE;

    @Value("${header.informationRecipients.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public List<org.eclipse.mdht.uml.cda.InformationRecipient> buildTemplateInstance(Collection<InformationRecipient> informationRecipients) {
        final List<org.eclipse.mdht.uml.cda.InformationRecipient> ccdInformationRecipients = new ArrayList<>();

        for (com.scnsoft.eldermark.entity.document.ccd.InformationRecipient informationRecipient : informationRecipients) {
            final org.eclipse.mdht.uml.cda.InformationRecipient ccdInformationRecipient =
                    CDAFactory.eINSTANCE.createInformationRecipient();
            final IntendedRecipient intendedRecipient = CDAFactory.eINSTANCE.createIntendedRecipient();

            final Person ccdPerson = informationRecipient.getPerson();
            if (ccdPerson != null) {
                var person = cdaSectionEntryFactory.buildPerson(ccdPerson);
                intendedRecipient.setInformationRecipient(person);
            }

            final Organization ccdOrganization = informationRecipient.getOrganization();
            if (ccdOrganization != null) {
                org.eclipse.mdht.uml.cda.Organization organization = CDAFactory.eINSTANCE.createOrganization();
                ON on = DatatypesFactory.eINSTANCE.createON();
                if (ccdOrganization.getName() != null) {
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
    public List<InformationRecipient> parseSection(Client client, Collection<org.eclipse.mdht.uml.cda.InformationRecipient> informationRecipients) {
        if (CollectionUtils.isEmpty(informationRecipients)) {
            return Collections.emptyList();
        }
        checkNotNull(client);

        final List<InformationRecipient> resultList = new ArrayList<>();
        for (org.eclipse.mdht.uml.cda.InformationRecipient srcRecipient : informationRecipients) {
            final InformationRecipient resultRecipient = new InformationRecipient();
            resultList.add(resultRecipient);
            resultRecipient.setClient(client);
            resultRecipient.setOrganization(client.getOrganization());
            resultRecipient.setOrganizationId(client.getOrganizationId());

            final IntendedRecipient assignedEntity = srcRecipient.getIntendedRecipient();
            final Person person = PersonFactory.parse(assignedEntity, client.getOrganization(), "NWHIN_RECIPIENT");
            resultRecipient.setPerson(person);

            org.eclipse.mdht.uml.cda.Organization srcOrg = assignedEntity.getReceivedOrganization();
            if (srcOrg != null) {
                Community community = CcdTransform.toCommunity(srcOrg, client.getOrganization(), "NWHIN_RECIPIENT");
                // TODO added default organization
                resultRecipient.setCommunity(client.getCommunity());
            }
        }

        return resultList;
    }

}
