package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.shared.DatabaseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional(readOnly = true)
public class DatabasesFacadeImpl implements DatabasesFacade {
    private DatabasesService databasesService;

    @Autowired
    public DatabasesFacadeImpl(DatabasesService databasesService) {
        this.databasesService = databasesService;
    }

    @Override
    public List<DatabaseDto> getDatabases() {
        List<Database> databases = databasesService.getDatabases();
        return createDtoList(databases);
    }

    @Override
    public DatabaseDto getDatabaseByCompanyId(String databaseName) {
        Database database = databasesService.getDatabaseByCompanyId(databaseName);
        if (database == null) {
            return null;
        }

        DatabaseDto databaseDto = new DatabaseDto();
        databaseDto.setId(database.getId());
        databaseDto.setName(database.getName());

        return databaseDto;
    }

    @Override
    public DatabaseDto getDatabaseById(long databaseId) {
        Database database = databasesService.getDatabaseById(databaseId);
        if (database == null) {
            return null;
        }

        DatabaseDto databaseDto = new DatabaseDto();
        databaseDto.setId(database.getId());
        databaseDto.setName(database.getName());

        return databaseDto;
    }

    private List<DatabaseDto> createDtoList(List<Database> databases) {
        List<DatabaseDto> databaseDtoList = new ArrayList<DatabaseDto>();
        for (Database database : databases) {
            DatabaseDto databaseDto = new DatabaseDto();
            databaseDto.setId(database.getId());
            databaseDto.setName(database.getName());

            databaseDtoList.add(databaseDto);
        }
        return databaseDtoList;
    }
}
