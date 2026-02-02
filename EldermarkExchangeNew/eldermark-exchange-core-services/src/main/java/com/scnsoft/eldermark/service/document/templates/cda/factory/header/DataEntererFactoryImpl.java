package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.document.ccd.DataEnterer;
import com.scnsoft.eldermark.service.document.templates.cda.factory.entries.CdaSectionEntryFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.PersonFactory;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.mdht.uml.cda.AssignedEntity;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.hl7.datatypes.II;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Transcriptionist
 * <br/>
 * <p>
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

    private static CdaSectionEntryFactory cdaSectionEntryFactory = CdaSectionEntryFactory.INSTANCE;

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
            II id;
            if (ccdPerson.getId() != null) {
                //todo not npi
                id = CcdUtils.getNpiId(ccdPerson.getId().toString());
            } else {
                id = CcdUtils.getNullId();
            }
            assignedEntity.getIds().add(id);
            if (ccdPerson.getCode() != null) {
                assignedEntity.setCode(CcdUtils.createCE(ccdPerson.getCode(), "2.16.840.1.113883.6.101"));
            }

            CcdUtils.addConvertedAddresses(ccdPerson.getAddresses(), assignedEntity.getAddrs(), true);
            CcdUtils.addConvertedTelecoms(ccdPerson.getTelecoms(), assignedEntity.getTelecoms(), true);

            var person = cdaSectionEntryFactory.buildPerson(ccdPerson);
            assignedEntity.setAssignedPerson(person);
        } else {
            assignedEntity.setNullFlavor(NullFlavor.NI);
        }
        ccdDataEnterer.setAssignedEntity(assignedEntity);

        return ccdDataEnterer;
    }

    @Override
    public DataEnterer parseSection(Client client, org.eclipse.mdht.uml.cda.DataEnterer dataEnterer) {
        if (!CcdParseUtils.hasContent(dataEnterer)) {
            return null;
        }
        checkNotNull(client);

        final DataEnterer resultDataEnterer = new DataEnterer();
        resultDataEnterer.setClient(client);
        resultDataEnterer.setOrganization(client.getOrganization());
        resultDataEnterer.setOrganizationId(client.getOrganizationId());

        final AssignedEntity assignedEntity = dataEnterer.getAssignedEntity();
        Person person = personFactory.parse(assignedEntity, client.getOrganization(), "NWHIN_DATA_ENTERER");
        resultDataEnterer.setPerson(person);

        client.setDataEnterer(resultDataEnterer);
        return resultDataEnterer;
    }

}
