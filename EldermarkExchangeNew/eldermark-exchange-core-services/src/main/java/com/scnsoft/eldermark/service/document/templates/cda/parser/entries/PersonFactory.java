package com.scnsoft.eldermark.service.document.templates.cda.parser.entries;

import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.AD;
import org.eclipse.mdht.uml.hl7.datatypes.PN;
import org.eclipse.mdht.uml.hl7.datatypes.TEL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersonFactory {

    private final CcdCodeFactory ccdCodeFactory;

    @Autowired
    public PersonFactory(CcdCodeFactory ccdCodeFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
    }

    public Person parse(AssignedEntity entity, Organization organization, String legacyTable) {
        if (!CcdParseUtils.hasContent(entity) || !CcdParseUtils.hasContent(entity.getAssignedPerson())) {
            return null;
        }

        org.eclipse.mdht.uml.cda.Person ccdPatient = entity.getAssignedPerson();
        EList<PN> names = ccdPatient.getNames();
        EList<AD> addresses = entity.getAddrs();
        EList<TEL> telecoms = entity.getTelecoms();
        String legacyId = CcdParseUtils.getFirstIdExtensionStr(entity.getIds());

        final Person person = CcdParseUtils.createPerson(names, addresses, telecoms, organization, legacyTable,
                legacyId);
        if (CcdParseUtils.hasContent(entity.getCode())) {
            person.setCode(ccdCodeFactory.convert(entity.getCode()));
        }
        return person;
    }

    public Person parse(RelatedEntity entity, Organization organization, String legacyTable) {
        if (!CcdParseUtils.hasContent(entity) || !CcdParseUtils.hasContent(entity.getRelatedPerson())) {
            return null;
        }

        org.eclipse.mdht.uml.cda.Person ccdPatient = entity.getRelatedPerson();
        EList<PN> names = ccdPatient.getNames();
        EList<AD> addresses = entity.getAddrs();
        EList<TEL> telecoms = entity.getTelecoms();

        // TODO test null legacyId
        final Person person = CcdParseUtils.createPerson(names, addresses, telecoms, organization, legacyTable, null);
        if (CcdParseUtils.hasContent(entity.getCode())) {
            person.setCode(ccdCodeFactory.convert(entity.getCode()));
        }
        return person;
    }

    public Person parse(AssociatedEntity entity, Organization organization, String legacyTable) {
        if (!CcdParseUtils.hasContent(entity) || !CcdParseUtils.hasContent(entity.getAssociatedPerson())) {
            return null;
        }

        org.eclipse.mdht.uml.cda.Person ccdPatient = entity.getAssociatedPerson();
        EList<PN> names = ccdPatient.getNames();
        EList<AD> addresses = entity.getAddrs();
        EList<TEL> telecoms = entity.getTelecoms();
        String legacyId = CcdParseUtils.getFirstIdExtensionStr(entity.getIds());

        final Person person = CcdParseUtils.createPerson(names, addresses, telecoms, organization, legacyTable,
                legacyId);
        if (CcdParseUtils.hasContent(entity.getCode())) {
            person.setCode(ccdCodeFactory.convert(entity.getCode()));
        }
        return person;
    }

    public Person parse(AssignedAuthor entity, Organization organization, String legacyTable) {
        if (!CcdParseUtils.hasContent(entity) || !CcdParseUtils.hasContent(entity.getAssignedPerson())) {
            return null;
        }

        org.eclipse.mdht.uml.cda.Person ccdPatient = entity.getAssignedPerson();
        EList<PN> names = ccdPatient.getNames();
        EList<AD> addresses = entity.getAddrs();
        EList<TEL> telecoms = entity.getTelecoms();
        String legacyId = CcdParseUtils.getFirstIdExtensionStr(entity.getIds());

        final Person person = CcdParseUtils.createPerson(names, addresses, telecoms, organization, legacyTable,
                legacyId);
        if (CcdParseUtils.hasContent(entity.getCode())) {
            person.setCode(ccdCodeFactory.convert(entity.getCode()));
        }
        return person;
    }

    public static Person parse(IntendedRecipient entity, Organization organization, String legacyTable) {
        if (!CcdParseUtils.hasContent(entity) || !CcdParseUtils.hasContent(entity.getInformationRecipient())) {
            return null;
        }

        org.eclipse.mdht.uml.cda.Person ccdPatient = entity.getInformationRecipient();
        EList<PN> names = ccdPatient.getNames();
        EList<AD> addresses = entity.getAddrs();
        EList<TEL> telecoms = entity.getTelecoms();
        String legacyId = CcdParseUtils.getFirstIdExtensionStr(entity.getIds());

        final Person person = CcdParseUtils.createPerson(names, addresses, telecoms, organization, legacyTable,
                legacyId);
        return person;
    }

    public static Person parse(PatientRole entity, Organization organization, String legacyTable) {
        if (!CcdParseUtils.hasContent(entity) || !CcdParseUtils.hasContent(entity.getPatient())) {
            return null;
        }

        Patient ccdPatient = entity.getPatient();
        EList<PN> names = ccdPatient.getNames();
        EList<AD> addresses = entity.getAddrs();
        EList<TEL> telecoms = entity.getTelecoms();
        String legacyId = CcdParseUtils.getFirstIdExtensionStr(entity.getIds());

        final Person person = CcdParseUtils.createPerson(names, addresses, telecoms, organization, legacyTable,
                legacyId);
        return person;
    }
}
