package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.document.ccd.LegalAuthenticator;
import com.scnsoft.eldermark.service.document.templates.cda.factory.entries.CdaSectionEntryFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.PersonFactory;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.mdht.uml.cda.AssignedEntity;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.hl7.datatypes.CS;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.datatypes.II;
import org.eclipse.mdht.uml.hl7.datatypes.TS;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A person who formally signs-off.
 * <br/><br/>
 * The {@code legalAuthenticator} identifies the single person legally responsible for the document and must be present if the document
 * has been legally authenticated. A clinical document that does not contain this element has not been legally authenticated.
 * <br/><br/>
 * The act of legal authentication requires a certain privilege be granted to the legal authenticator depending upon local policy.
 * Based on local practice, clinical documents may be released before legal authentication.
 * <br/><br/>
 * All clinical documents have the potential for legal authentication, given the appropriate credentials.
 * <br/><br/>
 * Local policies MAY choose to delegate the function of legal authentication to a device or system that generates the clinical document.
 * In these cases, the legal authenticator is a person accepting responsibility for the document, not the generating device or system.
 * <br/><br/>
 * Note that the legal authenticator, if present, must be a person.
 *
 * @see LegalAuthenticator
 */
@Component
public class LegalAuthenticatorFactoryImpl extends OptionalTemplateFactory implements LegalAuthenticatorFactory {

    private static CdaSectionEntryFactory cdaSectionEntryFactory = CdaSectionEntryFactory.INSTANCE;

    public static final String LEGACY_TABLE = "NWHIN_LEGAL_AUTHENTICATOR";

    @Value("${header.legalAuthenticator.enabled}")
    private boolean isTemplateIncluded;

    @Autowired
    private PersonFactory personFactory;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public org.eclipse.mdht.uml.cda.LegalAuthenticator buildTemplateInstance(LegalAuthenticator legalAuthenticator) {
        if (legalAuthenticator == null) {
            return null;
        }

        final org.eclipse.mdht.uml.cda.LegalAuthenticator ccdLegalAuthenticator = CDAFactory.eINSTANCE.createLegalAuthenticator();

        final Date time = legalAuthenticator.getTime();
        final TS effectiveTime = DatatypesFactory.eINSTANCE.createTS();
        if (time != null) {
            effectiveTime.setValue(CcdUtils.formatDate(time));
        } else {
            effectiveTime.setNullFlavor(NullFlavor.NI);
        }
        ccdLegalAuthenticator.setTime(effectiveTime);

        final CS signatureCode = DatatypesFactory.eINSTANCE.createCS();
        signatureCode.setCode("S");
        ccdLegalAuthenticator.setSignatureCode(signatureCode);

        final AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
        final Person ccdPerson = legalAuthenticator.getPerson();
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

            CcdUtils.addConvertedAddresses(ccdPerson.getAddresses(), assignedEntity.getAddrs(), false);
            CcdUtils.addConvertedTelecoms(ccdPerson.getTelecoms(), assignedEntity.getTelecoms(), false);

            var person = cdaSectionEntryFactory.buildPerson(ccdPerson);
            assignedEntity.setAssignedPerson(person);
        } else {
            assignedEntity.setNullFlavor(NullFlavor.NI);
        }
        ccdLegalAuthenticator.setAssignedEntity(assignedEntity);

        return ccdLegalAuthenticator;
    }

    @Override
    public LegalAuthenticator parseSection(Client client, org.eclipse.mdht.uml.cda.LegalAuthenticator srcAuthenticator) {
        if (!CcdParseUtils.hasContent(srcAuthenticator)) {
            return null;
        }
        checkNotNull(client);

        final LegalAuthenticator resultAuthenticator = new LegalAuthenticator();
        resultAuthenticator.setClient(client);
        resultAuthenticator.setOrganization(client.getOrganization());
        resultAuthenticator.setOrganizationId(client.getOrganizationId());

        if (CcdParseUtils.hasContent(srcAuthenticator.getTime())) {
            Date date = CcdParseUtils.parseDate(srcAuthenticator.getTime().getValue());
            resultAuthenticator.setTime(date);
        }

        final AssignedEntity assignedEntity = srcAuthenticator.getAssignedEntity();
        final Person person = personFactory.parse(assignedEntity, client.getOrganization(), LEGACY_TABLE);
        resultAuthenticator.setPerson(person);

        client.setLegalAuthenticator(resultAuthenticator);
        return resultAuthenticator;
    }

}
