package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

import java.util.List;

public interface DatabasesDao {
    List<Database> getDatabases();

    Database getDatabaseById(Long id);
    List<Database> getDatabasesByIds(List<Long> id);

    /**
     * Finds a database by system_setup.login_company_id. Since it is unique, only one database with given name should exist.
     *
     * @param loginCompanyId the name of the database
     * @return database (not null) or null if database not found
     * @throws com.scnsoft.eldermark.dao.exceptions.MultipleEntitiesFoundException
     *          if more than one database with given name exists
     */
    Database getDatabaseByCompanyId(String loginCompanyId);
    Database getDatabaseByAlternativeId(String databaseAlternativeId);

    List<Database> getDatabasesByEmployeeLogin(String employeeLogin);

    void update(Database database);

    Database getDatabaseByOid(String oid);

    Pair<String, String> getDatabaseLogos(long id);
}
