package com.scnsoft.eldermark.web.security;

import com.scnsoft.eldermark.dao.carecoordination.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.service.BasePhrService;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.services.phr.AccessRightsService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author phomal
 * Created by phomal on 5/16/2017.
 */
@Component
public class CareTeamSecurityUtils extends BasePhrService {

    private final static Logger logger = LoggerFactory.getLogger(CareTeamSecurityUtils.class);

    @Autowired
    UserDao userDao;

    @Autowired
    UserResidentRecordsDao userResidentRecordsDao;

    @Autowired
    ResidentCareTeamMemberDao residentCareTeamMemberDao;

    @Autowired
    AccessRightsService accessRightsService;

    @Autowired
    private MPIService mpiService;

    public User getCurrentUser() {
        return userDao.getOne(PhrSecurityUtils.getCurrentUserId());
    }

    public Employee getCurrentEmployeeOrThrow() {
        return getEmployeeOrThrow(getCurrentUser());
    }

    public boolean checkAccessToUserInfo(Long userId, AccessRight.Code accessRightCode) {
        boolean selfAccess = PhrSecurityUtils.checkAccessToUserInfo(userId);
        if (selfAccess) {
            return true;
        }

        Long currentUserId = PhrSecurityUtils.getCurrentUserId();
        logger.warn("Access attempt by logged in User #" + currentUserId + " to User #" + userId + "'s restricted patient data (" + accessRightCode + ")");

        List<ResidentCareTeamMember> ctms = getCareTeamMembers(currentUserId, userId);
        for (ResidentCareTeamMember ctm : ctms) {
            if (accessRightsService.checkHasAccessRight(ctm, accessRightCode)) {
                return true;
            }
        }

        return false;
    }

    private List<ResidentCareTeamMember> getCareTeamMembers(Long currentUserId, Long userId) {

        User userCtm = userDao.findOne(currentUserId);
        if (userCtm == null || userCtm.getEmployeeId() == null) {
            return Collections.emptyList();
        }
        final List<Long> residentIds = userResidentRecordsDao.getAllResidentIdsByUserId(userId);
        if (CollectionUtils.isEmpty(residentIds)) {
            throw new PhrException(PhrExceptionType.USER_NOT_FOUND);
        }

        return residentCareTeamMemberDao.getResidentCareTeamMembersByEmployeeIdAndResidentIds(userCtm.getEmployeeId(), residentIds);
    }

    private boolean checkCtmRelationExists(Long userId) {
        Long currentUserId = PhrSecurityUtils.getCurrentUserId();
        logger.warn("Access attempt by logged in User #" + currentUserId + " to User #" + userId + "'s restricted patient data");

        final List<ResidentCareTeamMember> ctms = getCareTeamMembers(currentUserId, userId);

        return CollectionUtils.isNotEmpty(ctms);
    }

    public void checkAccessToUserInfoOrThrow(Long userId, AccessRight.Code accessRightCode) {
        if (!checkAccessToUserInfo(userId, accessRightCode)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
    }

    public void checkAccessToUserInfoOrThrow(Long userId) {
        if (!PhrSecurityUtils.checkAccessToUserInfo(userId) && !checkCtmRelationExists(userId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
    }

    /**
     * Check if userId is associated to specified residentId.
     *
     * @param userId
     * @param residentId
     * @return
     */
    public boolean isAssociated(Long userId, Long residentId) {
        // check 1. Is user (userId) associated with the patient (residentId)?
        Collection<Long> residentIds = getResidentIds(userId);
        if (CollectionUtils.isNotEmpty(residentIds) && residentIds.contains(residentId)) {
            return true;
        }

        // check 2. Is current user an employee from the patient's care team?
        residentIds = getCareTeamReceiversForCurrentUser();
        if (CollectionUtils.isNotEmpty(residentIds) && residentIds.contains(residentId)) {
            return true;
        }

        // check 3. Is current user an employee from a care team of patient's merged record (One Care Team feature)?
        residentIds = mpiService.listMergedResidents(residentIds);
        if (CollectionUtils.isNotEmpty(residentIds) && residentIds.contains(residentId)) {
            return true;
        }

        return false;
    }

    /**
     * Check if userId is associated with specific access right to specified residentId.
     *
     * @param userId
     * @param residentId
     * @param accessRight access right to check
     * @return
     */
    public boolean isAssociated(Long userId, Long residentId, AccessRight.Code accessRight) {
        // check 1. Is user (userId) associated with the patient (residentId)?
        Collection<Long> residentIds = getResidentIds(userId);
        if (CollectionUtils.isNotEmpty(residentIds) && residentIds.contains(residentId)) {
            return true;
        }

        // check 2. Is current user an employee from the patient's care team with specific access right?
        residentIds = getCareTeamReceiversForCurrentUser(accessRight);
        if (CollectionUtils.isNotEmpty(residentIds) && residentIds.contains(residentId)) {
            return true;
        }

        // check 3. Is current user an employee from a care team of patient's merged record (One Care Team feature)?
        residentIds = mpiService.listMergedResidents(residentIds);
        if (CollectionUtils.isNotEmpty(residentIds) && residentIds.contains(residentId)) {
            return true;
        }

        return false;
    }

    /**
     * Check if current user is associated with specific access right to specified residentId.
     *
     * @param residentId
     * @param accessRight access right to check
     * @return
     */
    public boolean isAssociatedWithCurrentUser(Long residentId, AccessRight.Code accessRight) {
        return isAssociated(PhrSecurityUtils.getCurrentUserId(), residentId, accessRight);
    }

    /**
     * Check if current user is associated to specified residentId.
     *
     * @param residentId
     * @return
     */
    public boolean isAssociatedWithCurrentUser(Long residentId) {
        return isAssociated(PhrSecurityUtils.getCurrentUserId(), residentId);
    }

    /**
     * Get a list of resident IDs of care receivers from a care team of the current user.
     *
     * @return a list of resident IDs
     */
    List<Long> getCareTeamReceiversForCurrentUser() {
        Long currentUserId = PhrSecurityUtils.getCurrentUserId();
        Long employeeId = getEmployeeIdOrThrow(currentUserId);
        return residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeId(Collections.singleton(employeeId));
    }

    /**
     * Get a list of resident IDs of care receivers, that allowed specific access, from a care team of the current user.
     *
     * @param accessRight access right to check
     * @return
     */
    public List<Long> getCareTeamReceiversForCurrentUser(AccessRight.Code accessRight) {
        Long currentUserId = PhrSecurityUtils.getCurrentUserId();
        Long employeeId = getEmployeeIdOrThrow(currentUserId);
        return residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeIdWithAccessRightsCheck(Collections.singleton(employeeId),
                accessRightsService.getAccessRight(accessRight));
    }

    /**
     * Get a list of resident IDs of care receivers, that allowed specific access, from a care team of the current user
     * plus IDs of their merged residents.
     *
     * @return a list of resident IDs
     */
    public List<Long> getCareTeamReceiversAndTheirMergesForCurrentUser(AccessRight.Code accessRight) {
        // CCN-772 One Care Team
        final List<Long> residentIds = getCareTeamReceiversForCurrentUser(accessRight);
        return getWithMergedResidentIds(residentIds);
    }

    /**
     * Get a list of resident IDs of care receivers, from a care team of the current user
     * plus IDs of their merged residents.
     *
     * @return a list of resident IDs
     */
    public List<Long> getCareTeamReceiversAndTheirMergesForCurrentUser() {
        // CCN-772 One Care Team
        final List<Long> residentIds = getCareTeamReceiversForCurrentUser();
        return getWithMergedResidentIds(residentIds);
    }

    private List<Long> getWithMergedResidentIds(List<Long> residentIds) {
        final Set<Long> careTeamReceiversWithMerges = new HashSet<>(residentIds);
        careTeamReceiversWithMerges.addAll(mpiService.listMergedResidents(residentIds));

        return new ArrayList<>(careTeamReceiversWithMerges);
    }

    /**
     * Checks if current user is a part of care team for given care receiver
     *
     * @param careReceiverId
     * @param accessRight
     * @return
     */
    public boolean checkAccessToCareTeamMember(Long careReceiverId, AccessRight.Code accessRight) {
        return checkAccessToCareTeamMember(residentCareTeamMemberDao.get(careReceiverId), accessRight);
    }

    /**
     * Checks if current user is a part of care team for given care receiver
     *
     * @param careTeamMember
     * @param accessRight
     * @return
     */
    public boolean checkAccessToCareTeamMember(ResidentCareTeamMember careTeamMember, AccessRight.Code accessRight) {
        final List<Long> residentIds = getCareTeamReceiversAndTheirMergesForCurrentUser(accessRight);
        return careTeamMember != null && getCurrentUser().getEmployeeId().equals(careTeamMember.getEmployee().getId()) && residentIds.contains(careTeamMember.getResidentId());
    }

    public void checkAccessToCareTeamMemberOrThrow(Long careReceiverId, AccessRight.Code accessRight) {
        checkAccessToCareTeamMemberOrThrow(residentCareTeamMemberDao.get(careReceiverId), accessRight);
    }

    public void checkAccessToCareTeamMemberOrThrow(ResidentCareTeamMember careTeamMember, AccessRight.Code accessRight) {
        if (!checkAccessToCareTeamMember(careTeamMember, accessRight)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
    }

    public boolean checkAccessToCareTeamMember(ResidentCareTeamMember careTeamMember) {
        final List<Long> residentIds = getCareTeamReceiversAndTheirMergesForCurrentUser();
        return careTeamMember != null && getCurrentUser().getEmployeeId().equals(careTeamMember.getEmployee().getId()) && residentIds.contains(careTeamMember.getResidentId());
    }

    public void checkAccessToCareTeamMemberOrThrow(Long careReceiverId) {
        checkAccessToCareTeamMemberOrThrow(residentCareTeamMemberDao.get(careReceiverId));
    }

    public void checkAccessToCareTeamMemberOrThrow(ResidentCareTeamMember careTeamMember) {
        if (!checkAccessToCareTeamMember(careTeamMember)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
    }
}
