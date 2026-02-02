package com.scnsoft.eldermark.services.cda.templates.sections.entries;

import com.scnsoft.eldermark.entity.Author;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import org.eclipse.mdht.uml.cda.AssignedAuthor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author phomal
 * Created on 4/13/2018.
 */
@Component
public class AuthorFactory {

    private final PersonFactory personFactory;

    @Autowired
    public AuthorFactory(PersonFactory personFactory) {
        this.personFactory = personFactory;
    }

    public Author parseAuthor(org.eclipse.mdht.uml.cda.Author ccdAuthor, Resident resident, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdAuthor) || resident == null) {
            return null;
        }

        Author author = new Author();
        author.setResident(resident);
        author.setDatabase(resident.getDatabase());
        author.setLegacyTable(legacyTable);
        author.setLegacyId(0L);

        author.setTime(CcdParseUtils.convertTsToDate(ccdAuthor.getTime()));
        AssignedAuthor ccdAssignedAuthor = ccdAuthor.getAssignedAuthor();
        if (ccdAssignedAuthor.getRepresentedOrganization() != null) {
            Organization org = CcdTransform.toOrganization(ccdAssignedAuthor.getRepresentedOrganization(),
                    resident.getDatabase(), legacyTable);
            //TODO as for now, organization is set from resident since all nwhin entities will be assigned to single organization
            author.setOrganization(resident.getFacility());
        }
        if (ccdAssignedAuthor.getAssignedPerson() != null) {
            author.setPerson(personFactory.parse(ccdAssignedAuthor, resident.getDatabase(), legacyTable));
        }

        return author;
    }

}
