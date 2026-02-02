package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.basic.Telecom;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.ccd.Author;
import com.scnsoft.eldermark.entity.document.ccd.CdaConstants;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.service.document.templates.cda.factory.entries.CdaSectionEntryFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.RequiredTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.PersonFactory;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.mdht.uml.cda.AssignedAuthor;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.datatypes.II;
import org.eclipse.mdht.uml.hl7.datatypes.TS;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    private static CdaSectionEntryFactory cdaSectionEntryFactory = CdaSectionEntryFactory.INSTANCE;

    @Autowired
    private PersonFactory personFactory;

    @Override
    public List<org.eclipse.mdht.uml.cda.Author> buildTemplateInstance(Collection<com.scnsoft.eldermark.entity.document.ccd.Author> authors) {
        final List<org.eclipse.mdht.uml.cda.Author> ccdAuthors = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(authors)) {
            for (com.scnsoft.eldermark.entity.document.ccd.Author author : authors) {
                ccdAuthors.add(buildAuthor(author));
            }
        } else {
            ccdAuthors.add(buildNullAuthor());
        }

        return ccdAuthors;
    }

    public org.eclipse.mdht.uml.cda.Author buildNullAuthor() {
        org.eclipse.mdht.uml.cda.Author ccdAuthor = CDAFactory.eINSTANCE.createAuthor();

        TS effectiveTime = DatatypesFactory.eINSTANCE.createTS();
        effectiveTime.setValue(CcdUtils.formatDate(new Date()));
        ccdAuthor.setTime(effectiveTime);

        AssignedAuthor assignedAuthor = CDAFactory.eINSTANCE.createAssignedAuthor();

        II id = DatatypesFactory.eINSTANCE.createII();
        id.setRoot(CdaConstants.US_NPI_ROOT);
        assignedAuthor.getIds().add(id);

        assignedAuthor.setNullFlavor(NullFlavor.NI);
        ccdAuthor.setAssignedAuthor(assignedAuthor);

        assignedAuthor.getAddrs().add(CcdUtils.getNullAddress());
        assignedAuthor.getTelecoms().add(CcdUtils.getNullTelecom());
        org.eclipse.mdht.uml.cda.Person person = CDAFactory.eINSTANCE.createPerson();
        person.getNames().add(CcdUtils.getNullName());
        assignedAuthor.setAssignedPerson(person);

        return ccdAuthor;
    }

    public org.eclipse.mdht.uml.cda.Author buildAuthor(com.scnsoft.eldermark.entity.document.ccd.Author author) {
        org.eclipse.mdht.uml.cda.Author ccdAuthor = CDAFactory.eINSTANCE.createAuthor();

        Date time = author.getTime();
        TS effectiveTime = DatatypesFactory.eINSTANCE.createTS();
        if (time != null) {
            effectiveTime.setValue(CcdUtils.formatSimpleDate(time));
        } else {
            effectiveTime.setValue(CcdUtils.formatDate(new Date()));
        }
        ccdAuthor.setTime(effectiveTime);

        Community ccdCommunity = author.getCommunity();
        Person ccdPerson = author.getPerson();
        List<? extends Address> addresses = null;
        List<? extends Telecom> telecoms = null;

        boolean noData = true;
        AssignedAuthor assignedAuthor = CDAFactory.eINSTANCE.createAssignedAuthor();

        if (ccdPerson != null) {
            noData = false;

            var npi = DatatypesFactory.eINSTANCE.createII();
            npi.setRoot(CdaConstants.US_NPI_ROOT);
            assignedAuthor.getIds().add(npi);

            if (ccdPerson.getId() != null) {
                //fake npi, because [1..1] id allowed only with npi root
                npi.setExtension(ccdPerson.getId().toString());
                npi.setAssigningAuthorityName("Simply Connect Id");
            }

            if (ccdPerson.getCode() != null) {
                assignedAuthor.setCode(CcdUtils.createCE(ccdPerson.getCode(), CodeSystem.NUCC_PROVIDER_CODES.getOid()));
            }

            var assignedPerson = cdaSectionEntryFactory.buildPerson(ccdPerson);
            assignedAuthor.setAssignedPerson(assignedPerson);

            addresses = ccdPerson.getAddresses();
            telecoms = ccdPerson.getTelecoms();
        } else {
            var device = cdaSectionEntryFactory.buildNullAuthoringDevice();
            assignedAuthor.setAssignedAuthoringDevice(device);

            if (ccdCommunity != null) {
                noData = false;

                II id = DatatypesFactory.eINSTANCE.createII();
                id.setRoot(CdaConstants.US_NPI_ROOT);
                assignedAuthor.getIds().add(id);

                var organization = cdaSectionEntryFactory.buildOrganization(ccdCommunity, false);
                assignedAuthor.setRepresentedOrganization(organization);

                addresses = ccdCommunity.getAddresses();
                telecoms = CareCoordinationUtils.wrapIfNonNull(ccdCommunity.getTelecom());
            }
        }

        if (!noData) {
            CcdUtils.addConvertedAddresses(addresses, assignedAuthor.getAddrs(), true);
            CcdUtils.addConvertedTelecoms(telecoms, assignedAuthor.getTelecoms(), true);
        }
        ccdAuthor.setAssignedAuthor(assignedAuthor);

        return ccdAuthor;
    }

    @Override
    public List<Author> parseSection(Client client, Collection<org.eclipse.mdht.uml.cda.Author> authors) {
        if (CollectionUtils.isEmpty(authors)) {
            return Collections.emptyList();
        }
        checkNotNull(client);

        List<Author> resultList = new ArrayList<>();
        for (org.eclipse.mdht.uml.cda.Author srcAuthor : authors) {
            final Author resultAuthor = new Author();
            resultList.add(resultAuthor);
            resultAuthor.setClient(client);
            resultAuthor.setOrganization(client.getOrganization());
            resultAuthor.setOrganizationId(client.getOrganizationId());

            resultAuthor.setLegacyId(0L);
            resultAuthor.setLegacyTable(LEGACY_TABLE);

            if (CcdParseUtils.hasContent(srcAuthor.getTime())) {
                Date date = CcdParseUtils.parseDate(srcAuthor.getTime().getValue());
                resultAuthor.setTime(date);
            }

            final AssignedAuthor assignedEntity = srcAuthor.getAssignedAuthor();
            Person person = personFactory.parse(assignedEntity, client.getOrganization(), LEGACY_TABLE);
            resultAuthor.setPerson(person);

            final org.eclipse.mdht.uml.cda.Organization srcOrg = assignedEntity.getRepresentedOrganization();
            if (srcOrg != null) {
                Community org = CcdTransform.toCommunity(srcOrg, client.getOrganization(), LEGACY_TABLE);
                // TODO added default organization
                resultAuthor.setCommunity(client.getCommunity());
            }
        }

        if (client.getAuthors() == null) {
            client.setAuthors(new ArrayList<>());
        }
        client.getAuthors().addAll(resultList);
        return resultList;
    }

}
