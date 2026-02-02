package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.PrivilegesDao;
import com.scnsoft.eldermark.entity.CareTeamRole;
import com.scnsoft.eldermark.entity.phr.Privilege;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author phomal
 * Created on 11/1/2017
 */
@Service
@Transactional(readOnly = true)
public class PrivilegesService extends BasePhrService {

    private final PrivilegesDao privilegesDao;

    @Autowired
    public PrivilegesService(PrivilegesDao privilegesDao) {
        this.privilegesDao = privilegesDao;
    }

    public Boolean canInviteFriendToCareTeam() {
        final Long employeeId = getEmployeeIdOrThrow(PhrSecurityUtils.getCurrentUserId());
        return privilegesDao.hasRight(employeeId, Privilege.Name.CARE_TEAM_LIST_INVITE_FRIEND);
    }

    public Boolean canDeleteFromCareTeam(CareTeamRole careTeamMemberRole) {
        final Long employeeId = getEmployeeIdOrThrow(PhrSecurityUtils.getCurrentUserId());
        return privilegesDao.hasRight(employeeId, Privilege.Name.CARE_TEAM_DELETE, careTeamMemberRole);
    }

    public Boolean canDeleteThemselfFromCareTeam() {
        final Long employeeId = getEmployeeIdOrThrow(PhrSecurityUtils.getCurrentUserId());
        return privilegesDao.hasRight(employeeId, Privilege.Name.CARE_TEAM_DELETE_SELF);
    }

    public Boolean canSetCareTeamMemberEmergency() {
        final Long employeeId = getEmployeeIdOrThrow(PhrSecurityUtils.getCurrentUserId());
        return privilegesDao.hasRight(employeeId, Privilege.Name.CARE_TEAM_EMERGENCY_WRITE);
    }

    public Boolean canReadOrganization(Long orgId) {
        final Long employeeId = getEmployeeIdOrThrow(PhrSecurityUtils.getCurrentUserId());
        return privilegesDao.hasRight(employeeId, Privilege.Name.CARE_TEAM_EMERGENCY_WRITE);
    }

    public Boolean canAddNote() {
        final Long employeeId = getEmployeeIdOrThrow(PhrSecurityUtils.getCurrentUserId());
        return privilegesDao.hasRight(employeeId, Privilege.Name.ADD_NOTE);
    }

    public Boolean canEditNote() {
        final Long employeeId = getEmployeeIdOrThrow(PhrSecurityUtils.getCurrentUserId());
        return privilegesDao.hasRight(employeeId, Privilege.Name.EDIT_NOTE);
    }

    public Boolean canViewNote() {
        final Long employeeId = getEmployeeIdOrThrow(PhrSecurityUtils.getCurrentUserId());
        return privilegesDao.hasRight(employeeId, Privilege.Name.VIEW_NOTE);
    }

    public Boolean canViewAlert() {
        final Long employeeId = getEmployeeIdOrThrow(PhrSecurityUtils.getCurrentUserId());
        return privilegesDao.hasRight(employeeId, Privilege.Name.NOTIFY_ALERT_READ);
    }

    public Boolean canChangeAlertStatus() {
        final Long employeeId = getEmployeeIdOrThrow(PhrSecurityUtils.getCurrentUserId());
        return privilegesDao.hasRight(employeeId, Privilege.Name.NOTIFY_ALERT_STATUS_CHANGE);
    }

}
