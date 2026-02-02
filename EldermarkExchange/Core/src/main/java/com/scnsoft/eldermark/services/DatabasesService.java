package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.entity.Database;

import java.util.List;

public interface DatabasesService {
    List<Database> getDatabases();

    Database getDatabaseById(long id);
    List<Database> getDatabasesByIds(List<Long> id);

    Database getDatabaseByCompanyId(String databaseName);

    Database getDatabaseByAlternativeId(String alternativeId);

    List<Database> getDatabasesByEmployeeLogin(String employeeLogin);

    Database getUnaffiliatedDatabase();

    boolean isUnaffiliated(Database database);
}
