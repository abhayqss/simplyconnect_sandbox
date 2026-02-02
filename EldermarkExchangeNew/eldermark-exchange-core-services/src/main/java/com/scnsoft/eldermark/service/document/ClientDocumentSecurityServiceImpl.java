package com.scnsoft.eldermark.service.document;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.dto.ClientDocumentSecurityFieldsAware;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientDocumentSecurityAwareEntity;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.document.DocumentType;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderStatus;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.LabResearchOrderService;
import com.scnsoft.eldermark.service.ReferralService;
import com.scnsoft.eldermark.service.security.BaseSecurityService;
import com.scnsoft.eldermark.service.security.LabSecurityService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.PermissionFilterUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiFunction;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("clientDocumentSecurityService")
@Transactional(readOnly = true)
public class ClientDocumentSecurityServiceImpl extends BaseSecurityService implements ClientDocumentSecurityService {

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = EnumSet.of(
            DOCUMENT_VIEW_MERGED_SHARED_ALL_EXCEPT_OPTED_OUT,
            DOCUMENT_VIEW_MERGED_SHARED_IF_ASSOCIATED_ORGANIZATION,
            DOCUMENT_VIEW_MERGED_SHARED_IF_ASSOCIATED_COMMUNITY,
            DOCUMENT_VIEW_MERGED_SHARED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
            DOCUMENT_VIEW_MERGED_SHARED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
            DOCUMENT_VIEW_MERGED_SHARED_IF_CURRENT_RP_COMMUNITY_CTM,
            DOCUMENT_VIEW_MERGED_SHARED_IF_CURRENT_RP_CLIENT_CTM,
            DOCUMENT_VIEW_MERGED_SHARED_IF_SELF_RECORD,
            DOCUMENT_VIEW_MERGED_SHARED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
            DOCUMENT_VIEW_MERGED_SHARED_IF_CLIENT_FOUND_IN_RECORD_SEARCH
    );


    @Autowired
    private ClientDocumentService documentService;

    @Autowired
    private ReferralService referralService;

    @Autowired
    private LabSecurityService labSecurityService;

    @Autowired
    private LabResearchOrderService labResearchOrderService;

    @Override
    public boolean canViewList() {
        return hasAnyPermission(VIEW_LIST_PERMISSIONS);
    }

    @Override
    public boolean canView(long documentId) {
        return canDownload(documentId);
    }

    @Override
    public boolean canDownload(long documentId) {
        var document = documentService.findSecurityAwareEntity(documentId);
        return canDownload(document);
    }

    @Override
    public boolean canDownloadAll(Collection<Long> documentIds) {
        if (CollectionUtils.isNotEmpty(documentIds)) {
            var documents = documentService.findSecurityAwareEntities(documentIds);
            return documents.stream().allMatch(this::canDownload);
        }
        return true;
    }

    private boolean canDownload(ClientDocumentSecurityAwareEntity document) {
        return hasGeneralRoleAccess(document) && hasCoordinatorAccessIfLabNotReviewedOrElse(document, true);
    }

    private boolean hasGeneralRoleAccess(ClientDocumentSecurityAwareEntity document) {
        if (document.getClientId() == null) {
            return false;
        }

        if (BooleanUtils.isTrue(document.getIsCloud())) {
            return false;
        }

        if (!isEligibleForDiscoveryCommunity(document.getClientCommunityId())) {
            return false;
        }

        var filter = currentUserFilter();

        var mergedClients = lazyMergedSecurityClients(document);
        var mergedOrganizations = lazyOrganizationIdsSet(mergedClients);
        var mergedCommunities = lazyCommunityIdsSet(mergedClients);

        if (filter.hasPermission(DOCUMENT_VIEW_MERGED_SHARED_ALL_EXCEPT_OPTED_OUT)) {
            return areAllClientsOptedIn(mergedClients.get());
        }

        if (filter.hasPermission(DOCUMENT_VIEW_MERGED_SHARED_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(DOCUMENT_VIEW_MERGED_SHARED_IF_ASSOCIATED_ORGANIZATION);

            if (isDocumentSharedWithAny(document, employees) &&
                    isAnyCreatedUnderAnyOrganization(employees, mergedOrganizations.get())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_VIEW_MERGED_SHARED_IF_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(DOCUMENT_VIEW_MERGED_SHARED_IF_ASSOCIATED_COMMUNITY);

            if (isDocumentSharedWithAny(document, employees) &&
                    isAnyCreatedUnderAnyCommunity(employees, mergedCommunities.get())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_VIEW_MERGED_SHARED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(DOCUMENT_VIEW_MERGED_SHARED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);

            if (isDocumentSharedWithAny(document, employees) &&
                    isAnyInAffiliatedOrganizationOfAnyCommunity(employees, mergedCommunities.get()) &&
                    isClientOptedIn(document)
            ) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_VIEW_MERGED_SHARED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = filter.getEmployees(DOCUMENT_VIEW_MERGED_SHARED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);

            if (isDocumentSharedWithAny(document, employees) &&
                    isAnyInAffiliatedCommunityOfAny(employees, mergedCommunities.get()) &&
                    isClientOptedIn(document)) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_VIEW_MERGED_SHARED_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = filter.getEmployees(DOCUMENT_VIEW_MERGED_SHARED_IF_CURRENT_RP_COMMUNITY_CTM);

            if (isDocumentSharedWithAny(document, employees) &&
                    isAnyInAnyCommunityCareTeam(
                            employees,
                            mergedCommunities.get(),
                            AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                            HieConsentCareTeamType.currentForAny(mergedClients.get()))) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_VIEW_MERGED_SHARED_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = filter.getEmployees(DOCUMENT_VIEW_MERGED_SHARED_IF_CURRENT_RP_CLIENT_CTM);

            if (isDocumentSharedWithAny(document, employees) && isAnyInAnyClientCareTeam(
                    employees,
                    mergedClients.get(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentForAny(mergedClients.get()))) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_VIEW_MERGED_SHARED_IF_SELF_RECORD)) {
            var employees = filter.getEmployees(DOCUMENT_VIEW_MERGED_SHARED_IF_SELF_RECORD);

            if (isDocumentSharedWithAny(document, employees) && isAnySelfClientRecord(employees, mergedClients.get())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_VIEW_MERGED_SHARED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = filter.getEmployees(DOCUMENT_VIEW_MERGED_SHARED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);

            if (isDocumentSharedWithAny(document, employees) &&
                    isAnySelfClientRecord(employees, mergedClients.get()) &&
                    areAllClientsOptedIn(mergedClients.get())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_VIEW_MERGED_SHARED_IF_CLIENT_FOUND_IN_RECORD_SEARCH) &&
                filter.containsAnyClientRecordSearchFoundIds(CareCoordinationUtils.toIdsSet(mergedClients.get()))) {
            var employees = filter.getEmployees(DOCUMENT_VIEW_MERGED_SHARED_IF_CLIENT_FOUND_IN_RECORD_SEARCH);
            return isDocumentSharedWithAny(document, employees);
        }

        return false;
    }

    private boolean hasCoordinatorAccessIfLabNotReviewedOrElse(ClientDocumentSecurityAwareEntity document, boolean other) {
        if (document.getDocumentType() == DocumentType.LAB_RESULT) {
            var labResearch = labResearchOrderService.findSecurityAware(document.getLabResearchOrderId());
            boolean labDocumentNotReviewed = labResearch.getStatus() != LabResearchOrderStatus.REVIEWED;
            if (labDocumentNotReviewed) {
                return labSecurityService.canReview(document.getLabResearchOrderId());
            }
        }
        return other;
    }

    @Override
    public boolean canDownloadCcd(long clientId) {
        return canDownloadGenerated(clientId,
                DOCUMENT_VIEW_CCD_ALL_EXCEPT_OPTED_OUT,
                DOCUMENT_VIEW_CCD_IF_ASSOCIATED_ORGANIZATION,
                DOCUMENT_VIEW_CCD_IF_ASSOCIATED_COMMUNITY,
                DOCUMENT_VIEW_CCD_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
                DOCUMENT_VIEW_CCD_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
                DOCUMENT_VIEW_CCD_IF_CURRENT_RP_COMMUNITY_CTM,
                DOCUMENT_VIEW_CCD_IF_CURRENT_RP_CLIENT_CTM,
                DOCUMENT_VIEW_CCD_IF_SELF_RECORD,
                DOCUMENT_VIEW_CCD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                DOCUMENT_VIEW_CCD_IF_SHARED_BY_REFERRAL,
                DOCUMENT_VIEW_CCD_IF_CLIENT_FOUND_IN_RECORD_SEARCH,
                referralService::existsInboundWithEnabledCcd);

    }

    @Override
    public boolean canDownloadFacesheet(long clientId) {
        return canDownloadGenerated(clientId,
                DOCUMENT_VIEW_FACESHEET_ALL_EXCEPT_OPTED_OUT,
                DOCUMENT_VIEW_FACESHEET_IF_ASSOCIATED_ORGANIZATION,
                DOCUMENT_VIEW_FACESHEET_IF_ASSOCIATED_COMMUNITY,
                DOCUMENT_VIEW_FACESHEET_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
                DOCUMENT_VIEW_FACESHEET_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
                DOCUMENT_VIEW_FACESHEET_IF_CURRENT_RP_COMMUNITY_CTM,
                DOCUMENT_VIEW_FACESHEET_IF_CURRENT_RP_CLIENT_CTM,
                DOCUMENT_VIEW_FACESHEET_IF_SELF_RECORD,
                DOCUMENT_VIEW_FACESHEET_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                DOCUMENT_VIEW_FACESHEET_IF_SHARED_BY_REFERRAL,
                DOCUMENT_VIEW_FACESHEET_IF_CLIENT_FOUND_IN_RECORD_SEARCH,
                referralService::existsInboundWithEnabledFacesheet);
    }

    @Override
    public boolean canDownloadServicePlanPdf(long clientId) {
        var client = clientService.findSecurityAwareEntity(clientId);

        if (!isInEligibleForDiscoveryCommunity(client)) {
            return false;
        }

        var filter = currentUserFilter();

        if (filter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        return checkSharedPermission(clientId, filter, DOCUMENT_VIEW_SERVICE_PLAN_PDF_IF_SHARED_BY_REFERRAL,
                referralService::existsInboundWithEnabledServicePlan);
    }

    private boolean canDownloadGenerated(long clientId,
                                         Permission allExceptOptedOut,
                                         Permission associatedOrganization,
                                         Permission associatedCommunity,
                                         Permission optedInFromAffiliatedOrganization,
                                         Permission optedInFromAffiliatedCommunity,
                                         Permission currentRpCommunityCtm,
                                         Permission currentRpClientCtm,
                                         Permission selfRecord,
                                         Permission optedInClientAddedBySelf,
                                         Permission sharedByReferral,
                                         Permission clientFoundInRecordSearch,
                                         BiFunction<Long, PermissionFilter, Boolean> sharedByReferralChecker) {
        var client = clientService.findSecurityAwareEntity(clientId);

        if (!isInEligibleForDiscoveryCommunity(client)) {
            return false;
        }

        var filter = currentUserFilter();

        if (filter.hasPermission(allExceptOptedOut) && isClientOptedIn(client)) {
            return true;
        }

        if (filter.hasPermission(associatedOrganization)) {
            var employees = filter.getEmployees(associatedOrganization);

            if (isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }

        if (filter.hasPermission(associatedCommunity)) {
            var employees = filter.getEmployees(associatedCommunity);

            if (isAnyCreatedUnderCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (filter.hasPermission(optedInFromAffiliatedOrganization)) {
            var employees = filter.getEmployees(optedInFromAffiliatedOrganization);

            if (isClientOptedIn(client) &&
                    isAnyInAffiliatedOrganizationOfCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (filter.hasPermission(optedInFromAffiliatedCommunity)) {
            var employees = filter.getEmployees(optedInFromAffiliatedCommunity);

            if (isClientOptedIn(client) &&
                    isAnyInAffiliatedCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (filter.hasPermission(currentRpCommunityCtm)) {
            var employees = filter.getEmployees(currentRpCommunityCtm);

            if (isAnyInCommunityCareTeam(
                    employees,
                    client.getCommunityId(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (filter.hasPermission(currentRpClientCtm)) {
            var employees = filter.getEmployees(currentRpClientCtm);

            if (isAnyInClientCareTeam(
                    employees,
                    client,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (filter.hasPermission(selfRecord)) {
            var employees = filter.getEmployees(selfRecord);

            if (isSelfClientRecord(employees, client)) {
                return true;
            }
        }

        if (filter.hasPermission(optedInClientAddedBySelf)) {
            var employees = filter.getEmployees(optedInClientAddedBySelf);

            if (isClientOptedInAndAddedBySelf(employees, client)) {
                return true;
            }
        }

        if (checkSharedPermission(clientId, filter, sharedByReferral, sharedByReferralChecker)) {
            return true;
        }

        return filter.hasPermission(clientFoundInRecordSearch) && filter.containsClientRecordSearchFoundId(clientId);
    }

    private boolean checkSharedPermission(long clientId,
                                          PermissionFilter filter,
                                          Permission sharedByReferral,
                                          BiFunction<Long, PermissionFilter, Boolean> sharedByReferralChecker) {
        if (filter.hasPermission(sharedByReferral)) {
            var employees = filter.getEmployees(sharedByReferral);
            var filteredFilter = PermissionFilterUtils.filterWithEmployeesOnly(filter, employees);

            return sharedByReferralChecker.apply(clientId, filteredFilter);
        }
        return false;
    }

    @Override
    public boolean canUpload(ClientDocumentSecurityFieldsAware dto) {
        var client = clientService.findSecurityAwareEntity(dto.getClientId());

        if (!isInEligibleForDiscoveryCommunity(client)) {
            return false;
        }

        var filter = currentUserFilter();

        if (filter.hasPermission(DOCUMENT_ADD_ALL_EXCEPT_OPTED_OUT) && isClientOptedIn(client)) {
            return true;
        }

        if (filter.hasPermission(DOCUMENT_ADD_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(DOCUMENT_ADD_IF_ASSOCIATED_ORGANIZATION);

            if (isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_ADD_IF_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(DOCUMENT_ADD_IF_ASSOCIATED_COMMUNITY);

            if (isAnyCreatedUnderCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
            var employees = filter.getEmployees(DOCUMENT_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM);

            if (isAnyInCommunityCareTeam(
                    employees,
                    client.getCommunityId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_ADD_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = filter.getEmployees(DOCUMENT_ADD_IF_CURRENT_REGULAR_CLIENT_CTM);

            if (isAnyInClientCareTeam(
                    employees,
                    client,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = filter.getEmployees(DOCUMENT_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);

            if (isClientOptedInAndAddedBySelf(employees, client)) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_ADD_IF_SELF_RECORD)) {
            var employees = filter.getEmployees(DOCUMENT_ADD_IF_SELF_RECORD);
            if (isSelfClientRecord(employees, client)) {
                return true;
            }
        }

        return filter.hasPermission(DOCUMENT_ADD_IF_CLIENT_FOUND_IN_RECORD_SEARCH) && filter.containsClientRecordSearchFoundId(dto.getClientId());
    }

    @Override
    public boolean canDelete(long documentId) {
        return canDelete(documentService.findSecurityAwareEntity(documentId));
    }

    @Override
    public boolean canDelete(ClientDocumentSecurityAwareEntity document) {
        return canModify(document);
    }

    @Override
    public boolean canEdit(long documentId) {
        return canEdit(documentService.findSecurityAwareEntity(documentId));
    }

    @Override
    public boolean canEdit(ClientDocumentSecurityAwareEntity document) {
        return canModify(document);
    }

    public boolean canModify(ClientDocumentSecurityAwareEntity document) {
        if (document.getDocumentType() == DocumentType.LAB_RESULT) {
            return false;
        }

        if (document.getClientId() == null) {
            return false;
        }

        if (BooleanUtils.isTrue(document.getIsCloud())) {
            return false;
        }

        if (!isEligibleForDiscoveryCommunity((document.getClientCommunityId()))) {
            return false;
        }

        var filter = currentUserFilter();

        if (filter.hasPermission(DOCUMENT_MODIFY_SHARED_ALL_EXCEPT_OPTED_OUT) && isClientOptedIn(document)) {
            return true;
        }

        var client = lazyClient(document.getClientId());

        if (filter.hasPermission(DOCUMENT_MODIFY_SHARED_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(DOCUMENT_MODIFY_SHARED_IF_ASSOCIATED_ORGANIZATION);

            if (isDocumentSharedWithAny(document, employees) &&
                    isAnyCreatedUnderOrganization(employees, client.get().getOrganizationId())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_MODIFY_SHARED_IF_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(DOCUMENT_MODIFY_SHARED_IF_ASSOCIATED_COMMUNITY);

            if (isDocumentSharedWithAny(document, employees)
                    && isAnyCreatedUnderCommunity(employees, client.get().getCommunityId())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_MODIFY_SHARED_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
            var employees = filter.getEmployees(DOCUMENT_MODIFY_SHARED_IF_CURRENT_REGULAR_COMMUNITY_CTM);

            if (isDocumentSharedWithAny(document, employees) &&
                    isAnyInCommunityCareTeam(
                            employees,
                            client.get().getCommunityId(),
                            AffiliatedCareTeamType.REGULAR,
                            HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_MODIFY_SHARED_ADDED_BY_SELF_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
            var employees = filter.getEmployees(DOCUMENT_MODIFY_SHARED_ADDED_BY_SELF_IF_CURRENT_REGULAR_COMMUNITY_CTM);

            if (isAddedBySelf(document, employees) &&
                    isDocumentSharedWithAny(document, employees) &&
                    isAnyInCommunityCareTeam(
                            employees,
                            client.get().getCommunityId(),
                            AffiliatedCareTeamType.REGULAR,
                            HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_MODIFY_SHARED_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = filter.getEmployees(DOCUMENT_MODIFY_SHARED_IF_CURRENT_REGULAR_CLIENT_CTM);

            if (isDocumentSharedWithAny(document, employees) &&
                    isAnyInClientCareTeam(
                            employees,
                            client.get(),
                            AffiliatedCareTeamType.REGULAR,
                            HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_MODIFY_SHARED_ADDED_BY_SELF_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = filter.getEmployees(DOCUMENT_MODIFY_SHARED_ADDED_BY_SELF_IF_CURRENT_REGULAR_CLIENT_CTM);

            if (isAddedBySelf(document, employees) &&
                    isDocumentSharedWithAny(document, employees) &&
                    isAnyInClientCareTeam(
                            employees,
                            client.get(),
                            AffiliatedCareTeamType.REGULAR,
                            HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_MODIFY_SHARED_ADDED_BY_SELF_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = filter.getEmployees(DOCUMENT_MODIFY_SHARED_ADDED_BY_SELF_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);

            if (isAddedBySelf(document, employees)
                    && isDocumentSharedWithAny(document, employees)
                    && isClientOptedInAndAddedBySelf(employees, client.get())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_MODIFY_SHARED_ADDED_BY_SELF_IF_SELF_RECORD)) {
            var employees = filter.getEmployees(DOCUMENT_MODIFY_SHARED_ADDED_BY_SELF_IF_SELF_RECORD);
            if (isAddedBySelf(document, employees)
                    && isDocumentSharedWithAny(document, employees)
                    && isSelfClientRecord(employees, client.get())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_MODIFY_SHARED_ADDED_BY_SELF_IF_CLIENT_FOUND_IN_RECORD_SEARCH) && filter.containsClientRecordSearchFoundId(document.getClientId())) {
            var employees = filter.getEmployees(DOCUMENT_MODIFY_SHARED_ADDED_BY_SELF_IF_CLIENT_FOUND_IN_RECORD_SEARCH);
            return isAddedBySelf(document, employees) && isDocumentSharedWithAny(document, employees);
        }

        return false;
    }

    private boolean isDocumentSharedWithAny(ClientDocumentSecurityAwareEntity document, Collection<Employee> employees) {
        return document.getEldermarkShared() ||
                employees.stream()
                        .map(Employee::getOrganizationId)
                        .anyMatch(o -> document.getSharedWithOrganizationIds().contains(o));
    }

    private boolean isAddedBySelf(ClientDocumentSecurityAwareEntity document, Collection<Employee> employees) {
        return document.getAuthorId() != null && isSelfEmployeeRecord(employees, document.getAuthorId());
    }
}
