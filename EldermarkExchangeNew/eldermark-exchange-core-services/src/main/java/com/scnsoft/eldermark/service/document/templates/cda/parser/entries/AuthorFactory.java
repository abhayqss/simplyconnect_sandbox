package com.scnsoft.eldermark.service.document.templates.cda.parser.entries;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.ccd.Author;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
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

    public Author parseAuthor(org.eclipse.mdht.uml.cda.Author ccdAuthor, Client client, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdAuthor) || client == null) {
            return null;
        }

        Author author = new Author();
        author.setClient(client);
        author.setOrganization(client.getOrganization());
        author.setLegacyTable(legacyTable);
        author.setLegacyId(0L);

        author.setTime(CcdParseUtils.convertTsToDate(ccdAuthor.getTime()));
        AssignedAuthor ccdAssignedAuthor = ccdAuthor.getAssignedAuthor();
        if (ccdAssignedAuthor.getRepresentedOrganization() != null) {
            Community community = CcdTransform.toCommunity(ccdAssignedAuthor.getRepresentedOrganization(),
                    client.getOrganization(), legacyTable);
            //TODO as for now, organization is set from resident since all nwhin entities will be assigned to single organization
            author.setCommunity(client.getCommunity());
        }
        if (ccdAssignedAuthor.getAssignedPerson() != null) {
            author.setPerson(personFactory.parse(ccdAssignedAuthor, client.getOrganization(), legacyTable));
        }

        return author;
    }

}
