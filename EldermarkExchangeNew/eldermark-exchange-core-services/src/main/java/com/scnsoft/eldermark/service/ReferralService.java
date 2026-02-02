package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ReferralFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ReferralRequestSecurityAwareEntity;
import com.scnsoft.eldermark.beans.security.projection.entity.ReferralSecurityAwareEntity;
import com.scnsoft.eldermark.dao.referral.ReferralListItemAware;
import com.scnsoft.eldermark.dao.referral.ReferralRequestListItemAware;
import com.scnsoft.eldermark.dao.referral.ReferralRequestReferralCommunityAware;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.CommunityName;
import com.scnsoft.eldermark.entity.network.PartnerNetwork;
import com.scnsoft.eldermark.entity.referral.Referral;
import com.scnsoft.eldermark.entity.referral.ReferralAction;
import com.scnsoft.eldermark.entity.referral.ReferralInfoRequest;
import com.scnsoft.eldermark.entity.referral.ReferralRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.List;

public interface ReferralService extends
        SecurityAwareEntityService<ReferralSecurityAwareEntity, Long> {

    List<ReferralRequest> createRequestsWithinSystem(Community community, List<Long> serviceIds);

    ReferralRequest createRequestForNetwork(Community community, Collection<PartnerNetwork> partnerNetworks);

    ReferralRequest createRequest(Community community);

    Page<ReferralInfoRequest> findInboundInfoRequests(Long requestId, PermissionFilter permissionFilter, Pageable pageable);

    Page<ReferralInfoRequest> findOutboundInfoRequests(Long referralId, PermissionFilter permissionFilter, Pageable pageable);

    ReferralInfoRequest saveInfoRequest(ReferralInfoRequest referralInfoRequest);

    ReferralInfoRequest respondToInfoRequest(ReferralInfoRequest referralInfoRequest);

    ReferralInfoRequest findInfoRequest(Long infoRequestId);

    void changeAssignee(ReferralRequest request, Long employeeId);

    List<Employee> findPossibleAssignees(ReferralRequest request, Sort sort);

    Page<ReferralListItemAware> findListItemOutbounds(ReferralFilter filter, PermissionFilter permissionFilter, Pageable pageable);

    List<CommunityName> findRecipients(ReferralFilter filter, PermissionFilter permissionFilter);

    Page<ReferralRequestListItemAware> findListItemInbounds(ReferralFilter filter, PermissionFilter permissionFilter, Pageable pageable);

    Page<ReferralRequestReferralCommunityAware> findCommunitiesInbound(ReferralFilter filter, PermissionFilter permissionFilter, Pageable pageable);

    Referral findById(Long id);

    ReferralRequest findRequestById(Long id);

    Referral getById(Long id);

    ReferralRequest getRequestById(Long requestId);

    Referral submit(Referral referral);

    Page<ReferralRequest> findOutboundRequests(Long referralId, Pageable pageable);

    void preadmit(ReferralRequest referralRequest, Employee loggedUser);

    void accept(ReferralRequest referralRequest, Employee loggedUser, Long serviceStartDate, Long serviceEndDate, String comment);

    void decline(ReferralRequest referralRequest, Employee loggedUser, Long referralDeclineReasonId, String comment);

    void cancel(Referral referral, Employee loggedUser);

    boolean existsInboundWithEnabledFacesheet(Long clientId, PermissionFilter permissionFilter);

    boolean existsInboundWithEnabledCcd(Long clientId, PermissionFilter permissionFilter);

    boolean existsInboundWithEnabledServicePlan(Long clientId, PermissionFilter permissionFilter);

    boolean isAvailableAction(Referral referral, ReferralAction action);

    boolean isAvailableAction(ReferralRequest referralRequest, ReferralAction action);

    boolean isVisibleInboundByStatus(ReferralRequest request);

    ReferralSecurityAwareEntity findReferralSecurityByInfoRequestId(Long infoRequestId);

    ReferralRequestSecurityAwareEntity findReferralRequestSecurityByInfoRequestId(Long infoRequestId);

    ReferralRequestSecurityAwareEntity findReferralRequestSecurityEntity(Long requestId);

    boolean existsAccessibleReferralRequest(Long clientId, PermissionFilter permissionFilter);

    <P> P findRequestById(Long requestId, Class<P> projection);

    <P> P findReferralById(Long referralId, Class<P> projection);
}
