package com.scnsoft.eldermark.service.healthpartners.author;

import com.scnsoft.eldermark.entity.MedicalProfessional;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.ccd.Author;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

public interface HpMedicalProfessionalAuthorFactory {

    Author createAuthor(Organization organization,
                        String legacyTable,
                        String firstName,
                        String middleName,
                        String lastName);

    Pair<Author, MedicalProfessional> create(Organization organization,
                                             String legacyTable,
                                             String npi,
                                             String firstName,
                                             String middleName,
                                             String lastName);

}
