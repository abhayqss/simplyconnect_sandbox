package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientProgramNoteDao;
import com.scnsoft.eldermark.dao.specification.ClientProgramNoteSpecificationGenerator;
import com.scnsoft.eldermark.entity.note.ClientProgramNote;
import com.scnsoft.eldermark.entity.note.ClientProgramNoteAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientProgramNoteServiceImpl extends BaseNoteService<ClientProgramNote> implements ClientProgramNoteService {

    @Autowired
    private ClientProgramNoteDao clientProgramNoteDao;

    @Autowired
    private ClientProgramNoteSpecificationGenerator clientProgramNoteSpecificationGenerator;

    @Override
    public ClientProgramNote save(ClientProgramNote entity) {
        return clientProgramNoteDao.save(entity);
    }

    @Override
    public ClientProgramNote findById(Long id) {
        return clientProgramNoteDao.findById(id).orElseThrow();
    }

    @Override
    public List<ClientProgramNoteAware> findAll(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var hasAccess = clientProgramNoteSpecificationGenerator.hasAccess(permissionFilter);
        var byCommunities = clientProgramNoteSpecificationGenerator.byCommunities(filter.getAccessibleCommunityIdsAndNames());
        var inProgress = clientProgramNoteSpecificationGenerator.inProgress(filter.getInstantFrom(), filter.getInstantTo());
        return clientProgramNoteDao.findAll(hasAccess.and(byCommunities.and(inProgress)), ClientProgramNoteAware.class);
    }
}
