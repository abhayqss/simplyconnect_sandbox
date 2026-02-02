package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.dao.DatabasesDao;
import com.scnsoft.eldermark.entity.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabasesServiceImpl implements DatabasesService {
    private DatabasesDao databasesDao;

    @Value( "${unaffiliated.organization.oid}" )
    private String unaffiliatedOrganizationOid;

    @Override
    public Database getDatabaseById(long id) {
        return databasesDao.getDatabaseById(id);
    }

    @Autowired
    public DatabasesServiceImpl(DatabasesDao databasesDao) {
        this.databasesDao = databasesDao;
    }

    @Override
    public List<Database> getDatabases() {
        return databasesDao.getDatabases();
    }

    @Override
    public List<Database> getDatabasesByIds(List<Long> ids) {
        return databasesDao.getDatabasesByIds(ids);
    }

    @Override
    public Database getDatabaseByCompanyId(String databaseName) {
        return databasesDao.getDatabaseByCompanyId(databaseName);
    }

    @Override
    public Database getDatabaseByAlternativeId(String alternativeId) {
        return databasesDao.getDatabaseByAlternativeId(alternativeId);
    }

    @Override
    public List<Database> getDatabasesByEmployeeLogin(String employeeLogin){
        return databasesDao.getDatabasesByEmployeeLogin (employeeLogin);
    }

    @Override
    public Database getUnaffiliatedDatabase() {
        return databasesDao.getDatabaseByOid(unaffiliatedOrganizationOid);
    }

    @Override
    public boolean isUnaffiliated(Database database) {
        return unaffiliatedOrganizationOid.equals(database.getOid());
    }

}
