package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.Authenticator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Name;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.PersonFactory;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdUtils;
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

                org.eclipse.mdht.uml.cda.Person person = CDAFactory.eINSTANCE.createPerson();
                if (CollectionUtils.isNotEmpty(ccdPerson.getNames())) {
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
    public List<Authenticator> parseSection(Client client, Collection<org.eclipse.mdht.uml.cda.Authenticator> authenticators) {
        if (CollectionUtils.isEmpty(authenticators)) {
            return Collections.emptyList();
        }
        checkNotNull(client);

        final List<Authenticator> resultList = new ArrayList<>();
        for (org.eclipse.mdht.uml.cda.Authenticator srcAuthenticator : authenticators) {
            final Authenticator resultAuthenticator = new Authenticator();
            resultList.add(resultAuthenticator);
            resultAuthenticator.setClient(client);
            resultAuthenticator.setOrganization(client.getOrganization());
            resultAuthenticator.setOrganizationId(client.getOrganizationId());

            if (CcdParseUtils.hasContent(srcAuthenticator.getTime())) {
                Date date = CcdParseUtils.parseDate(srcAuthenticator.getTime().getValue());
                resultAuthenticator.setTime(date);
            }

            final AssignedEntity assignedEntity = srcAuthenticator.getAssignedEntity();
            Person person = personFactory.parse(assignedEntity, client.getOrganization(), "NWHIN_AUTHENTICATOR");
            resultAuthenticator.setPerson(person);
        }

        return resultList;
    }

}
