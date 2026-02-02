package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.model.target.Author;
import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface ResidentAuthorAssembler {
    Author.Updatable createAuthorUpdatable(Long organizationNewId);

    Author createAuthor(Long organizationNewId, long residentId, long legacyResidentId, long databaseId);
}
