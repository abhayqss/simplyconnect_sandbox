package com.scnsoft.eldermark.service.healthpartners.author;

import com.scnsoft.eldermark.dao.AuthorDao;
import com.scnsoft.eldermark.dao.MedicalProfessionalDao;
import com.scnsoft.eldermark.entity.MedicalProfessional;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.document.ccd.Author;
import com.scnsoft.eldermark.service.PersonService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HpMedicalProfessionalAuthorFactoryImplTest {

    @Mock
    private MedicalProfessionalDao medicalProfessionalDao;

    @Mock
    private AuthorDao authorDao;

    @Mock
    private PersonService personService;

    @InjectMocks
    private HpMedicalProfessionalAuthorFactoryImpl instance;

    private void mockMedicalProfessionalDao() {
        doAnswer(invocationOnMock -> {
            var arg = invocationOnMock.<MedicalProfessional>getArgument(0);
            //mock as if dao set id. Needed because legacyId is primitive
            if (arg.getId() == null) {
                arg.setId(1L);
            }
            return arg;
        }).when(medicalProfessionalDao).save(any());
    }

    @Test
    void createAuthorFromSuppliedData() {
        var client = createOrganization();
        var firstName = "firstName";
        var middleName = "middleName";
        var lastName = "lastName";
        var legacyTable = "legacyTable";

        doAnswer(returnsFirstArg()).when(authorDao).save(any());
        when(personService.updateLegacyId(any())).thenReturn(true);

        var author = instance.createAuthor(client, legacyTable, firstName, middleName, lastName);

        verifyAuthor(author, client, legacyTable, firstName, middleName, lastName);
    }

    @Test
    void createsPairFromSuppliedData() {
        var organization = createOrganization();
        var npi = "npi";
        var firstName = "firstName";
        var middleName = "middleName";
        var lastName = "lastName";
        var legacyTable = "legacyTable";

        doAnswer(returnsFirstArg()).when(authorDao).save(any());
        mockMedicalProfessionalDao();
        when(personService.updateLegacyId(any())).thenReturn(true);

        var pair = instance.create(organization, legacyTable, npi, firstName, middleName, lastName);

        verifyAuthor(pair.getFirst(), organization, legacyTable, firstName, middleName, lastName);
        verifyMedicalProfessional(pair.getSecond(), organization, legacyTable, npi, firstName, middleName, lastName);
    }

    private Organization createOrganization() {
        var org = new Organization();
        org.setId(3L);
        return org;
    }


    private void verifyAuthor(Author author, Organization organization, String legacyTable, String firstName, String middleName, String lastName) {
        assertEquals(organization, author.getOrganization());
        assertEquals(legacyTable, author.getLegacyTable());
        assertNull(author.getCommunity());

        verifyPrescriberPerson(author.getPerson(), organization, legacyTable, firstName, middleName, lastName);
    }

    private void verifyMedicalProfessional(MedicalProfessional medicalProfessional,
                                           Organization organization,
                                           String legacyTable,
                                           String npi,
                                           String firstName,
                                           String middleName,
                                           String lastName) {
        assertEquals(organization, medicalProfessional.getOrganization());
        assertEquals(npi, medicalProfessional.getNpi());
        verifyPrescriberPerson(medicalProfessional.getPerson(), organization, legacyTable, firstName, middleName, lastName);
    }

    private void verifyPrescriberPerson(Person person,
                                        Organization organization,
                                        String legacyTable,
                                        String firstName,
                                        String middleName,
                                        String lastName) {
        assertNotNull(person);
        assertEquals(organization, person.getOrganization());
        assertEquals(legacyTable, person.getLegacyTable());

        Assertions.assertThat(person.getNames()).hasSize(1);

        var name = person.getNames().get(0);

        assertEquals(firstName, name.getGiven());
        assertEquals(lastName, name.getFamily());
        assertEquals(middleName, name.getMiddle());
        assertEquals(legacyTable, name.getLegacyTable());
    }
}