package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.careteam.CareTeamMemberModifiedDao;
import com.scnsoft.eldermark.dao.careteam.CareTeamMemberModifiedListReadByEmployeeStatusDao;
import com.scnsoft.eldermark.dao.careteam.CareTeamMemberModifiedReadByEmployeeStatusDao;
import com.scnsoft.eldermark.entity.careteam.CareTeamMemberModificationType;
import com.scnsoft.eldermark.entity.careteam.CareTeamMemberModified;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

@Service
@Transactional
public class ClientCareTeamMemberModifiedServiceImpl implements ClientCareTeamMemberModifiedService {

    @Autowired
    private CareTeamMemberModifiedDao careTeamMemberModifiedDao;

    @Autowired
    private CareTeamMemberModifiedReadByEmployeeStatusDao careTeamMemberModifiedReadByEmployeeStatusDao;

    @Autowired
    private CareTeamMemberModifiedListReadByEmployeeStatusDao careTeamMemberModifiedListReadByEmployeeStatusDao;

    @Override
    public void clientCareTeamMemberModified(ClientCareTeamMember ctm, Long performedById, CareTeamMemberModificationType modificationType) {
        clientCareTeamMemberModified(
                ctm.getId(),
                Optional.ofNullable(ctm.getEmployeeId()).orElseGet(() -> ctm.getEmployee().getId()),
                Optional.ofNullable(ctm.getClientId()).orElseGet(() -> ctm.getClient().getId()),
                performedById,
                modificationType
        );
    }

    @Override
    public void clientCareTeamMemberModified(Long ctmId, Long ctmEmployeeId, Long clientId, Long performedById, CareTeamMemberModificationType modificationType) {
        //don't save for clients which are not associated (+merged)?
        var modified = new CareTeamMemberModified();
        modified.setCareTeamMemberId(ctmId);
        modified.setCtmEmployeeId(ctmEmployeeId);
        modified.setClientId(clientId);
        modified.setModificationType(modificationType);
        modified.setPerformedById(performedById);
        modified.setDateTime(Instant.now());

        careTeamMemberModifiedDao.save(modified);
    }
    @Override
    public void careTeamMemberViewed(Long careTeamMemberId, Long currentEmployeeId) {
        careTeamMemberModifiedReadByEmployeeStatusDao.careTeamMemberViewed(careTeamMemberId, currentEmployeeId);
    }

    @Override
    public void careTeamMemberListViewed(Long currentEmployeeId, Long clientId) {
        careTeamMemberModifiedListReadByEmployeeStatusDao.careTeamMemberListViewed(currentEmployeeId, clientId);
    }

    @Override
    public void setCurrent(Collection<Long> clientCareTeamMemberIds) {
        careTeamMemberModifiedDao.markRemovedByCtmIdsAndType(clientCareTeamMemberIds, CareTeamMemberModificationType.ON_HOLD);
    }
}

