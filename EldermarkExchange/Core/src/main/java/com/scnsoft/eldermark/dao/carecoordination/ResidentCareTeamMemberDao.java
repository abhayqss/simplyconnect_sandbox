package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDao;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author mradzivonenka
 * @author Netkachev
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 24-Sep-15.
 */
public interface ResidentCareTeamMemberDao extends BaseDao<ResidentCareTeamMember> {
    /**
     * Get a list of care team members for the specified residents
     */
    List<ResidentCareTeamMember> getCareTeamMembers(Collection<Long> residentIds, Boolean affiliated, final Pageable pageable);

    //ResidentCareTeamMember getCareTeamMember(@Param("employeeId") Long employeeId, @Param("residentId") Long residentId);
    /**
     * Shortcut for {@link ResidentCareTeamMemberDao#getCareTeamMembers(Collection, Boolean, Pageable)}
     */
    List<ResidentCareTeamMember> getCareTeamMembers(Long residentId);

    Long getCareTeamMembersCount(Long residentId);

    List<ResidentCareTeamMember> getResidentCareTeamMembersByEmployeeAndRole(Long residentId, Long employeeId, Long roleId);

    List<Long> getCareTeamResidentIdsByEmployeeId(Set<Long> employeeIds, Long databaseId);

    List<Long> getCareTeamResidentIdsByEmployeeId(Set<Long> employeeIds);

    List<Long> getCareTeamResidentIdsByEmployeeIdWithAccessRightsCheck(Set<Long> employeeIds, AccessRight accessRight);

    @Transactional(propagation = Propagation.MANDATORY)
    List<ResidentCareTeamMember> getCareTeamMembersByEmployeeIds(Set<Long> employeeIds, Pageable pageable);

    Long getCareTeamMembersCountByEmployeeIds(Set<Long> employeeIds);

    ResidentCareTeamMember getResidentCareTeamMemberByEmployeeIdAndResidentId(Long employeeId, Long residentId);

    List<ResidentCareTeamMember> getResidentCareTeamMembersByEmployeeIdAndResidentIds(Long employeeId, List<Long> residentIds);

    @Transactional(propagation = Propagation.MANDATORY)
    void deleteByIdIn(List<Long> idsToDelete);

    List<Long> getResidentCareTeamMemberIdsFromDeletedAffiliatedRelation();

    boolean checkHasResidentCareTeamMember(Set<Long> employeeIds, Long residentId);

    List<ResidentCareTeamMember> getPrimaryCareTeamMembersWithAccessRightCheckExcludeInactive(Set<Long> residentIds, AccessRight accessRight);

    List<ResidentCareTeamMember> getAffiliatedCareTeamMembersWithAccessRightCheckExcludeInactive(Set<Long> residentIds, AccessRight accessRight);
    
    Boolean getIncludeInFaceSheetById(Long careTeamMemberId);

    List<ResidentCareTeamMember> getCareTeamMembersToBeIncludedInFacesheet(Collection<Long> residentIds);
}