package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.document.ccd.Informant;
import com.scnsoft.eldermark.service.document.templates.cda.factory.entries.CdaSectionEntryFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.PersonFactory;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.mdht.uml.cda.AssignedEntity;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.Informant12;
import org.eclipse.mdht.uml.cda.RelatedEntity;
import org.eclipse.mdht.uml.hl7.datatypes.II;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.eclipse.mdht.uml.hl7.vocab.RoleClassMutualRelationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The informant element describes the source of the information in a medical document.
 * Informant is a person who provided information (e.g. family member of patient who could not speak)
 * Assigned health care providers may be a source of information when a document is created.
 * (e.g., a nurse's aide who provides information about a recent significant health care event
 * that occurred within an acute care facility.)
 *
 * @see Informant
 */
@Component
public class InformantFactoryImpl extends OptionalTemplateFactory implements InformantFactory {

    private static CdaSectionEntryFactory cdaSectionEntryFactory = CdaSectionEntryFactory.INSTANCE;

    @Autowired
    private PersonFactory personFactory;

    @Value("${header.informant.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public List<Informant12> buildTemplateInstance(Collection<Informant> informants) {
        final List<Informant12> ccdInformants = new ArrayList<>();

        for (Informant informant : informants) {
            final Informant12 informant12 = CDAFactory.eINSTANCE.createInformant12();

            if (Boolean.TRUE.equals(informant.getPersonalRelation())) {
                final RelatedEntity relatedEntity = CDAFactory.eINSTANCE.createRelatedEntity();
                relatedEntity.setClassCode(RoleClassMutualRelationship.PRS);
                final Person ccdPerson = informant.getPerson();
                if (ccdPerson != null) {
                    if (ccdPerson.getCode() != null) {
                        relatedEntity.setCode(CcdUtils.createCE(ccdPerson.getCode(), "2.16.840.1.113883.6.101"));
                    }

                    CcdUtils.addConvertedAddresses(ccdPerson.getAddresses(), relatedEntity.getAddrs(), true);
                    CcdUtils.addConvertedTelecoms(ccdPerson.getTelecoms(), relatedEntity.getTelecoms(), false);

                    var person = cdaSectionEntryFactory.buildPerson(ccdPerson);
                    relatedEntity.setRelatedPerson(person);
                } else {
                    relatedEntity.setNullFlavor(NullFlavor.NI);
                    relatedEntity.getAddrs().add(CcdUtils.getNullAddress());
                    relatedEntity.setRelatedPerson(cdaSectionEntryFactory.buildNullPerson());
                }
                informant12.setRelatedEntity(relatedEntity);
            } else {
                final AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
                final Person ccdPerson = informant.getPerson();
                if (ccdPerson != null) {
                    assignedEntity.getIds().add(CcdUtils.getId(ccdPerson.getId()));
                    if (ccdPerson.getCode() != null) {
                        assignedEntity.setCode(CcdUtils.createCE(ccdPerson.getCode(), "2.16.840.1.113883.6.101"));
                    }

                    CcdUtils.addConvertedAddresses(ccdPerson.getAddresses(), assignedEntity.getAddrs(), true);
                    CcdUtils.addConvertedTelecoms(ccdPerson.getTelecoms(), assignedEntity.getTelecoms(), false);

                    var person = cdaSectionEntryFactory.buildPerson(ccdPerson);
                    assignedEntity.setAssignedPerson(person);
                } else {
                    assignedEntity.setNullFlavor(NullFlavor.NI);
                    assignedEntity.getIds().add(CcdUtils.getNullId());
                    assignedEntity.getAddrs().add(CcdUtils.getNullAddress());
                    assignedEntity.setAssignedPerson(cdaSectionEntryFactory.buildNullPerson());
                }
                informant12.setAssignedEntity(assignedEntity);
            }
            ccdInformants.add(informant12);
        }

        return ccdInformants;
    }

    @Override
    public List<Informant> parseSection(Client client, Collection<Informant12> informant12s) {
        if (CollectionUtils.isEmpty(informant12s)) {
            return Collections.emptyList();
        }
        checkNotNull(client);

        final List<Informant> resultList = new ArrayList<>();
        for (Informant12 srcInformant : informant12s) {
            final Informant resultInformant = new Informant();
            resultList.add(resultInformant);
            resultInformant.setClient(client);
            resultInformant.setOrganization(client.getOrganization());
            resultInformant.setOrganizationId(client.getOrganizationId());

            final AssignedEntity assignedEntity = srcInformant.getAssignedEntity();
            final RelatedEntity relatedEntity = srcInformant.getRelatedEntity();
            if (CcdParseUtils.hasContent(assignedEntity)) {
                final Person person = personFactory.parse(assignedEntity, client.getOrganization(), "NWHIN_INFORMANT");
                resultInformant.setPerson(person);
                resultInformant.setPersonalRelation(false);
            } else if (CcdParseUtils.hasContent(relatedEntity)) {
                final Person person = personFactory.parse(relatedEntity, client.getOrganization(), "NWHIN_INFORMANT");
                resultInformant.setPerson(person);
                resultInformant.setPersonalRelation(true);
            }
        }

        return resultList;
    }

}
