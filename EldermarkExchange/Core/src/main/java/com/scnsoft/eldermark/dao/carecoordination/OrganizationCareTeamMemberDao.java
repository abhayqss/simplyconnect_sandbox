package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDao;
import com.scnsoft.eldermark.entity.OrganizationCareTeamMember;
import com.scnsoft.eldermark.shared.carecoordination.SimpleDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author mradzivonenka
 * @author Netkachev
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 24-Sep-15.
 */
public interface OrganizationCareTeamMemberDao extends BaseDao<OrganizationCareTeamMember> {
    List<OrganizationCareTeamMember> getOrganizationCareTeamMembers(Long organizationId, Boolean affiliated, Long databaseId, final Pageable pageable);
    List<OrganizationCareTeamMember> getOrganizationCareTeamMembersExcludeInactive(Long organizationId);
    Long getOrganizationCareTeamMembersCount(final Long organizationId);
    List<OrganizationCareTeamMember> getOrganizationCareTeamMembersByEmployeeAndRole(final Long organizationId, final Long employeeId, final Long roleId);
    List<Long> getCctOrganizationIdsForEmployee(Set<Long> employeeAndLinkedEmployeeIds, Long databaseId);
    List<Long> getCctOrganizationIdsForEmployee(Long employeeIds, Long databaseId);
    List<Long> getPatientOrganizationIdsForEmployee(Long employeeIds);
    List<Long> getCareTeamResidentIdsByEmployeeId(Set<Long> employeeIds, Long databaseId);

    @Transactional(propagation = Propagation.MANDATORY)
    void deleteByIdIn(List<Long> idsToDelete);
    List<Long> getOrganizationCareTeamMemberIdsFromDeletedAffiliatedRelation();

//    boolean hasAffiliatedCareTeamMembers(Long organizationId, Long affiliatedDatabaseId);
//    boolean hasAffiliatedCareTeamMembersInDb(Long databaseId, Long affiliatedDatabaseId);
//    boolean hasCareTeamMembers(Long organizationId, Long employeeId);
    boolean hasAffiliatedCareTeamMembers(Long organizationId, Long databaseId, Long affiliatedDatabaseId, boolean otherOrgs);
    List<SimpleDto> getOtherOrganizationsWithAffiliatedMembers(Long organizationId, Long databaseId, Long affiliatedDatabaseId);
    boolean checkHasOrganizationCareTeamMember(Set<Long> employeeIds, Long organizationId);
	List<OrganizationCareTeamMember> getCareTeamMembersByEmployeeId(Long employeeId, Long organizationId);
}
