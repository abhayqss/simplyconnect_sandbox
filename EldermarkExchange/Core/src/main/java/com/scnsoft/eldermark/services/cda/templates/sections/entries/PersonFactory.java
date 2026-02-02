package com.scnsoft.eldermark.services.cda.templates.sections.entries;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.AD;
import org.eclipse.mdht.uml.hl7.datatypes.PN;
import org.eclipse.mdht.uml.hl7.datatypes.TEL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author phomal
 * Created on 5/17/2018.
 */
@Component
public class PersonFactory {

    private final CcdCodeFactory ccdCodeFactory;

    @Autowired
    public PersonFactory(CcdCodeFactory ccdCodeFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
    }

    public Person parse(AssignedEntity entity, Database database, String legacyTable) {
        if (!CcdParseUtils.hasContent(entity) || !CcdParseUtils.hasContent(entity.getAssignedPerson())) {
            return null;
        }

        org.eclipse.mdht.uml.cda.Person ccdPatient = entity.getAssignedPerson();
        EList<PN> names = ccdPatient.getNames();
        EList<AD> addresses = entity.getAddrs();
        EList<TEL> telecoms = entity.getTelecoms();
        String legacyId = CcdParseUtils.getFirstIdExtensionStr(entity.getIds());

        final Person person = CcdParseUtils.createPerson(names, addresses, telecoms, database, legacyTable, legacyId);
        if (CcdParseUtils.hasContent(entity.getCode())) {
            person.setCode(ccdCodeFactory.convert(entity.getCode()));
        }
        return person;
    }

    public Person parse(RelatedEntity entity, Database database, String legacyTable) {
        if (!CcdParseUtils.hasContent(entity) || !CcdParseUtils.hasContent(entity.getRelatedPerson())) {
            return null;
        }

        org.eclipse.mdht.uml.cda.Person ccdPatient = entity.getRelatedPerson();
        EList<PN> names = ccdPatient.getNames();
        EList<AD> addresses = entity.getAddrs();
        EList<TEL> telecoms = entity.getTelecoms();

        // TODO test null legacyId
        final Person person = CcdParseUtils.createPerson(names, addresses, telecoms, database, legacyTable, null);
        if (CcdParseUtils.hasContent(entity.getCode())) {
            person.setCode(ccdCodeFactory.convert(entity.getCode()));
        }
        return person;
    }

    public Person parse(AssociatedEntity entity, Database database, String legacyTable) {
        if (!CcdParseUtils.hasContent(entity) || !CcdParseUtils.hasContent(entity.getAssociatedPerson())) {
            return null;
        }

        org.eclipse.mdht.uml.cda.Person ccdPatient = entity.getAssociatedPerson();
        EList<PN> names = ccdPatient.getNames();
        EList<AD> addresses = entity.getAddrs();
        EList<TEL> telecoms = entity.getTelecoms();
        String legacyId = CcdParseUtils.getFirstIdExtensionStr(entity.getIds());

        final Person person = CcdParseUtils.createPerson(names, addresses, telecoms, database, legacyTable, legacyId);
        if (CcdParseUtils.hasContent(entity.getCode())) {
            person.setCode(ccdCodeFactory.convert(entity.getCode()));
        }
        return person;
    }

    public Person parse(AssignedAuthor entity, Database database, String legacyTable) {
        if (!CcdParseUtils.hasContent(entity) || !CcdParseUtils.hasContent(entity.getAssignedPerson())) {
            return null;
        }

        org.eclipse.mdht.uml.cda.Person ccdPatient = entity.getAssignedPerson();
        EList<PN> names = ccdPatient.getNames();
        EList<AD> addresses = entity.getAddrs();
        EList<TEL> telecoms = entity.getTelecoms();
        String legacyId = CcdParseUtils.getFirstIdExtensionStr(entity.getIds());

        final Person person = CcdParseUtils.createPerson(names, addresses, telecoms, database, legacyTable, legacyId);
        if (CcdParseUtils.hasContent(entity.getCode())) {
            person.setCode(ccdCodeFactory.convert(entity.getCode()));
        }
        return person;
    }


    public static Person parse(IntendedRecipient entity, Database database, String legacyTable) {
        if (!CcdParseUtils.hasContent(entity) || !CcdParseUtils.hasContent(entity.getInformationRecipient())) {
            return null;
        }

        org.eclipse.mdht.uml.cda.Person ccdPatient = entity.getInformationRecipient();
        EList<PN> names = ccdPatient.getNames();
        EList<AD> addresses = entity.getAddrs();
        EList<TEL> telecoms = entity.getTelecoms();
        String legacyId = CcdParseUtils.getFirstIdExtensionStr(entity.getIds());

        final Person person = CcdParseUtils.createPerson(names, addresses, telecoms, database, legacyTable, legacyId);
        return person;
    }

    public static Person parse(PatientRole entity, Database database, String legacyTable) {
        if (!CcdParseUtils.hasContent(entity) || !CcdParseUtils.hasContent(entity.getPatient())) {
            return null;
        }

        Patient ccdPatient = entity.getPatient();
        EList<PN> names = ccdPatient.getNames();
        EList<AD> addresses = entity.getAddrs();
        EList<TEL> telecoms = entity.getTelecoms();
        String legacyId = CcdParseUtils.getFirstIdExtensionStr(entity.getIds());

        final Person person = CcdParseUtils.createPerson(names, addresses, telecoms, database, legacyTable, legacyId);
        return person;
    }

}
