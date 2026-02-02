package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.dto.ClientAssessmentResultSecurityFieldsAware;
import com.scnsoft.eldermark.beans.security.projection.entity.AssessmentSecurityAwareEntity;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientSecurityAwareEntity;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.AssessmentService;
import com.scnsoft.eldermark.service.ClientAssessmentResultService;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.StreamUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("clientAssessmentResultSecurityService")
@Transactional(readOnly = true)
public class ClientAssessmentResultSecurityServiceImpl extends BaseSecurityService implements ClientAssessmentResultSecurityService {

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = EnumSet.of(
            ASSESSMENT_VIEW_ALL_EXCEPT_OPTED_OUT,
            ASSESSMENT_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION,
            ASSESSMENT_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY,
            ASSESSMENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
            ASSESSMENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
            ASSESSMENT_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM,
            ASSESSMENT_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM,
            ASSESSMENT_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
            ASSESSMENT_VIEW_MERGED_IF_SELF_RECORD);

    private static final Long AUTHOR_CHECK_EXISTS_CREATED_BY_SELF = -1L;

    @Autowired
    private ClientAssessmentResultService clientAssessmentService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private CommunityService communityService;

    @Override
    public boolean canAdd(ClientAssessmentResultSecurityFieldsAware dto) {
        return canAdd(clientService.findSecurityAwareEntity(dto.getClientId()), dto.getAssessmentId());
    }

    @Override
    public boolean canAdd(Long clientId, Long assessmentId) {
        return canAdd(clientService.findSecurityAwareEntity(clientId), assessmentId);
    }

    private boolean canAdd(ClientSecurityAwareEntity client, Long assessmentId) {
        if (!isInEligibleForDiscoveryCommunity(client)) {
            return false;
        }

        if (!canModifyTypeInCommunity(client.getCommunityId(), assessmentId)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(ASSESSMENT_ADD_ALL_EXCEPT_OPTED_OUT) && isClientOptedIn(client)) {
            return true;
        }

        if (permissionFilter.hasPermission(ASSESSMENT_ADD_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_ADD_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_ADD_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_ADD_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(
                    employees,
                    client.getCommunityId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_ADD_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_ADD_IF_CURRENT_REGULAR_CLIENT_CTM);
            if (isAnyInClientCareTeam(
                    employees,
                    client,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
            if (isClientOptedInAndAddedBySelf(employees, client)) {
                return true;
            }
        }

        return false;
    }


    @Override
    public List<Assessment> findAccessibleTypesForAdd(Long clientId, List<String> filterByCode) {
        var client = clientService.findSecurityAwareEntity(clientId);

        if (!canAdd(client, ANY_TARGET_TYPE)) {
            return Collections.emptyList();
        }

        var community = communityService.get(client.getCommunityId());

        return assessmentService.findTypesAllowedInCommunity(community, filterByCode);
    }

    @Override
    public List<Assessment> findAccessibleTypesForView(Long clientId, List<String> filterByCode) {
        var client = clientService.findSecurityAwareEntity(clientId);
        if (!isInEligibleForDiscoveryCommunity(client)) {
            return Collections.emptyList();
        }

        var mergedClients = mergedClientsEligibleForDiscovery(clientId);
        var communities = communityService.findSecurityAwareEntities(CareCoordinationUtils.getCommunityIdsSet(mergedClients));

        var allowedTypesInMergedClientsCommunities = assessmentService.findTypesAllowedInAnyCommunities(communities, filterByCode);
        var existingTypes = assessmentService.findTypesExistingForAnyClient(CareCoordinationUtils.toIdsSet(mergedClients), filterByCode);

        return Stream.concat(allowedTypesInMergedClientsCommunities.stream(), existingTypes.stream())
                .filter(StreamUtils.distinctByKey(Assessment::getId))
                .filter(type -> canViewByClientIdAndType(clientId, () -> type))
                .collect(Collectors.toList());
    }

    @Override
    public boolean canEdit(Long clientAssessmentResultId) {
        var assessmentResult = clientAssessmentService.findSecurityAwareEntity(clientAssessmentResultId);

        return canEdit(clientService.findSecurityAwareEntity(assessmentResult.getClientId()),
                Lazy.of(assessmentResult::getEmployeeId),
                assessmentService.findSecurityAwareEntity(assessmentResult.getAssessmentId())
        );
    }

    private boolean canEdit(ClientSecurityAwareEntity client,
                            Supplier<Long> assessmentAuthorIdSupplier,
                            AssessmentSecurityAwareEntity assessment) {
        if (BooleanUtils.isFalse(assessment.getEditable())) {
            return false;
        }

        var assessmentId = assessment.getId();
        if (!canModifyTypeInCommunity(client.getCommunityId(), assessmentId)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(ASSESSMENT_EDIT_ALL_EXCEPT_OPTED_OUT) && isClientOptedIn(client)) {
            return true;
        }

        if (permissionFilter.hasPermission(ASSESSMENT_EDIT_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_EDIT_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_EDIT_ADDED_BY_SELF_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_EDIT_ADDED_BY_SELF_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, client.getCommunityId())
                    && isExistsCreatedBySelfOfClientOrCreatedBySelf(employees, client.getId(), assessmentAuthorIdSupplier.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_EDIT_ADDED_BY_SELF_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_EDIT_ADDED_BY_SELF_IF_CURRENT_REGULAR_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(employees, client.getCommunityId(), AffiliatedCareTeamType.REGULAR, HieConsentCareTeamType.currentWithOptimizations(client))
                    && isExistsCreatedBySelfOfClientOrCreatedBySelf(employees, client.getId(), assessmentAuthorIdSupplier.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_EDIT_ADDED_BY_SELF_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_EDIT_ADDED_BY_SELF_IF_CURRENT_REGULAR_CLIENT_CTM);
            if (isAnyInClientCareTeam(employees, client, AffiliatedCareTeamType.REGULAR, HieConsentCareTeamType.currentWithOptimizations(client))
                    && isExistsCreatedBySelfOfClientOrCreatedBySelf(employees, client.getId(), assessmentAuthorIdSupplier.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_EDIT_ADDED_BY_SELF_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_EDIT_ADDED_BY_SELF_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
            if (isClientOptedInAndAddedBySelf(employees, client) &&
                    isExistsCreatedBySelfOfClientOrCreatedBySelf(employees, client.getId(), assessmentAuthorIdSupplier.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_EDIT_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_EDIT_IF_CURRENT_REGULAR_CLIENT_CTM);
            if (isAnyInClientCareTeam(employees, client, AffiliatedCareTeamType.REGULAR, HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_EDIT_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_EDIT_IF_CURRENT_REGULAR_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(employees, client.getCommunityId(), AffiliatedCareTeamType.REGULAR, HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        return false;
    }

    private boolean isExistsCreatedBySelfOfClientOrCreatedBySelf(Collection<Employee> employees, Long clientId, Long authorId) {
        if (AUTHOR_CHECK_EXISTS_CREATED_BY_SELF.equals(authorId)) {
            return clientAssessmentService.existsCreatedByAnyOfClient(CareCoordinationUtils.toIdsSet(employees), clientId);
        }
        return isSelfEmployeeRecord(employees, authorId);
    }

    private boolean canModifyTypeInCommunity(Long communityId, Long assessmentId) {
        //checks if user can add or edit assessments of given type in community

        //no need to check user role because according to current permission matrix if user can add or edit assessment,
        //then there are no type restrictions by roles

        var community = communityService.findSecurityAwareEntity(communityId);
        if (ANY_TARGET_TYPE.equals(assessmentId)) {
            return assessmentService.existTypesAllowedInCommunity(community);
        }
        return assessmentService.isTypeAllowedForCommunity(community, assessmentId);
    }

    @Override
    public boolean canViewList() {
        return hasAnyPermission(VIEW_LIST_PERMISSIONS);
    }

    @Override
    public boolean canView(Long clientAssessmentResultId) {
        var assessmentResult = clientAssessmentService.findSecurityAwareEntity(clientAssessmentResultId);
        var assessment = Lazy.of(() -> assessmentService.findById(assessmentResult.getAssessmentId()).orElseThrow());

        return canViewByClientIdAndType(assessmentResult.getClientId(), assessment);
    }

    private boolean canViewByClientIdAndType(Long clientId, Supplier<Assessment> assessment) {
        var client = clientService.findById(clientId, CommunityIdAware.class);
        return canViewByClientAndType(clientId, client, assessment);
    }

    private <T extends ClientIdAware> boolean canViewByClientAndType(Long clientId, CommunityIdAware client, Supplier<Assessment> assessment) {
        if (!isInEligibleForDiscoveryCommunity(client)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        var mergedClients = mergedClientsEligibleForDiscovery(clientId);

        if (!isTypeViewAllowedForAnyClient(assessment.get(), mergedClients)) {
            return false;
        }

        var mergedClientOrganizationIds = lazyOrganizationIdsSet(() -> mergedClients);
        var mergedClientsCommunityIds = lazyCommunityIdsSet(() -> mergedClients);

        var mergedOptedInClientsCommunityIds = Lazy.of(() ->
                mergedClients.stream()
                        .filter(this::isClientOptedIn)
                        .map(CommunityIdAware::getCommunityId)
                        .collect(Collectors.toSet())
        );

        if (permissionFilter.hasPermission(ASSESSMENT_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION);
            var employeesInClientsOrganizations = findEmployeesInOrganizations(employees, mergedClientOrganizationIds.get());

            if (canViewAssessmentTypeByRole(employeesInClientsOrganizations, permissionFilter,
                    assessment.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY);
            var employeesInCommunities = findEmployeesInCommunities(employees, mergedClientsCommunityIds.get());

            if (canViewAssessmentTypeByRole(employeesInCommunities, permissionFilter,
                    assessment.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);

            var employeesInAffiliatedOrgs = findInAffiliatedOrganizationOfAnyCommunity(employees, mergedOptedInClientsCommunityIds.get());

            if (canViewAssessmentTypeByRole(employeesInAffiliatedOrgs, permissionFilter, assessment.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);
            var employeesInAffiliatedComms = findInAffiliatedCommunityOfAny(employees, mergedOptedInClientsCommunityIds.get());

            if (canViewAssessmentTypeByRole(employeesInAffiliatedComms, permissionFilter,
                    assessment.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM);
            var rpCommunitiesCareTeamMembers = findCommunitiesCareTeamMembers(
                    employees,
                    mergedClientsCommunityIds.get(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentForAny(mergedClients)
            );

            if (canViewAssessmentTypeByRole(rpCommunitiesCareTeamMembers, permissionFilter, assessment.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM);
            var rpClientsCareTeamMembers = findClientsCareTeamMembers(
                    employees,
                    mergedClients,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentForAny(mergedClients)
            );

            if (canViewAssessmentTypeByRole(rpClientsCareTeamMembers, permissionFilter, assessment.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);

            var creators = findClientsCreators(
                    employees,
                    mergedClients.stream()
                            .filter(this::isClientOptedIn)
                            .collect(Collectors.toList())
            );

            if (canViewAssessmentTypeByRole(creators, permissionFilter, assessment.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ASSESSMENT_VIEW_MERGED_IF_SELF_RECORD)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_VIEW_MERGED_IF_SELF_RECORD);
            var selfClientsRecords = filterEmployeesSelfClientRecord(employees, mergedClients);

            if (canViewAssessmentTypeByRole(selfClientsRecords, permissionFilter,
                    assessment.get())) {
                return true;
            }
        }

        return false;
    }

    private boolean canViewAssessmentTypeByRole(Stream<Employee> employees,
                                                PermissionFilter permissionFilter,
                                                Assessment assessment) {
        var allTypesAllowedEmployees = CareCoordinationUtils.toIdsSet(permissionFilter.getEmployees(ASSESSMENT_VIEW_ALL_TYPES_ALLOWED));
        if (allTypesAllowedEmployees.isEmpty() && employees
                .map(permissionFilter::getEmployeeRole)
                .noneMatch(role -> assessment.getAllowedRoles().contains(role))) {
            return false;
        }

        return true;
    }

    private boolean isTypeViewAllowedForAnyClient(Assessment assessment, Collection<ClientSecurityAwareEntity> clients) {
        //1. check if assessment type is allowed in any client's community
        var clientCommunityIds = CareCoordinationUtils.getCommunityIdsSet(clients);
        if (assessmentService.isTypeAllowedForAnyCommunity(communityService.findSecurityAwareEntities(clientCommunityIds),
                assessment.getId())) {
            return true;
        }

        //2. check if client or merged already has assessment with such a type
        // (possible when assessment type was enabled for organization, assessment was added and assessment type was disabled)
        if (assessmentService.isTypeExistsForAnyClient(CareCoordinationUtils.toIdsSet(clients), assessment.getId())) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canViewTypeOfClient(Long assessmentId, Long clientId) {
        var assessment = Lazy.of(() -> assessmentService.findById(assessmentId).orElseThrow());
        return canViewByClientIdAndType(clientId, assessment);
    }

    @Override
    public boolean canModifyTypeOfClient(Long assessmentId, Long clientId) {
        //checks that user can modify (add or edit) assessments of given client at all and that type modifications is allowed
        //allowed type modifications are checked inside canAdd or canEdit

        //also checking merged clients because assessment modification request might come from merged client
        //assessments screen and UI sends clientId from url
        var merged = mergedClientsEligibleForDiscovery(clientId);
        var assessment = assessmentService.findSecurityAwareEntity(assessmentId);

        return merged.stream().anyMatch(
                mergedClient -> {
                    //using AUTHOR_CHECK_EXISTS_CREATED_BY_SELF for canEdit because we don't have any specific assessment result
                    //to get his creator for 'created by self' checks. Instead we'll check that there exists at least one
                    //assessment already created by current user
                    return canAdd(mergedClient, assessmentId) ||
                            canEdit(mergedClient,
                                    Lazy.of(() -> AUTHOR_CHECK_EXISTS_CREATED_BY_SELF),
                                    assessment
                            );
                }
        );
    }

    @Override
    public boolean canHide(Long clientAssessmentResultId) {
        var assessmentResult = clientAssessmentService.findSecurityAwareEntity(clientAssessmentResultId);
        var client = clientService.findSecurityAwareEntity(assessmentResult.getClientId());

        if (!isInEligibleForDiscoveryCommunity(client)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(ASSESSMENT_HIDE_ALL_EXCEPT_OPTED_OUT) && isClientOptedIn(client)) {
            return true;
        }

        if (!canModifyTypeInCommunity(client.getCommunityId(), assessmentResult.getAssessmentId())) {
            return false;
        }

        if (permissionFilter.hasPermission(ASSESSMENT_HIDE_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_HIDE_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canRestore(Long clientAssessmentResultId) {
        var assessmentResult = clientAssessmentService.findSecurityAwareEntity(clientAssessmentResultId);
        var client = clientService.findSecurityAwareEntity(assessmentResult.getClientId());

        if (!isInEligibleForDiscoveryCommunity(client)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(ASSESSMENT_RESTORE_ALL_EXCEPT_OPTED_OUT) && isClientOptedIn(client)) {
            return true;
        }

        if (!canModifyTypeInCommunity(client.getCommunityId(), assessmentResult.getAssessmentId())) {
            return false;
        }

        if (permissionFilter.hasPermission(ASSESSMENT_RESTORE_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(ASSESSMENT_RESTORE_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canDownloadInTuneReport(Long clientId) {
        return hasAnyPermission(List.of(CLIENT_REPORT_GENERATION_IN_TUNE_ALLOWED))
                && canViewByClientIdAndType(clientId, () -> assessmentService.findByShortName(Assessment.IN_TUNE));
    }
}
