package com.scnsoft.eldermark.service.healthpartners.author;

import com.scnsoft.eldermark.dao.AuthorDao;
import com.scnsoft.eldermark.dao.MedicalProfessionalDao;
import com.scnsoft.eldermark.entity.MedicalProfessional;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.document.ccd.Author;
import com.scnsoft.eldermark.service.PersonService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class HpMedicalProfessionalAuthorFactoryImpl implements HpMedicalProfessionalAuthorFactory {

    @Autowired
    private MedicalProfessionalDao medicalProfessionalDao;

    @Autowired
    private AuthorDao authorDao;

    @Autowired
    private PersonService personService;

    @Override
    @Transactional
    public Author createAuthor(Organization organization, String legacyTable, String firstName, String middleName,
                               String lastName) {
        var person = createPerson(organization, legacyTable, firstName, middleName, lastName);
        return createAuthor(person, organization, legacyTable);
    }

    @Override
    @Transactional
    public Pair<Author, MedicalProfessional> create(Organization organization, String legacyTable, String npi,
                                                    String firstName, String middleName, String lastName) {
        var author = createAuthor(organization, legacyTable, firstName, middleName, lastName);
        var prof = createMedicalProfessional(npi, author.getPerson(), organization);
        return new Pair<>(author, prof);
    }

    private Author createAuthor(Person person, Organization organization, String legacyTable) {
        var author = new Author();
        author.setLegacyId(0);
        author.setLegacyTable(legacyTable);
        author.setOrganization(organization);
        author.setPerson(person);
        author = authorDao.save(author);
        personService.updateLegacyId(author.getPerson());
        return author;
    }

    private Person createPerson(Organization organization, String legacyTable, String firstName, String middleName, String lastName) {
        var person = CareCoordinationUtils.createNewPerson(
                organization,
                legacyTable
        );

        var name = CareCoordinationUtils.createAndAddName(
                person,
                firstName,
                lastName,
                legacyTable);
        name.setMiddle(middleName);

        return person;
    }

    private MedicalProfessional createMedicalProfessional(String npi, Person person, Organization organization) {
        var medicalProfessional = new MedicalProfessional();
        medicalProfessional.setLegacyId(Instant.now().toEpochMilli());
        medicalProfessional.setNpi(npi);
        medicalProfessional.setOrganization(organization);
        medicalProfessional.setPerson(person);

        medicalProfessional = medicalProfessionalDao.save(medicalProfessional);
        medicalProfessional.setLegacyId(medicalProfessional.getId());
        return medicalProfessionalDao.save(medicalProfessional);
    }
}
