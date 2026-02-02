package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.PersonFactory;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.OptionalTemplateFactory;
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
        ccdLegalAuthenticator.setAssignedEntity(assignedEntity);

        return ccdLegalAuthenticator;
    }

    @Override
    public LegalAuthenticator parseSection(Resident resident, org.eclipse.mdht.uml.cda.LegalAuthenticator srcAuthenticator) {
        if (!CcdParseUtils.hasContent(srcAuthenticator)) {
            return null;
        }
        checkNotNull(resident);

        final LegalAuthenticator resultAuthenticator = new LegalAuthenticator();
        resultAuthenticator.setResident(resident);
        resultAuthenticator.setDatabase(resident.getDatabase());
        resultAuthenticator.setDatabaseId(resident.getDatabaseId());

        if (CcdParseUtils.hasContent(srcAuthenticator.getTime())) {
            Date date = CcdParseUtils.parseDate(srcAuthenticator.getTime().getValue());
            resultAuthenticator.setTime(date);
        }

        final AssignedEntity assignedEntity = srcAuthenticator.getAssignedEntity();
        final Person person = personFactory.parse(assignedEntity, resident.getDatabase(), LEGACY_TABLE);
        resultAuthenticator.setPerson(person);

        resident.setLegalAuthenticator(resultAuthenticator);
        return resultAuthenticator;
    }

}
