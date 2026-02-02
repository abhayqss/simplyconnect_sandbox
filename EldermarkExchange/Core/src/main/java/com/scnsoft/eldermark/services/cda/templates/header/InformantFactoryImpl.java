package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.PersonFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.templates.OptionalTemplateFactory;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.mdht.uml.cda.AssignedEntity;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.Informant12;
import org.eclipse.mdht.uml.cda.RelatedEntity;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
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
                        relatedEntity.setCode(CcdUtils.createCE(ccdPerson.getCode(),"2.16.840.1.113883.6.101"));
                    }
                    if (ccdPerson.getAddresses() != null) {
                        for (PersonAddress address : ccdPerson.getAddresses()) {
                            CcdUtils.addConvertedAddress(relatedEntity.getAddrs(), address);
                        }
                    }
                    if (ccdPerson.getTelecoms() != null) {
                        for (PersonTelecom telecom : ccdPerson.getTelecoms()) {
                            CcdUtils.addConvertedTelecom(relatedEntity.getTelecoms(), telecom);
                        }
                    }
                    org.eclipse.mdht.uml.cda.Person person = CDAFactory.eINSTANCE.createPerson();
                    if (ccdPerson.getNames() != null) {
                        for (Name name : ccdPerson.getNames()) {
                            CcdUtils.addConvertedName(person.getNames(), name);
                        }
                    } else {
                        person.getNames().add(CcdUtils.getNullName());
                    }
                    relatedEntity.setRelatedPerson(person);
                } else {
                    relatedEntity.setNullFlavor(NullFlavor.NI);
                }
                informant12.setRelatedEntity(relatedEntity);
            } else {
                final AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
                final Person ccdPerson = informant.getPerson();
                if (ccdPerson != null) {
                    if (ccdPerson.getId() != null) {
                        II id = DatatypesFactory.eINSTANCE.createII();
                        id.setRoot("2.16.840.1.113883.4.6");
                        id.setExtension(ccdPerson.getId().toString());
                        assignedEntity.getIds().add(id);
                    }
                    if (ccdPerson.getCode() != null) {
                        assignedEntity.setCode(CcdUtils.createCE(ccdPerson.getCode(), "2.16.840.1.113883.6.101"));
                    }
                    if (ccdPerson.getAddresses() != null) {
                        for (PersonAddress address : ccdPerson.getAddresses()) {
                            CcdUtils.addConvertedAddress(assignedEntity.getAddrs(), address);
                        }
                    }
                    if (ccdPerson.getTelecoms() != null) {
                        for (PersonTelecom telecom : ccdPerson.getTelecoms()) {
                            CcdUtils.addConvertedTelecom(assignedEntity.getTelecoms(), telecom);
                        }
                    }
                    org.eclipse.mdht.uml.cda.Person person = CDAFactory.eINSTANCE.createPerson();
                    if (ccdPerson.getNames() != null) {
                        for (Name name : ccdPerson.getNames()) {
                            CcdUtils.addConvertedName(person.getNames(), name);
                        }
                    } else {
                        person.getNames().add(CcdUtils.getNullName());
                    }
                    assignedEntity.setAssignedPerson(person);
                } else {
                    assignedEntity.setNullFlavor(NullFlavor.NI);
                }
                informant12.setAssignedEntity(assignedEntity);
            }
            ccdInformants.add(informant12);
        }

        return ccdInformants;
    }

    @Override
    public List<Informant> parseSection(Resident resident, Collection<Informant12> informant12s) {
        if (CollectionUtils.isEmpty(informant12s)) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final List<Informant> resultList = new ArrayList<>();
        for (Informant12 srcInformant : informant12s) {
            final Informant resultInformant = new Informant();
            resultList.add(resultInformant);
            resultInformant.setResident(resident);
            resultInformant.setDatabase(resident.getDatabase());
            resultInformant.setDatabaseId(resident.getDatabaseId());

            final AssignedEntity assignedEntity = srcInformant.getAssignedEntity();
            final RelatedEntity relatedEntity = srcInformant.getRelatedEntity();
            if (CcdParseUtils.hasContent(assignedEntity)) {
                final Person person = personFactory.parse(assignedEntity, resident.getDatabase(), "NWHIN_INFORMANT");
                resultInformant.setPerson(person);
                resultInformant.setPersonalRelation(false);
            } else if (CcdParseUtils.hasContent(relatedEntity)) {
                final Person person = personFactory.parse(relatedEntity, resident.getDatabase(), "NWHIN_INFORMANT");
                resultInformant.setPerson(person);
                resultInformant.setPersonalRelation(true);
            }
        }

        return resultList;
    }

}
