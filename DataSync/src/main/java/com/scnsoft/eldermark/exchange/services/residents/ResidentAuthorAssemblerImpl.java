package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.model.target.Author;
import com.scnsoft.eldermark.exchange.model.target.AuthorType;
import com.scnsoft.eldermark.framework.DatabaseInfo;
import org.springframework.stereotype.Component;

@Component
public class ResidentAuthorAssemblerImpl implements ResidentAuthorAssembler {
    @Override
    public Author.Updatable createAuthorUpdatable(Long organizationNewId) {
        Author.Updatable updatable = new Author.Updatable();
        updatable.setPersonId(null);
        updatable.setTime(null);
        updatable.setOrganizationId(organizationNewId);
        return updatable;
    }

    @Override
    public Author createAuthor(Long organizationNewId, long residentId, long legacyResidentId,
                               long databaseId) {
        Author author = new Author();
        author.setDatabaseId(databaseId);
        author.setLegacyTable(AuthorType.CCD_HEADER.getLegacyTableName());
        author.setLegacyId(legacyResidentId);
        author.setResidentId(residentId);
        author.setUpdatable(createAuthorUpdatable(organizationNewId));
        return author;
    }
}
