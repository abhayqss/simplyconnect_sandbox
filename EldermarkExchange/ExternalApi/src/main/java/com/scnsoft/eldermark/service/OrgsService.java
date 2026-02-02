package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.web.entity.OrgDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author phomal
 * Created on 1/30/2018.
 */
@Service
@Transactional(readOnly = true)
public class OrgsService {

    private final DatabasesService databasesService;
    private final PrivilegesService privilegesService;

    @Autowired
    public OrgsService(DatabasesService databasesService, PrivilegesService privilegesService) {
        this.databasesService = databasesService;
        this.privilegesService = privilegesService;
    }

    public OrgDto get(Long orgId) {
        if (!privilegesService.canReadOrganization(orgId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        final Database database = databasesService.getDatabaseById(orgId);

        return convert(database);
    }

    public List<OrgDto> listAllAccessible() {
        return convert(privilegesService.listOrganizationsWithReadAccess());
    }

    private List<OrgDto> convert(List<Database> databases) {
        List<OrgDto> dtoList = new ArrayList<>(databases.size());
        for (Database database : databases) {
            dtoList.add(convert(database));
        }
        return dtoList;
    }

    private static OrgDto convert(Database database) {
        final OrgDto dto = new OrgDto();
        dto.setId(database.getId());
        dto.setName(database.getName());
        return dto;
    }

}
