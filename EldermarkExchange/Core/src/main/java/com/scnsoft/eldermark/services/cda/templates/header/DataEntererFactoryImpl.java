package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.PersonFactory;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.OptionalTemplateFactory;
import org.eclipse.mdht.uml.cda.AssignedEntity;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.datatypes.II;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Transcriptionist
 * <br/>
 *
 * The {@code dataEnterer} element represents the person who transferred the content, written or dictated by someone else, into the clinical document.
 * The guiding rule of thumb is that an author provides the content found within the header or body of the document,
 * subject to their own interpretation, and the {@code dataEnterer} adds that information to the electronic system.
 * In other words, a {@code dataEnterer} transfers information from one source to another (e.g., transcription from paper form to electronic system).
 * If the {@code dataEnterer} is missing, this role is assumed to be played by the author.
 *
 * @see DataEnterer
 * @see Person
 * @see PersonAddress
 * @see PersonTelecom
 * @see Name
 */
@Component
public class DataEntererFactoryImpl extends OptionalTemplateFactory implements DataEntererFactory {

    @Autowired
    private PersonFactory personFactory;

    @Value("${header.dataEnterer.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public org.eclipse.mdht.uml.cda.DataEnterer buildTemplateInstance(DataEnterer dataEnterer) {
        if (dataEnterer == null) {
            return null;
        }

        org.eclipse.mdht.uml.cda.DataEnterer ccdDataEnterer = CDAFactory.eINSTANCE.createDataEnterer();

        final AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
        final Person ccdPerson = dataEnterer.getPerson();
        if (ccdPerson != null) {
            II id = DatatypesFactory.eINSTANCE.createII();
            if (ccdPerson.getId() != null) {
                id.setRoot("2.16.840.1.113883.4.6");
                id.setExtension(ccdPerson.getId().toString());
            } else {
                id.setNullFlavor(NullFlavor.NI);
            }
            assignedEntity.getIds().add(id);
            if (ccdPerson.getCode() != null) {
                assignedEntity.setCode(CcdUtils.createCE(ccdPerson.getCode(),"2.16.840.1.113883.6.101"));
            }
            if (ccdPerson.getAddresses() != null) {
                for (PersonAddress address : ccdPerson.getAddresses()) {
                    CcdUtils.addConvertedAddress(assignedEntity.getAddrs(), address);
                }
            } else {
                assignedEntity.getAddrs().add(CcdUtils.getNullAddress());
            }
            if (ccdPerson.getTelecoms() != null) {
                for (PersonTelecom telecom : ccdPerson.getTelecoms()) {
                    CcdUtils.addConvertedTelecom(assignedEntity.getTelecoms(), telecom);
                }
            } else {
                assignedEntity.getTelecoms().add(CcdUtils.getNullTelecom());
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
        ccdDataEnterer.setAssignedEntity(assignedEntity);

        return ccdDataEnterer;
    }

    @Override
    public DataEnterer parseSection(Resident resident, org.eclipse.mdht.uml.cda.DataEnterer dataEnterer) {
        if (!CcdParseUtils.hasContent(dataEnterer)) {
            return null;
        }
        checkNotNull(resident);

        final DataEnterer resultDataEnterer = new DataEnterer();
        resultDataEnterer.setResident(resident);
        resultDataEnterer.setDatabase(resident.getDatabase());
        resultDataEnterer.setDatabaseId(resident.getDatabaseId());

        final AssignedEntity assignedEntity = dataEnterer.getAssignedEntity();
        Person person = personFactory.parse(assignedEntity, resident.getDatabase(), "NWHIN_DATA_ENTERER");
        resultDataEnterer.setPerson(person);

        resident.setDataEnterer(resultDataEnterer);
        return resultDataEnterer;
    }

}
