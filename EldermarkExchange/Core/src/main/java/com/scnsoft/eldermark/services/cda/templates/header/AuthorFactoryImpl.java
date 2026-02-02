package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.PersonFactory;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.RequiredTemplateFactory;
import org.eclipse.mdht.uml.cda.AssignedAuthor;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.datatypes.II;
import org.eclipse.mdht.uml.hl7.datatypes.ON;
import org.eclipse.mdht.uml.hl7.datatypes.TS;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A person/machine that created document content
 *
 * @see Author
 */
@Component
public class AuthorFactoryImpl extends RequiredTemplateFactory implements AuthorFactory {

    private static final String LEGACY_TABLE = "NWHIN_AUTHOR";

    @Autowired
    private PersonFactory personFactory;

    @Override
    public List<org.eclipse.mdht.uml.cda.Author> buildTemplateInstance(Collection<com.scnsoft.eldermark.entity.Author> authors) {
        final List<org.eclipse.mdht.uml.cda.Author> ccdAuthors = new ArrayList<>();
        for (com.scnsoft.eldermark.entity.Author author : authors) {
            ccdAuthors.add(buildAuthor(author));
        }
        return ccdAuthors;
    }

    public org.eclipse.mdht.uml.cda.Author buildAuthor(com.scnsoft.eldermark.entity.Author author) {
        org.eclipse.mdht.uml.cda.Author ccdAuthor = CDAFactory.eINSTANCE.createAuthor();

        Date time = author.getTime();
        TS effectiveTime = DatatypesFactory.eINSTANCE.createTS();
        if (time != null) {
            effectiveTime.setValue(CcdUtils.formatDate(time));
        } else {
            effectiveTime.setNullFlavor(NullFlavor.NI);
        }
        ccdAuthor.setTime(effectiveTime);

        Organization ccdOrganization = author.getOrganization();
        Person ccdPerson = author.getPerson();

        AssignedAuthor assignedAuthor = CDAFactory.eINSTANCE.createAssignedAuthor();
        if (ccdOrganization != null) {

            II id = DatatypesFactory.eINSTANCE.createII();
            id.setNullFlavor(NullFlavor.NA);
            assignedAuthor.getIds().add(id);

            if (ccdOrganization.getAddresses() != null) {
                for (OrganizationAddress address : ccdOrganization.getAddresses()) {
                    CcdUtils.addConvertedAddress(assignedAuthor.getAddrs(), address);
                }
            } else {
                CcdUtils.addConvertedAddress(assignedAuthor.getAddrs(), null);
            }
            if(ccdOrganization.getTelecom() != null) {
                CcdUtils.addConvertedTelecom(assignedAuthor.getTelecoms(), ccdOrganization.getTelecom());
            } else {
                assignedAuthor.getTelecoms().add(CcdUtils.getNullTelecom());
            }
            org.eclipse.mdht.uml.cda.Organization organization = CDAFactory.eINSTANCE.createOrganization();
            ON on = DatatypesFactory.eINSTANCE.createON();
            if (ccdOrganization.getName() != null) {
                on.addText(ccdOrganization.getName());
            } else {
                on.setNullFlavor(NullFlavor.NI);
            }
            organization.getNames().add(on);
            assignedAuthor.setRepresentedOrganization(organization);
        } else if (ccdPerson != null) {
            II id = DatatypesFactory.eINSTANCE.createII();
            if (ccdPerson.getId() != null) {
                id.setRoot("2.16.840.1.113883.4.6");
                id.setExtension(ccdPerson.getId().toString());
            } else {
                id.setNullFlavor(NullFlavor.NI);
            }
            assignedAuthor.getIds().add(id);
            if (ccdPerson.getCode() != null) {
                assignedAuthor.setCode(CcdUtils.createCE(ccdPerson.getCode(),"2.16.840.1.113883.6.101"));
            }
            if (ccdPerson.getAddresses() != null) {
                for (PersonAddress address : ccdPerson.getAddresses()) {
                    CcdUtils.addConvertedAddress(assignedAuthor.getAddrs(), address);
                }
            } else {
                assignedAuthor.getAddrs().add(CcdUtils.getNullAddress());
            }
            if (ccdPerson.getTelecoms() != null) {
                for (PersonTelecom telecom : ccdPerson.getTelecoms()) {
                    CcdUtils.addConvertedTelecom(assignedAuthor.getTelecoms(), telecom);
                }
            } else {
                assignedAuthor.getTelecoms().add(CcdUtils.getNullTelecom());
            }
            org.eclipse.mdht.uml.cda.Person person = CDAFactory.eINSTANCE.createPerson();
            if (ccdPerson.getNames() != null) {
                for (Name name : ccdPerson.getNames()) {
                    CcdUtils.addConvertedName(person.getNames(), name);
                }
            } else {
                person.getNames().add(CcdUtils.getNullName());
            }
            assignedAuthor.setAssignedPerson(person);
        } else {
            assignedAuthor.setNullFlavor(NullFlavor.NI);
        }
        ccdAuthor.setAssignedAuthor(assignedAuthor);

        return ccdAuthor;
    }

    @Override
    public List<Author> parseSection(Resident resident, Collection<org.eclipse.mdht.uml.cda.Author> authors) {
        if (CollectionUtils.isEmpty(authors)) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        List<Author> resultList = new ArrayList<>();
        for (org.eclipse.mdht.uml.cda.Author srcAuthor : authors) {
            final Author resultAuthor = new Author();
            resultList.add(resultAuthor);
            resultAuthor.setResident(resident);
            resultAuthor.setDatabase(resident.getDatabase());
            resultAuthor.setDatabaseId(resident.getDatabaseId());

            resultAuthor.setLegacyId(0L);
            resultAuthor.setLegacyTable(LEGACY_TABLE);

            if (CcdParseUtils.hasContent(srcAuthor.getTime())) {
                Date date = CcdParseUtils.parseDate(srcAuthor.getTime().getValue());
                resultAuthor.setTime(date);
            }

            final AssignedAuthor assignedEntity = srcAuthor.getAssignedAuthor();
            Person person = personFactory.parse(assignedEntity, resident.getDatabase(), LEGACY_TABLE);
            resultAuthor.setPerson(person);

            final org.eclipse.mdht.uml.cda.Organization srcOrg = assignedEntity.getRepresentedOrganization();
            if (srcOrg != null) {
                Organization org = CcdTransform.toOrganization(srcOrg, resident.getDatabase(), LEGACY_TABLE);
                // TODO added default organization
                resultAuthor.setOrganization(resident.getFacility());
            }
        }

        resident.getAuthors().addAll(resultList);
        return resultList;
    }

}
