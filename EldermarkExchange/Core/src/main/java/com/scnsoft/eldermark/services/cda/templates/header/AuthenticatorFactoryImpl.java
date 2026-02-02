package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.PersonFactory;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.OptionalTemplateFactory;
import org.apache.commons.collections.CollectionUtils;
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

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Attests to accuracy, but no legal standing
 *
 * @see Authenticator
 */
@Component
public class AuthenticatorFactoryImpl extends OptionalTemplateFactory implements AuthenticatorFactory {

    @Value("${header.authenticators.enabled}")
    private boolean isTemplateIncluded;

    @Autowired
    private PersonFactory personFactory;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public List<org.eclipse.mdht.uml.cda.Authenticator> buildTemplateInstance(Collection<Authenticator> authenticators) {
        final List<org.eclipse.mdht.uml.cda.Authenticator> ccdAuthenticators = new ArrayList<>();

        for (com.scnsoft.eldermark.entity.Authenticator authenticator : authenticators) {
            org.eclipse.mdht.uml.cda.Authenticator ccdAuthenticator = CDAFactory.eINSTANCE.createAuthenticator();

            Date time = authenticator.getTime();
            TS effectiveTime = DatatypesFactory.eINSTANCE.createTS();
            if (time != null) {
                effectiveTime.setValue(CcdUtils.formatDate(time));
            } else {
                effectiveTime.setNullFlavor(NullFlavor.NI);
            }
            ccdAuthenticator.setTime(effectiveTime);

            CS signatureCode = DatatypesFactory.eINSTANCE.createCS();
            signatureCode.setCode("S");
            ccdAuthenticator.setSignatureCode(signatureCode);

            AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
            Person ccdPerson = authenticator.getPerson();
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
                    person.setNullFlavor(NullFlavor.NI);
                }
                assignedEntity.setAssignedPerson(person);
            } else {
                assignedEntity.setNullFlavor(NullFlavor.NI);
            }
            ccdAuthenticator.setAssignedEntity(assignedEntity);

            ccdAuthenticators.add(ccdAuthenticator);
        }

        return ccdAuthenticators;
    }

    @Override
    public List<Authenticator> parseSection(Resident resident, Collection<org.eclipse.mdht.uml.cda.Authenticator> authenticators) {
        if (CollectionUtils.isEmpty(authenticators)) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final List<Authenticator> resultList = new ArrayList<>();
        for (org.eclipse.mdht.uml.cda.Authenticator srcAuthenticator : authenticators) {
            final Authenticator resultAuthenticator = new Authenticator();
            resultList.add(resultAuthenticator);
            resultAuthenticator.setResident(resident);
            resultAuthenticator.setDatabase(resident.getDatabase());
            resultAuthenticator.setDatabaseId(resident.getDatabaseId());

            if (CcdParseUtils.hasContent(srcAuthenticator.getTime())) {
                Date date = CcdParseUtils.parseDate(srcAuthenticator.getTime().getValue());
                resultAuthenticator.setTime(date);
            }

            final AssignedEntity assignedEntity = srcAuthenticator.getAssignedEntity();
            Person person = personFactory.parse(assignedEntity, resident.getDatabase(), "NWHIN_AUTHENTICATOR" );
            resultAuthenticator.setPerson(person);
        }

        return resultList;
    }

}
