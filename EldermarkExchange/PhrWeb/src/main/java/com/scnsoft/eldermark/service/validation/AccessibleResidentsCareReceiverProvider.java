package com.scnsoft.eldermark.service.validation;

import com.scnsoft.eldermark.dao.phr.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.service.BasePhrService;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AccessibleResidentsCareReceiverProvider extends BasePhrService implements AccessibleResidentsProvider {

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    @Autowired
    private MPIService mpiService;

    @Autowired
    private ResidentCareTeamMemberDao residentCareTeamMemberDao;

    @Override
    public Collection<Long> getAccessibleResidentsOrThrow(Long receiverId) {
        final ResidentCareTeamMember careTeamMember = residentCareTeamMemberDao.getOne(receiverId);
        careTeamSecurityUtils.checkAccessToCareTeamMemberOrThrow(careTeamMember);
        return mpiService.listResidentWithMergedResidents(careTeamMember.getResidentId());
    }

    @Override
    public Collection<Long> getAccessibleResidentsOrThrow(Long receiverId, AccessRight.Code accessRightCode) {
        final ResidentCareTeamMember careTeamMember = residentCareTeamMemberDao.getOne(receiverId);
        careTeamSecurityUtils.checkAccessToCareTeamMemberOrThrow(careTeamMember, accessRightCode);
        return mpiService.listResidentWithMergedResidents(careTeamMember.getResidentId());
    }

    @Override
    public Long getMainResidentOrThrow(Long receiverId) {
        final ResidentCareTeamMember careTeamMember = residentCareTeamMemberDao.getOne(receiverId);
        careTeamSecurityUtils.checkAccessToCareTeamMemberOrThrow(careTeamMember);
        return careTeamMember.getResidentId();
    }

    @Override
    public Long getMainResidentOrThrow(Long receiverId, AccessRight.Code accessRightCode) {
        final ResidentCareTeamMember careTeamMember = residentCareTeamMemberDao.getOne(receiverId);
        careTeamSecurityUtils.checkAccessToCareTeamMemberOrThrow(careTeamMember, accessRightCode);
        return careTeamMember.getResidentId();
    }
}
