package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.ReferralIdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.dto.ReferralSecurityFieldsAware;
import com.scnsoft.eldermark.beans.security.projection.entity.*;
import com.scnsoft.eldermark.entity.basic.DisplayableNamedEntity;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.entity.referral.ReferralRequestSharedChannel;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.service.*;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("referralSecurityService")
@Transactional(readOnly = true)
public class ReferralSecurityServiceImpl extends BaseSecurityService implements ReferralSecurityService {

    @Autowired
    private ReferralService referralService;

    @Autowired
    private PartnerNetworkService partnerNetworkService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private ExternalEmployeeInboundReferralCommunityService externalEmployeeInboundReferralCommunityService;

    @Autowired
    private MarketplaceCommunitySecurityService marketplaceCommunitySecurityService;

    @Autowired
    private ReferralAttachmentService referralAttachmentService;

    @Autowired
    private ServiceTypeService serviceTypeService;

    @Override
    public boolean canAdd(ReferralSecurityFieldsAware dto) {

        if (CollectionUtils.isNotEmpty(dto.getSharedCommunityIds()) && dto.getMarketplaceCommunityId() != null) {
            throw new BusinessException("Only one value should be present");
        }

        var permissionFilter = currentUserFilter();

        var allowedServices = serviceTypeService.findAllowedForReferral(permissionFilter).stream()
                .map(DisplayableNamedEntity::getId)
                .collect(Collectors.toSet());

        if (!allowedServices.containsAll(dto.getServices())) {
            return false;
        }

        if (CollectionUtils.isNotEmpty(dto.getSharedCommunityIds())) {
            if (!isCommunityInAllNetworks(dto.getReferringCommunityId(), dto.getSharedCommunityIds())) {
                return false;
            }

            if (dto.getServices() != ReferralSecurityFieldsAware.ANY_SERVICES) {
                var targetCommunitiesServicesTypes = communityService.findAllById(dto.getSharedCommunityIds(), CommunityReferralConfigProjection.class).stream()
                        .map(CommunityReferralConfigProjection::getMarketplaceServiceTypes)
                        .flatMap(Collection::stream)
                        .map(DisplayableNamedEntity::getId)
                        .collect(Collectors.toSet());

                if (!targetCommunitiesServicesTypes.containsAll(dto.getServices())) {
                    return false;
                }
            }
        }

        if (dto.getMarketplaceCommunityId() != null) {
            if (!marketplaceCommunitySecurityService.canViewByCommunityId(dto.getMarketplaceCommunityId())) {
                return false;
            }

            var communityConfig = communityService.findById(
                    dto.getMarketplaceCommunityId(),
                    CommunityReferralConfigProjection.class
            );
            if (!communityConfig.isReceiveNonNetworkReferrals() && !isCommunityInAllNetworks(dto.getReferringCommunityId(),
                    Collections.singleton(dto.getMarketplaceCommunityId()))) {
                return false;
            }

            if (dto.getReferringCommunityId().equals(dto.getMarketplaceCommunityId())) {
                return false;
            }

            if (dto.getServices() != ReferralSecurityFieldsAware.ANY_SERVICES) {
                var targetCommunitiesServicesTypes = communityConfig.getMarketplaceServiceTypes().stream()
                        .map(DisplayableNamedEntity::getId)
                        .collect(Collectors.toSet());

                if (!targetCommunitiesServicesTypes.containsAll(dto.getServices())) {
                    return false;
                }
            }
        }

        if (dto.getClientId() != null) {
            var client = clientService.findSecurityAwareEntity(dto.getClientId());

            if (!client.getCommunityId().equals(dto.getReferringCommunityId())) {
                return false;
            }

            return hasCommonAccessToClientOutbound(
                    client,
                    permissionFilter,
                    CLIENT_REFERRAL_ADD_ALL_EXCEPT_OPTED_OUT,
                    CLIENT_REFERRAL_ADD_IF_ASSOCIATED_ORGANIZATION,
                    CLIENT_REFERRAL_ADD_IF_ASSOCIATED_COMMUNITY,
                    CLIENT_REFERRAL_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                    CLIENT_REFERRAL_ADD_IF_CURRENT_REGULAR_CLIENT_CTM,
                    CLIENT_REFERRAL_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                    CLIENT_REFERRAL_ADD_IF_SELF_RECORD
            );
        } else {
            var community = communityService.findSecurityAwareEntity(dto.getReferringCommunityId());

            return hasCommonAccessToB2bOutbound(
                    community,
                    permissionFilter,
                    B2B_REFERRAL_ADD_ALL,
                    B2B_REFERRAL_ADD_IF_ASSOCIATED_ORGANIZATION,
                    B2B_REFERRAL_ADD_IF_ASSOCIATED_COMMUNITY,
                    B2B_REFERRAL_ADD_IF_CO_REGULAR_COMMUNITY_CTM
            );
        }
    }

    private boolean isCommunityInAllNetworks(Long communityId, Collection<Long> networkCommunityIds) {
        return networkCommunityIds.stream()
                .allMatch(networkCommunityId -> partnerNetworkService.areInSameNetwork(
                        communityId,
                        networkCommunityId)
                );
    }

    @Override
    public boolean canAddToCommunity(Long communityId) {
        return hasCommonAccessToOutboundInCommunity(
                communityId,
                CLIENT_REFERRAL_ADD_ALL_EXCEPT_OPTED_OUT,
                CLIENT_REFERRAL_ADD_IF_ASSOCIATED_ORGANIZATION,
                CLIENT_REFERRAL_ADD_IF_ASSOCIATED_COMMUNITY,
                CLIENT_REFERRAL_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                CLIENT_REFERRAL_ADD_IF_CURRENT_REGULAR_CLIENT_CTM,
                CLIENT_REFERRAL_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                B2B_REFERRAL_ADD_ALL,
                B2B_REFERRAL_ADD_IF_ASSOCIATED_ORGANIZATION,
                B2B_REFERRAL_ADD_IF_ASSOCIATED_COMMUNITY,
                B2B_REFERRAL_ADD_IF_CO_REGULAR_COMMUNITY_CTM
        );
    }

    @Override
    public boolean canViewOutboundsInCommunity(Long communityId) {
        return hasCommonAccessToOutboundInCommunity(
                communityId,
                CLIENT_REFERRAL_VIEW_OUTBOUND_ALL_EXCEPT_OPTED_OUT,
                CLIENT_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_ORGANIZATION,
                CLIENT_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_COMMUNITY,
                CLIENT_REFERRAL_VIEW_OUTBOUND_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                CLIENT_REFERRAL_VIEW_OUTBOUND_IF_CURRENT_REGULAR_CLIENT_CTM,
                CLIENT_REFERRAL_VIEW_OUTBOUND_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                B2B_REFERRAL_VIEW_OUTBOUND_ALL,
                B2B_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_ORGANIZATION,
                B2B_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_COMMUNITY,
                B2B_REFERRAL_VIEW_OUTBOUND_IF_CO_REGULAR_COMMUNITY_CTM
        );
    }

    @Override
    public boolean canViewInboundsInCommunity(Long communityId) {
        return hasCommonAccessToInboundInCommunity(
                communityId,
                REFERRAL_VIEW_INBOUND_ALL_EXCEPT_CLIENT_OPTED_OUT,
                REFERRAL_VIEW_INBOUND_IF_ASSOCIATED_ORGANIZATION,
                REFERRAL_VIEW_INBOUND_IF_ASSOCIATED_COMMUNITY,
                REFERRAL_VIEW_INBOUND_IF_CO_REGULAR_COMMUNITY_CTM,
                REFERRAL_VIEW_INBOUND_IF_EXTERNAL_REFERRAL_REQUEST_EXCEPT_CLIENT_OPTED_OUT
        );
    }

    @Override
    public boolean canViewOutbound(Long referralId) {
        return hasCommonAccessToOutbound(
                referralService.findSecurityAwareEntity(referralId),
                CLIENT_REFERRAL_VIEW_OUTBOUND_ALL_EXCEPT_OPTED_OUT,
                CLIENT_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_ORGANIZATION,
                CLIENT_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_COMMUNITY,
                CLIENT_REFERRAL_VIEW_OUTBOUND_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                CLIENT_REFERRAL_VIEW_OUTBOUND_IF_CURRENT_REGULAR_CLIENT_CTM,
                CLIENT_REFERRAL_VIEW_OUTBOUND_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                B2B_REFERRAL_VIEW_OUTBOUND_ALL,
                B2B_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_ORGANIZATION,
                B2B_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_COMMUNITY,
                B2B_REFERRAL_VIEW_OUTBOUND_IF_CO_REGULAR_COMMUNITY_CTM
        );
    }

    @Override
    public boolean canViewOutboundList() {
        return hasAnyPermission(Arrays.asList(
                CLIENT_REFERRAL_VIEW_OUTBOUND_ALL_EXCEPT_OPTED_OUT,
                CLIENT_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_ORGANIZATION,
                CLIENT_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_COMMUNITY,
                CLIENT_REFERRAL_VIEW_OUTBOUND_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                CLIENT_REFERRAL_VIEW_OUTBOUND_IF_CURRENT_REGULAR_CLIENT_CTM,
                CLIENT_REFERRAL_VIEW_OUTBOUND_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                B2B_REFERRAL_VIEW_OUTBOUND_ALL,
                B2B_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_ORGANIZATION,
                B2B_REFERRAL_VIEW_OUTBOUND_IF_ASSOCIATED_COMMUNITY,
                B2B_REFERRAL_VIEW_OUTBOUND_IF_CO_REGULAR_COMMUNITY_CTM
        ));
    }

    @Override
    public boolean canViewInbound(Long requestId) {
        return hasCommonAccessToInbound(
                loadReferralRequest(requestId),
                REFERRAL_VIEW_INBOUND_ALL_EXCEPT_CLIENT_OPTED_OUT,
                REFERRAL_VIEW_INBOUND_IF_ASSOCIATED_ORGANIZATION,
                REFERRAL_VIEW_INBOUND_IF_ASSOCIATED_COMMUNITY,
                REFERRAL_VIEW_INBOUND_IF_CO_REGULAR_COMMUNITY_CTM,
                REFERRAL_VIEW_INBOUND_IF_EXTERNAL_REFERRAL_REQUEST_EXCEPT_CLIENT_OPTED_OUT,
                null
        );
    }

    @Override
    public boolean canViewInboundList() {
        return hasAnyPermission(Arrays.asList(
                REFERRAL_VIEW_INBOUND_ALL_EXCEPT_CLIENT_OPTED_OUT,
                REFERRAL_VIEW_INBOUND_IF_ASSOCIATED_ORGANIZATION,
                REFERRAL_VIEW_INBOUND_IF_ASSOCIATED_COMMUNITY,
                REFERRAL_VIEW_INBOUND_IF_CO_REGULAR_COMMUNITY_CTM,
                REFERRAL_VIEW_INBOUND_IF_EXTERNAL_REFERRAL_REQUEST_EXCEPT_CLIENT_OPTED_OUT
        ));
    }

    @Override
    public boolean canRequestInfo(Long requestId) {
        return hasCommonAccessToInbound(
                loadReferralRequest(requestId),
                REFERRAL_ADD_INFO_REQUEST_ALL_EXCEPT_CLIENT_OPTED_OUT,
                REFERRAL_ADD_INFO_REQUEST_IF_ASSOCIATED_ORGANIZATION,
                REFERRAL_ADD_INFO_REQUEST_IF_ASSOCIATED_COMMUNITY,
                REFERRAL_ADD_INFO_REQUEST_IF_CO_REGULAR_COMMUNITY_CTM,
                REFERRAL_ADD_INFO_REQUEST_IF_EXTERNAL_REFERRAL_REQUEST_EXCEPT_CLIENT_OPTED_OUT,
                null
        );
    }

    @Override
    public boolean canViewInboundInfoRequest(Long infoRequestId) {
        var request = referralService.findReferralRequestSecurityByInfoRequestId(infoRequestId);
        return hasCommonAccessToInbound(
                request,
                REFERRAL_VIEW_INBOUND_INFO_REQUEST_ALL_EXCEPT_CLIENT_OPTED_OUT,
                REFERRAL_VIEW_INBOUND_INFO_REQUEST_IF_ASSOCIATED_ORGANIZATION,
                REFERRAL_VIEW_INBOUND_INFO_REQUEST_IF_ASSOCIATED_COMMUNITY,
                REFERRAL_VIEW_INBOUND_INFO_REQUEST_IF_CO_REGULAR_COMMUNITY_CTM,
                REFERRAL_VIEW_INBOUND_INFO_REQUEST_IF_EXTERNAL_REFERRAL_REQUEST_EXCEPT_CLIENT_OPTED_OUT,
                null
        );
    }

    @Override
    public boolean canViewInboundInfoRequestList() {
        return hasAnyPermission(Arrays.asList(ROLE_SUPER_ADMINISTRATOR,
                REFERRAL_VIEW_INBOUND_INFO_REQUEST_IF_ASSOCIATED_ORGANIZATION,
                REFERRAL_VIEW_INBOUND_INFO_REQUEST_IF_ASSOCIATED_COMMUNITY,
                REFERRAL_VIEW_INBOUND_INFO_REQUEST_IF_CO_REGULAR_COMMUNITY_CTM,
                REFERRAL_VIEW_INBOUND_INFO_REQUEST_IF_EXTERNAL_REFERRAL_REQUEST_EXCEPT_CLIENT_OPTED_OUT));
    }

    @Override
    public boolean canViewOutboundInfoRequest(Long infoRequestId) {
        var referral = referralService.findReferralSecurityByInfoRequestId(infoRequestId);

        return hasCommonAccessToOutbound(referral,
                CLIENT_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_ALL_EXCEPT_OPTED_OUT,
                CLIENT_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_IF_ASSOCIATED_ORGANIZATION,
                CLIENT_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_IF_ASSOCIATED_COMMUNITY,
                CLIENT_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                CLIENT_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_IF_CURRENT_REGULAR_CLIENT_CTM,
                CLIENT_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                B2B_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_ALL,
                B2B_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_IF_ASSOCIATED_ORGANIZATION,
                B2B_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_IF_ASSOCIATED_COMMUNITY,
                B2B_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_IF_CO_REGULAR_COMMUNITY_CTM
        );
    }

    @Override
    public boolean canViewOutboundInfoRequestList() {
        return hasAnyPermission(Arrays.asList(
                CLIENT_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_ALL_EXCEPT_OPTED_OUT,
                CLIENT_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_IF_ASSOCIATED_ORGANIZATION,
                CLIENT_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_IF_ASSOCIATED_COMMUNITY,
                CLIENT_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                CLIENT_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_IF_CURRENT_REGULAR_CLIENT_CTM,
                CLIENT_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                B2B_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_ALL,
                B2B_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_IF_ASSOCIATED_ORGANIZATION,
                B2B_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_IF_ASSOCIATED_COMMUNITY,
                B2B_REFERRAL_VIEW_OUTBOUND_INFO_REQUEST_IF_CO_REGULAR_COMMUNITY_CTM
        ));
    }

    @Override
    public boolean canRespondToInfoRequest(Long infoRequestId) {
        var referral = referralService.findReferralSecurityByInfoRequestId(infoRequestId);

        return hasCommonAccessToOutbound(
                referral,
                CLIENT_REFERRAL_RESPOND_OUTBOUND_INFO_REQUEST_ALL_EXCEPT_OPTED_OUT,
                CLIENT_REFERRAL_RESPOND_OUTBOUND_INFO_REQUEST_IF_ASSOCIATED_ORGANIZATION,
                CLIENT_REFERRAL_RESPOND_OUTBOUND_INFO_REQUEST_IF_ASSOCIATED_COMMUNITY,
                CLIENT_REFERRAL_RESPOND_OUTBOUND_INFO_REQUEST_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                CLIENT_REFERRAL_RESPOND_OUTBOUND_INFO_REQUEST_IF_CURRENT_REGULAR_CLIENT_CTM,
                CLIENT_REFERRAL_RESPOND_OUTBOUND_INFO_REQUEST_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                B2B_REFERRAL_RESPOND_OUTBOUND_INFO_REQUEST_ALL,
                B2B_REFERRAL_RESPOND_OUTBOUND_INFO_REQUEST_IF_ASSOCIATED_ORGANIZATION,
                B2B_REFERRAL_RESPOND_OUTBOUND_INFO_REQUEST_IF_ASSOCIATED_COMMUNITY,
                B2B_REFERRAL_RESPOND_OUTBOUND_INFO_REQUEST_IF_CO_REGULAR_COMMUNITY_CTM
        );
    }

    @Override
    public boolean canPreadmit(Long requestId) {
        return hasCommonAccessToInbound(loadReferralRequest(requestId),
                REFERRAL_PRE_ADMIT_INBOUND_ALL_EXCEPT_CLIENT_OPTED_OUT,
                REFERRAL_PRE_ADMIT_INBOUND_IF_ASSOCIATED_ORGANIZATION,
                REFERRAL_PRE_ADMIT_INBOUND_IF_ASSOCIATED_COMMUNITY,
                REFERRAL_PRE_ADMIT_INBOUND_IF_CO_REGULAR_COMMUNITY_CTM,
                REFERRAL_PRE_ADMIT_INBOUND_IF_EXTERNAL_REFERRAL_REQUEST_EXCEPT_CLIENT_OPTED_OUT,
                null);
    }

    @Override
    public boolean canDecline(Long requestId) {
        return hasCommonAccessToInbound(
                loadReferralRequest(requestId),
                REFERRAL_DECLINE_INBOUND_ALL_EXCEPT_CLIENT_OPTED_OUT,
                REFERRAL_DECLINE_INBOUND_IF_ASSOCIATED_ORGANIZATION,
                REFERRAL_DECLINE_INBOUND_IF_ASSOCIATED_COMMUNITY,
                REFERRAL_DECLINE_INBOUND_IF_CO_REGULAR_COMMUNITY_CTM,
                REFERRAL_DECLINE_INBOUND_IF_EXTERNAL_REFERRAL_REQUEST_EXCEPT_CLIENT_OPTED_OUT,
                REFERRAL_DECLINE_INBOUND_IF_FAX_SHARED
        );
    }

    @Override
    public boolean canAccept(Long requestId) {
        return hasCommonAccessToInbound(
                loadReferralRequest(requestId),
                REFERRAL_ACCEPT_INBOUND_ALL_EXCEPT_CLIENT_OPTED_OUT,
                REFERRAL_ACCEPT_INBOUND_IF_ASSOCIATED_ORGANIZATION,
                REFERRAL_ACCEPT_INBOUND_IF_ASSOCIATED_COMMUNITY,
                REFERRAL_ACCEPT_INBOUND_IF_CO_REGULAR_COMMUNITY_CTM,
                REFERRAL_ACCEPT_INBOUND_IF_EXTERNAL_REFERRAL_REQUEST_EXCEPT_CLIENT_OPTED_OUT,
                REFERRAL_ACCEPT_INBOUND_IF_FAX_SHARED
        );
    }

    @Override
    public boolean canCancel(Long referralId) {
        var referral = referralService.findSecurityAwareEntity(referralId);
        var hasCommonAccess = hasCommonAccessToOutbound(
                referral,
                CLIENT_REFERRAL_CANCEL_OUTBOUND_ALL_EXCEPT_OPTED_OUT,
                CLIENT_REFERRAL_CANCEL_OUTBOUND_IF_ASSOCIATED_ORGANIZATION,
                CLIENT_REFERRAL_CANCEL_OUTBOUND_IF_ASSOCIATED_COMMUNITY,
                CLIENT_REFERRAL_CANCEL_OUTBOUND_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                CLIENT_REFERRAL_CANCEL_OUTBOUND_IF_CURRENT_REGULAR_CLIENT_CTM,
                null,
                B2B_REFERRAL_CANCEL_OUTBOUND_ALL,
                B2B_REFERRAL_CANCEL_OUTBOUND_IF_ASSOCIATED_ORGANIZATION,
                B2B_REFERRAL_CANCEL_OUTBOUND_IF_ASSOCIATED_COMMUNITY,
                B2B_REFERRAL_CANCEL_OUTBOUND_IF_CO_REGULAR_COMMUNITY_CTM
        );

        if (hasCommonAccess) {
            return true;
        }

        var filter = currentUserFilter();

        if (filter.hasPermission(REFERRAL_CANCEL_OUTBOUND_IF_CREATED_BY_SELF)) {
            var employees = filter.getEmployees(REFERRAL_CANCEL_OUTBOUND_IF_CREATED_BY_SELF);
            if (isSelfEmployeeRecord(employees, referral.getRequestingEmployeeId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canReassign(Long requestId) {
        return hasCommonAccessToInbound(
                loadReferralRequest(requestId),
                REFERRAL_REASSIGN_INBOUND_ALL_EXCEPT_CLIENT_OPTED_OUT,
                REFERRAL_REASSIGN_INBOUND_IF_ASSOCIATED_ORGANIZATION,
                REFERRAL_REASSIGN_INBOUND_IF_ASSOCIATED_COMMUNITY,
                REFERRAL_REASSIGN_INBOUND_IF_CO_REGULAR_COMMUNITY_CTM,
                null,
                null
        );
    }

    public boolean hasAddPermissions() {
        return hasAnyPermission(Set.of(
                CLIENT_REFERRAL_ADD_ALL_EXCEPT_OPTED_OUT,
                CLIENT_REFERRAL_ADD_IF_ASSOCIATED_ORGANIZATION,
                CLIENT_REFERRAL_ADD_IF_ASSOCIATED_COMMUNITY,
                CLIENT_REFERRAL_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                CLIENT_REFERRAL_ADD_IF_CURRENT_REGULAR_CLIENT_CTM,
                CLIENT_REFERRAL_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                B2B_REFERRAL_ADD_ALL,
                B2B_REFERRAL_ADD_IF_ASSOCIATED_ORGANIZATION,
                B2B_REFERRAL_ADD_IF_ASSOCIATED_COMMUNITY,
                B2B_REFERRAL_ADD_IF_CO_REGULAR_COMMUNITY_CTM
        ));
    }

    @Override
    public boolean canDownloadOutboundAttachment(Long attachmentId) {
        return canViewOutbound(referralAttachmentService.findReferralIdAwareById(attachmentId).getReferralId());
    }

    @Override
    public boolean canDownloadInboundAttachment(Long requestId, Long attachmentId) {
        return canViewInbound(requestId) &&
                referralService.findRequestById(requestId, ReferralIdAware.class).getReferralId()
                        .equals(
                                referralAttachmentService.findReferralIdAwareById(attachmentId).getReferralId()
                        );
    }

    //Use this method only if security checks are the same on all levels
    private boolean hasCommonAccessToInbound(ReferralRequestSecurityAwareEntity request,
                                             Permission allExceptOptedOut,
                                             Permission associatedOrganization,
                                             Permission associatedCommunity,
                                             Permission regularCoCommunityCtm,
                                             Permission externalReferralRequest,
                                             Permission faxShared) {

        var community = communityService.findSecurityAwareEntity(request.getCommunityId());
        if (!isEligibleForDiscoveryCommunity(community)) {
            return false;
        }

        var filter = currentUserFilter();

        if (filter.hasPermission(allExceptOptedOut)) {
            if (request.getReferralClientId() == null) {
                return true;
            } else if (request.getReferralClientHieConsentPolicyType() == HieConsentPolicyType.OPT_IN) {
                return true;
            }
        }

        if (filter.hasPermission(associatedOrganization)) {
            var employees = filter.getEmployees(associatedOrganization);

            if (isAnyCreatedUnderOrganization(employees, community.getOrganizationId())) {
                return true;
            }
        }

        if (filter.hasPermission(associatedCommunity)) {
            var employees = filter.getEmployees(associatedCommunity);

            if (isAnyCreatedUnderCommunity(employees, request.getCommunityId())) {
                return true;
            }
        }

        if (filter.hasPermission(regularCoCommunityCtm)) {
            var employees = filter.getEmployees(regularCoCommunityCtm);

            if (isAnyInCommunityCareTeam(
                    employees,
                    request.getCommunityId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (externalReferralRequest != null && filter.hasPermission(externalReferralRequest)) {
            var employees = filter.getEmployees(externalReferralRequest);
            if (externalEmployeeInboundReferralCommunityService.isCommunitySharedForAnyEmployee(
                    CareCoordinationUtils.toIdsSet(employees),
                    request.getCommunityId())) {
                if (request.getReferralClientId() == null) {
                    return true;
                } else if (request.getReferralClientHieConsentPolicyType() == HieConsentPolicyType.OPT_IN) {
                    return true;
                }
            }
        }

        if (faxShared != null && filter.hasPermission(faxShared)) {
            return ReferralRequestSharedChannel.FAX == request.getSharedChannel() && canViewOutbound(request.getReferralId());
        }

        return false;
    }

    private boolean hasCommonAccessToInboundInCommunity(Long communityId,
                                                        Permission allExceptClientOptedOut,
                                                        Permission associatedOrganization,
                                                        Permission associatedCommunity,
                                                        Permission currentCoRegularCommunityCtm,
                                                        Permission ifExternalReferralRequestExceptClientOptedOut) {

        return hasCommonAccessToInbound(
                new ReferralRequestSecurityAwareEntity() {
                    @Override
                    public HieConsentPolicyType getReferralClientHieConsentPolicyType() {
                        return null;
                    }

                    @Override
                    public Long getReferralClientId() {
                        return null;
                    }

                    @Override
                    public Long getCommunityId() {
                        return communityId;
                    }

                    @Override
                    public Long getReferralId() {
                        return null;
                    }

                    @Override
                    public ReferralRequestSharedChannel getSharedChannel() {
                        return null;
                    }
                },
                allExceptClientOptedOut,
                associatedOrganization,
                associatedCommunity,
                currentCoRegularCommunityCtm,
                ifExternalReferralRequestExceptClientOptedOut,
                null
        );
    }

    private boolean hasCommonAccessToOutbound(
            ReferralSecurityAwareEntity referral,
            Permission clientAllExceptOptedOut,
            Permission clientAssociatedOrganization,
            Permission clientAssociatedCommunity,
            Permission clientCurrentRegularCommunityCtm,
            Permission clientCurrentRegularClientCtm,
            Permission clientAddedBySelf,
            Permission b2bAll,
            Permission b2bAssociatedOrganization,
            Permission b2bAssociatedCommunity,
            Permission b2bCoRegularCommunityCtm
    ) {
        if (referral.getClientId() != null) {
            return hasCommonAccessToClientOutbound(
                    clientService.findSecurityAwareEntity(referral.getClientId()),
                    currentUserFilter(),
                    clientAllExceptOptedOut,
                    clientAssociatedOrganization,
                    clientAssociatedCommunity,
                    clientCurrentRegularCommunityCtm,
                    clientCurrentRegularClientCtm,
                    clientAddedBySelf,
                    null
            );
        } else {
            return hasCommonAccessToB2bOutbound(
                    communityService.findSecurityAwareEntity(referral.getRequestingCommunityId()),
                    currentUserFilter(),
                    b2bAll,
                    b2bAssociatedOrganization,
                    b2bAssociatedCommunity,
                    b2bCoRegularCommunityCtm
            );
        }
    }

    private boolean hasCommonAccessToB2bOutbound(
            CommunitySecurityAwareEntity community,
            PermissionFilter filter,
            Permission allPermission,
            Permission associatedOrganizationPermission,
            Permission associatedCommunityPermission,
            Permission coRegularCommunityCtm
    ) {
        if (!isEligibleForDiscoveryCommunity(community)) {
            return false;
        }

        if (filter.hasPermission(allPermission)) {
            return true;
        }

        if (filter.hasPermission(associatedOrganizationPermission)) {
            var employees = filter.getEmployees(associatedOrganizationPermission);

            if (isAnyCreatedUnderOrganization(employees, community.getOrganizationId())) {
                return true;
            }
        }

        if (filter.hasPermission(associatedCommunityPermission)) {
            var employees = filter.getEmployees(associatedCommunityPermission);

            if (isAnyCreatedUnderCommunity(employees, community.getId())) {
                return true;
            }
        }

        if (filter.hasPermission(coRegularCommunityCtm)) {
            var employees = filter.getEmployees(coRegularCommunityCtm);

            if (isAnyInCommunityCareTeam(
                    employees,
                    community.getId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        return false;
    }

    private boolean hasCommonAccessToClientOutbound(
            ClientSecurityAwareEntity client,
            PermissionFilter filter,
            Permission allExceptOptedOut,
            Permission associatedOrganization,
            Permission associatedCommunity,
            Permission currentRegularCommunityCtm,
            Permission currentRegularClientCtm,
            Permission optedInClientAddedBySelf,
            Permission ifSelfRecordPermission
    ) {
        if (!isInEligibleForDiscoveryCommunity(client)) {
            return false;
        }

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

        if (filter.hasPermission(currentRegularCommunityCtm)) {
            var employees = filter.getEmployees(currentRegularCommunityCtm);

            if (isAnyInCommunityCareTeam(
                    employees,
                    client.getCommunityId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (filter.hasPermission(currentRegularClientCtm)) {
            var employees = filter.getEmployees(currentRegularClientCtm);

            if (isAnyInClientCareTeam(
                    employees,
                    client,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (optedInClientAddedBySelf != null && filter.hasPermission(optedInClientAddedBySelf)) {
            var employees = filter.getEmployees(optedInClientAddedBySelf);

            if (isClientOptedInAndAddedBySelf(employees, client)) {
                return true;
            }
        }

        if (ifSelfRecordPermission != null && filter.hasPermission(ifSelfRecordPermission)) {
            var employees = filter.getEmployees(ifSelfRecordPermission);
            if (isSelfClientRecord(employees, client.getId())) {
                return true;
            }
        }

        return false;
    }

    private boolean hasCommonAccessToOutboundInCommunity(Long communityId,
                                                         Permission clientAllExceptOptedOut,
                                                         Permission clientAssociatedOrganization,
                                                         Permission clientAssociatedCommunity,
                                                         Permission clientCurrentRegularCommunityCtm,
                                                         Permission clientCurrentRegularClientCtm,
                                                         Permission clientOptedInAddedBySelf,
                                                         Permission b2bAll,
                                                         Permission b2bAssociatedOrganization,
                                                         Permission b2bAssociatedCommunity,
                                                         Permission b2bCoRegularCommunityCtm) {

        var clientExistsInCommunityLazy = Lazy.of(() -> !clientService.existInCommunity(communityId));

        var community = communityService.findSecurityAwareEntity(communityId);

        if (!isEligibleForDiscoveryCommunity(community)) {
            return false;
        }

        var filter = currentUserFilter();

        if (filter.hasPermission(b2bAll)) {
            return true;
        }

        if (filter.hasPermission(clientAllExceptOptedOut) && clientService.existOptedInInCommunity(communityId)) {
            return true;
        }

        if (filter.hasPermission(b2bAssociatedOrganization)) {
            var employees = filter.getEmployees(clientAssociatedOrganization);
            if (isAnyCreatedUnderOrganization(employees, community.getOrganizationId())) {
                return true;
            }
        }

        if (filter.hasPermission(clientAssociatedOrganization)) {
            var employees = filter.getEmployees(clientAssociatedOrganization);

            if (isAnyCreatedUnderOrganization(employees, community.getOrganizationId()) && clientExistsInCommunityLazy.get()) {
                return true;
            }
        }

        if (filter.hasPermission(b2bAssociatedCommunity)) {
            var employees = filter.getEmployees(b2bAssociatedCommunity);

            if (isAnyCreatedUnderCommunity(employees, communityId)) {
                return true;
            }
        }

        if (filter.hasPermission(clientAssociatedCommunity)) {
            var employees = filter.getEmployees(clientAssociatedCommunity);

            if (isAnyCreatedUnderCommunity(employees, communityId) && clientExistsInCommunityLazy.get()) {
                return true;
            }
        }

        if (filter.hasPermission(b2bCoRegularCommunityCtm)) {
            var employees = filter.getEmployees(b2bCoRegularCommunityCtm);

            if (isAnyInCommunityCareTeam(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (filter.hasPermission(clientCurrentRegularCommunityCtm)) {
            var employees = filter.getEmployees(clientCurrentRegularCommunityCtm);

            if (isAnyInCommunityCareTeam(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.current(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID))) {
                return true;
            }
        }

        if (filter.hasPermission(clientCurrentRegularClientCtm)) {
            var employees = filter.getEmployees(clientCurrentRegularClientCtm);

            if (isAnyInAnyClientCareTeamOfCommunity(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.current(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID))) {
                return true;
            }
        }

        if (clientOptedInAddedBySelf != null && filter.hasPermission(clientOptedInAddedBySelf)) {
            var employees = filter.getEmployees(clientOptedInAddedBySelf);

            if (isAnyClientOptedInAndAddedBySelfInCommunity(employees, communityId)) {
                return true;
            }
        }

        return false;
    }

    private ReferralRequestSecurityAwareEntity loadReferralRequest(Long id) {
        return referralService.findReferralRequestSecurityEntity(id);
    }
}
