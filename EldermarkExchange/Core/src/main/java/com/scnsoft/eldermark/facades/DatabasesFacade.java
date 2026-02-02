package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.shared.DatabaseDto;

import java.util.List;

public interface DatabasesFacade {
    List<DatabaseDto> getDatabases();

    DatabaseDto getDatabaseByCompanyId(String loginCompanyId);

    DatabaseDto getDatabaseById(long databaseId);
}
