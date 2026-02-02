package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ReferralFilter;
import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ReferralRequestSecurityAwareEntity;
import com.scnsoft.eldermark.beans.security.projection.entity.ReferralSecurityAwareEntity;
import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.dao.referral.*;
import com.scnsoft.eldermark.dao.specification.EmployeeSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ReferralSpecificationGenerator;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.ValueSet;
import com.scnsoft.eldermark.entity.basic.DisplayableNamedEntity;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.CommunityName;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.entity.marketplace.ServiceType;
import com.scnsoft.eldermark.entity.network.PartnerNetwork;
import com.scnsoft.eldermark.entity.referral.*;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.utils.CustomSortUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReferralServiceImpl implements ReferralService {

    @Autowired
    private ReferralDao referralDao;

    @Autowired
    private ReferralSpecificationGenerator referralSpecificationGenerator;

    @Autowired
    private ReferralInfoRequestDao referralInfoRequestDao;

    @Autowired
    private ReferralRequestDao referralRequestDao;

    @Autowired
    private EmployeeSpecificationGenerator employeeSpecificationGenerator;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private ReferralRequestAssignedHistoryDao referralRequestAssignedHistoryDao;

    @Autowired
    private ReferralRequestResponseDao referralRequestResponseDao;

    @Autowired
    private ReferralDeclineReasonDao referralDeclineReasonDao;

    @Autowired
    private ReferralNotificationService referralNotificationService;

    @Autowired
    private PartnerNetworkService partnerNetworkService;

    @Override
    public Referral findById(Long id) {
        return referralDao.findById(id).orElseThrow();
    }

    public ReferralRequest findRequestById(Long id) {
        return referralRequestDao.findById(id).orElseThrow();
    }

    @Override
    public Referral getById(Long id) {
        return referralDao.getOne(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ReferralRequest getRequestById(Long requestId) {
        return referralRequestDao.getOne(requestId);
    }

    @Override
    public Referral submit(Referral referral) {
        cleanupCommunity(referral);
        validateSubmit(referral);
        var result = referralDao.save(referral);
        referralNotificationService.sendSubmitNotifications(referral);
        return result;
    }

    @Override
    public Page<ReferralRequest> findOutboundRequests(Long referralId, Pageable pageable) {
        var requestsOfReferral = referralSpecificationGenerator.requestsOfReferral(referralId);
        var withExpressionSort = CustomSortUtils.<ReferralRequest>withExpressionSort(pageable.getSort());

        return referralRequestDao.findAll(requestsOfReferral.and(withExpressionSort), CustomSortUtils.unsortedPage(pageable));
    }

    private void validateSubmit(Referral referral) {
        if (StringUtils.isNotEmpty(referral.getCategoryOtherText()) &&
                referral.getCategories().stream().map(ReferralCategory::getCode).noneMatch("OTHER_OTHER"::equals)) {
            invalid("Plain text in category is provided, but Other is not selected");
        }

        if (referral.isMarketplace()) {
            if (referral.getReferralRequests().size() != 1) {
                invalid("Marketplace referral must contain exactly one ReferralRequest");
            }
            var marketplaceCommunity = referral.getReferralRequests().get(0).getCommunity();

            //referral to marketplace community which isReceiveNonNetworkReferrals=false can be
            //created from community which is in the same network
            if (!marketplaceCommunity.isReceiveNonNetworkReferrals() &&
                    !partnerNetworkService.areInSameNetwork(referral.getRequestingCommunityId(), marketplaceCommunity.getId())) {
                invalid("Attempt to create marketplace referral with isReceiveNonNetworkReferrals=false and client community not in the same network");
            }

        } else {
            //Validation that client's community is in request's networks is done in createRequestForNetwork.
            var withoutNetworks = referral.getReferralRequests().stream().map(ReferralRequest::getPartnerNetworks).anyMatch(CollectionUtils::isEmpty);

            if (withoutNetworks && partnerNetworkService.existsForCommunity(referral.getRequestingCommunityId())) {
                invalid("Attempt to create referral request without networks for client, whose community is in network");
            }
        }


        if (!CollectionUtils.emptyIfNull(referral.getReferralReasons())
                .stream()
                .map(code -> code.getValueSets().stream().map(ValueSet::getOid).collect(Collectors.toList()))
                .allMatch(valueSetList -> valueSetList.contains(ValueSetEnum.PROCEDURE_REASON.getOid()))) {
            invalid("Provided referral reason is not from 'Procedure Reason' value set");
        }

        var serviceTypes = referral.getServices();
        var serviceFromEntities = serviceTypes.stream()
                .filter(Objects::nonNull)
                .map(DisplayableNamedEntity::getDisplayName)
                .distinct()
                .collect(Collectors.joining(", "));

        if (!serviceFromEntities.equals(referral.getServiceName())) {
            invalid("Entity services' name doesn't name service name from referral");
        }

        if (!serviceTypes.stream().allMatch(ServiceType::getIsClientRelated)) {
            if (referral.getClient() != null
                    || referral.getClientLocation() != null
                    || referral.getLocationPhone() != null
                    || referral.getAddress() != null
                    || referral.getCity() != null
                    || referral.getState() != null
                    || referral.getZipCode() != null
                    || referral.getInNetworkInsurance() != null
            ) {
                invalid("Selected services doesn't allow to add client details");
            }
        }

        if (!serviceTypes.stream().allMatch(ServiceType::getCanAdditionalClinicalInfoBeShared)) {
            if (referral.isCcdShared() || referral.isFacesheetShared() || referral.isServicePlanShared()) {
                invalid("Selected services doesn't allow share additional clinical info");
            }
        }

        var client = referral.getClient();
        if (client != null) {
            if (HieConsentPolicyType.OPT_OUT.equals(client.getHieConsentPolicyType()) && referral.isCcdShared()) {
                invalid("CCD cannot be shared because client is opted out");
            }
        }

        if (CollectionUtils.isEmpty(referral.getReferralRequests())) {
            invalid("There is no community to accept the request");
        }
    }

    private void cleanupCommunity(Referral referral) {
        var communityId = referral.getRequestingCommunityId();
        var filteredRequests = referral.getReferralRequests().stream()
                .filter(r -> !r.getCommunity().getId().equals(communityId))
                .collect(Collectors.toList());
        referral.setReferralRequests(filteredRequests);
    }

    private void invalid(String message) {
        throw new ValidationException(message);
    }

    @Override
    public List<ReferralRequest> createRequestsWithinSystem(Community community, List<Long> serviceIds) {
        var communities = communityService.findForNonNetworkReferralRequestExceptWithServices(community, serviceIds);

        return communities.stream().map(c -> createRequest(c, null))
                .collect(Collectors.toList());
    }

    @Override
    public ReferralRequest createRequestForNetwork(Community community, Collection<PartnerNetwork> partnerNetworks) {
        partnerNetworks.forEach(partnerNetwork -> validateNetworkCommunity(community, partnerNetwork));

        return createRequest(community, partnerNetworks);
    }

    @Override
    public ReferralRequest createRequest(Community community) {
        return createRequest(community, null);
    }

    @Override
    public Page<ReferralInfoRequest> findOutboundInfoRequests(Long referralId, PermissionFilter permissionFilter, Pageable pageable) {
        var byReferralId = referralSpecificationGenerator.outboundInfoRequestsByReferralId(referralId);
        var hasAccess = referralSpecificationGenerator.hasAccessToOutboundInfoRequests(permissionFilter);
        return referralInfoRequestDao.findAll(byReferralId.and(hasAccess), pageable);
    }

    @Override
    public Page<ReferralInfoRequest> findInboundInfoRequests(Long requestId, PermissionFilter permissionFilter, Pageable pageable) {
        var byRequestId = referralSpecificationGenerator.inboundInfoRequestsByRequestId(requestId);
        var hasAccess = referralSpecificationGenerator.hasAccessToInboundInfoRequests(permissionFilter);
        var visible = referralSpecificationGenerator.isInboundInfoRequestVisibleByStatus();
        return referralInfoRequestDao.findAll(byRequestId.and(hasAccess.and(visible)), pageable);
    }

    @Override
    public ReferralInfoRequest saveInfoRequest(ReferralInfoRequest referralInfoRequest) {
        validateInboundVisible(referralInfoRequest.getReferralRequest());

        referralInfoRequest = referralInfoRequestDao.save(referralInfoRequest);
        referralNotificationService.sendInfoReqNotification(referralInfoRequest);
        return referralInfoRequest;
    }

    @Override
    public ReferralInfoRequest respondToInfoRequest(ReferralInfoRequest referralInfoRequest) {
        validateInboundVisible(referralInfoRequest.getReferralRequest());

        referralInfoRequest = referralInfoRequestDao.save(referralInfoRequest);
        referralNotificationService.sendReplyInfoReqNotification(referralInfoRequest);
        return referralInfoRequest;
    }

    @Override
    public ReferralInfoRequest findInfoRequest(Long infoRequestId) {
        return referralInfoRequestDao.findById(infoRequestId).orElseThrow();
    }

    @Override
    public void changeAssignee(ReferralRequest request, Long employeeId) {
        validateInboundVisible(request);

        var employee = Optional.ofNullable(employeeId)
                .map(id -> {
                    var possibleAssignees = referralSpecificationGenerator.possibleAssignees(request);
                    var byId = employeeSpecificationGenerator.byId(id);

                    return employeeDao.findOne(possibleAssignees.and(byId)).orElseThrow(() ->
                            new ValidationException("Employee [" + id + "] can't be assigned to referral request [" +
                                    request.getId() + "]"));
                })
                .orElse(null);

        var history = new ReferralRequestAssignedHistory();
        history.setId(new ReferralRequestAssignedHistory.Id(request, request.getAssignedEmployee(), Instant.now()));
        referralRequestAssignedHistoryDao.save(history);

        request.setAssignedEmployee(employee);
        var saved = referralRequestDao.save(request);
        if (employee != null) {
            referralNotificationService.sendAssignedToYouNotification(saved);
        }
    }

    @Override
    public List<Employee> findPossibleAssignees(ReferralRequest request, Sort sort) {
        var possibleAssignees = referralSpecificationGenerator.possibleAssignees(request);

        return employeeDao.findAll(possibleAssignees, sort);
    }

    @Override
    public Page<ReferralListItemAware> findListItemOutbounds(
            ReferralFilter filter,
            PermissionFilter permissionFilter,
            Pageable pageable
    ) {
        var hasAccess = referralSpecificationGenerator.hasAccessToOutboundReferrals(permissionFilter);
        var byFilter = referralSpecificationGenerator.byFilter(filter);

        var result = referralDao.findAll(byFilter.and(hasAccess), ReferralListItemAware.class, pageable);

        var ids = result.stream()
                .map(ReferralListItemAware::getId)
                .collect(Collectors.toList());

        var communities =
                referralRequestDao.findAll(
                                (root, query, criteriaBuilder) -> root.join(ReferralRequest_.referral).get(Referral_.id).in(ids),
                                ReferralRequestCommunityAware.class
                        ).stream()
                        .collect(
                                Collectors.groupingBy(
                                        ReferralRequestCommunityAware::getReferralId,
                                        Collectors.mapping(
                                                ReferralRequestCommunityAware::getCommunityName,
                                                Collectors.toList()
                                        )
                                ));

        return result.map(s -> {
            var aware = new ReferralListItemAwareImpl(s);
            aware.setReferralRequestsCommunityNames(communities.get(s.getId()));
            return aware;
        });
    }

    @Override
    public List<CommunityName> findRecipients(ReferralFilter filter, PermissionFilter permissionFilter) {
        var hasAccess = referralSpecificationGenerator.hasAccessToOutboundReferrals(permissionFilter);
        var byFilter = referralSpecificationGenerator.byFilter(filter);
        return referralDao.findCommunityNames(byFilter.and(hasAccess));
    }

    @Override
    public Page<ReferralRequestListItemAware> findListItemInbounds(
            ReferralFilter filter,
            PermissionFilter permissionFilter,
            Pageable pageable
    ) {
        var byFilter = referralSpecificationGenerator.requestByFilter(filter);
        var visibleByStatus = referralSpecificationGenerator.isInboundRequestVisibleByStatus();
        var hasAccess = referralSpecificationGenerator.hasAccessToInboundRequests(permissionFilter);
        return referralRequestDao.findAll(
                byFilter.and(hasAccess.and(visibleByStatus)),
                ReferralRequestListItemAware.class,
                pageable
        );
    }

    @Override
    public Page<ReferralRequestReferralCommunityAware> findCommunitiesInbound(
            ReferralFilter filter, PermissionFilter permissionFilter, Pageable pageable
    ) {
        var byFilter = referralSpecificationGenerator.requestByFilter(filter);
        var visibleByStatus = referralSpecificationGenerator.isInboundRequestVisibleByStatus();
        var hasAccess = referralSpecificationGenerator.hasAccessToInboundRequests(permissionFilter);
        return referralRequestDao.findAll(
                byFilter.and(hasAccess.and(visibleByStatus)),
                ReferralRequestReferralCommunityAware.class,
                pageable
        );
    }

    private void validateNetworkCommunity(Community community, PartnerNetwork partnerNetwork) {
        if (!partnerNetworkService.isCommunityInNetwork(community.getId(), partnerNetwork.getId())) {
            invalid("Community [" + community.getId() + "] is not from network [" + partnerNetwork.getId() + "]");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAvailableAction(Referral referral, ReferralAction action) {
        switch (action) {
            case ACCEPT:
            case DECLINE:
                return ReferralStatus.PENDING.equals(referral.getReferralStatus()) && isMarketplaceFaxShared(referral);
            case CANCEL:
                return ReferralStatus.PENDING.equals(referral.getReferralStatus());
            default:
                return false;
        }
    }

    @Override
    public boolean isAvailableAction(ReferralRequest referralRequest, ReferralAction action) {
        switch (action) {
            case PRE_ADMIT:
                return !referralRequest.getReferral().isMarketplace() && isVisibleInboundByStatus(referralRequest) && requestResponseIs(referralRequest, null);
            case ACCEPT:
                return isVisibleInboundByStatus(referralRequest) &&
                        ((referralRequest.getReferral().isMarketplace() && requestResponseIs(referralRequest, null)) ||
                                requestResponseIs(referralRequest, ReferralResponse.PRE_ADMIT));
            case DECLINE:
            case REQUEST_INFO: //same checks
                return isVisibleInboundByStatus(referralRequest) &&
                        (requestResponseIs(referralRequest, null) || requestResponseIs(referralRequest, ReferralResponse.PRE_ADMIT));
            case ASSIGN:
                return isVisibleInboundByStatus(referralRequest) &&
                        (requestResponseIs(referralRequest, null) ||
                                requestResponseIs(referralRequest, ReferralResponse.PRE_ADMIT) ||
                                requestResponseIs(referralRequest, ReferralResponse.ACCEPTED));
            case CANCEL:
                return false;

        }
        return false;
    }

    private boolean requestResponseIs(ReferralRequest request, ReferralResponse response) {
        if (response == null)
            return request.getLastResponse() == null; //pending

        return Optional.ofNullable(request.getLastResponse())
                .map(ReferralRequestResponse::getResponse)
                .filter(response::equals)
                .isPresent();
    }

    private void validateInboundVisible(ReferralRequest referralRequest) {
        if (!isVisibleInboundByStatus(referralRequest)) {
            throw new BusinessException("Attempt to interact with invisible referral request [" + referralRequest.getId() + "]");
        }
    }

    //todo use this method on each details call
    @Override
    public boolean isVisibleInboundByStatus(ReferralRequest request) {
        var referral = request.getReferral();

        switch (referral.getReferralStatus()) {
            case PENDING:
            case DECLINED:
                return true;
            case PRE_ADMIT:
            case ACCEPTED:
                if (referral.getUpdatedByResponse() == null) {
                    throw new BusinessException("Invalid referral [" + referral.getId() + "] state: state is " +
                            referral.getReferralStatus() + " but 'updatedByResponse' is null");
                }

                return isReferralUpdatedByRequest(referral, request);
            case CANCELED:
                return false;
            default:
                return false;
        }
    }

    private boolean isMarketplaceFaxShared(Referral referral) {
        if (!referral.isMarketplace()) {
            return false;
        }

        return referral.getReferralRequestIds().stream()
                .min(Long::compare)
                .map(this::getRequestById)
                .map(referralRequest -> ReferralRequestSharedChannel.FAX == referralRequest.getSharedChannel())
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public ReferralSecurityAwareEntity findReferralSecurityByInfoRequestId(Long infoRequestId) {
        var referralOfInfoRequest = referralSpecificationGenerator.referralOfInfoRequest(infoRequestId);

        var resultList = referralDao.findAll(referralOfInfoRequest, ReferralSecurityAwareEntity.class);
        return resultList.get(0); // should always be present
    }

    @Override
    @Transactional(readOnly = true)
    public ReferralRequestSecurityAwareEntity findReferralRequestSecurityByInfoRequestId(Long infoRequestId) {
        var referralRequestOfInfoRequest = referralSpecificationGenerator.referralRequestOfInfoRequest(infoRequestId);

        var resultList = referralRequestDao.findAll(referralRequestOfInfoRequest, ReferralRequestSecurityAwareEntity.class);
        return resultList.get(0); // should always be present
    }

    @Override
    @Transactional(readOnly = true)
    public ReferralRequestSecurityAwareEntity findReferralRequestSecurityEntity(Long requestId) {
        return referralRequestDao.findById(requestId, ReferralRequestSecurityAwareEntity.class).orElseThrow();
    }

    private boolean isReferralUpdatedByRequest(Referral referral, ReferralRequest request) {
        if (request.getLastResponse() == null) {
            return false;
        }

        return referral.getUpdatedByResponse().getId().equals(request.getLastResponse().getId());
    }

    @Override
    public boolean existsInboundWithEnabledFacesheet(Long clientId, PermissionFilter permissionFilter) {
        var hasAccess = referralSpecificationGenerator.hasAccessToInboundReferrals(permissionFilter);
        var byClientId = referralSpecificationGenerator.byClientId(clientId);
        var withSharedFacesheet = referralSpecificationGenerator.withSharedFacesheet();

        return referralDao.count(byClientId.and(withSharedFacesheet).and(hasAccess)) > 0;
    }

    @Override
    public boolean existsInboundWithEnabledCcd(Long clientId, PermissionFilter permissionFilter) {
        var hasAccess = referralSpecificationGenerator.hasAccessToInboundReferrals(permissionFilter);
        var byClientId = referralSpecificationGenerator.byClientId(clientId);
        var withSharedCcd = referralSpecificationGenerator.withSharedCcd();

        return referralDao.count(byClientId.and(withSharedCcd).and(hasAccess)) > 0;
    }

    @Override
    public boolean existsInboundWithEnabledServicePlan(Long clientId, PermissionFilter permissionFilter) {
        var hasAccess = referralSpecificationGenerator.hasAccessToInboundReferrals(permissionFilter);
        var byClientId = referralSpecificationGenerator.byClientId(clientId);
        var withSharedServicePlan = referralSpecificationGenerator.withSharedServicePlan();

        return referralDao.count(byClientId.and(withSharedServicePlan).and(hasAccess)) > 0;
    }

    private ReferralRequest createRequest(Community community, Collection<PartnerNetwork> partnerNetwork) {
        var target = new ReferralRequest();
        //referral will be set outside
        if (CollectionUtils.isNotEmpty(partnerNetwork)) {
            target.setPartnerNetworks(new ArrayList<>(partnerNetwork));
        }
        target.setCommunity(community);
        target.setCommunityId(community.getId());
        return target;
    }

    @Override
    public void preadmit(ReferralRequest referralRequest, Employee loggedUser) {
        validateReferralRequestAction(referralRequest, ReferralAction.PRE_ADMIT);

        Instant now = Instant.now();
        ReferralRequestResponse referralRequestResponse = createReferralRequestResponse(referralRequest, loggedUser, now, ReferralResponse.PRE_ADMIT);
        referralRequestResponse.setPreadmitDate(now);
        referralRequestResponse.setReferralRequest(referralRequest);
        referralRequestResponse = referralRequestResponseDao.save(referralRequestResponse);
        referralRequest.setLastResponse(referralRequestResponse);
        referralRequest = referralRequestDao.save(referralRequest);

        updateReferral(referralRequest.getReferral(), ReferralStatus.PRE_ADMIT, referralRequestResponse);
        referralNotificationService.sendPreAdmitNotification(referralRequest);
    }

    @Override
    public void accept(ReferralRequest referralRequest, Employee loggedUser, Long serviceStartDate, Long serviceEndDate, String comment) {
        validateReferralRequestAction(referralRequest, ReferralAction.ACCEPT);

        Instant now = Instant.now();
        ReferralRequestResponse referralRequestResponse = createReferralRequestResponse(referralRequest, loggedUser, now, ReferralResponse.ACCEPTED);
        referralRequestResponse.setServiceStartDate(DateTimeUtils.toInstant(serviceStartDate));
        referralRequestResponse.setServiceEndDate(DateTimeUtils.toInstant(serviceEndDate));
        referralRequestResponse.setComment(comment);
        referralRequestResponse = referralRequestResponseDao.save(referralRequestResponse);
        referralRequest.setLastResponse(referralRequestResponse);
        referralRequest = referralRequestDao.save(referralRequest);

        updateReferral(referralRequest.getReferral(), ReferralStatus.ACCEPTED, referralRequestResponse);
        referralNotificationService.sendAcceptedNotification(referralRequest);
    }

    @Override
    public void decline(ReferralRequest referralRequest, Employee loggedUser, Long referralDeclineReasonId, String comment) {
        validateReferralRequestAction(referralRequest, ReferralAction.DECLINE);

        Instant now = Instant.now();
        ReferralRequestResponse referralRequestResponse = createReferralRequestResponse(referralRequest, loggedUser, now, ReferralResponse.DECLINED);
        referralRequestResponse.setComment(comment);
        referralRequestResponse.setDeclineReason(referralDeclineReasonDao.findById(referralDeclineReasonId).orElseThrow());
        referralRequestResponse = referralRequestResponseDao.save(referralRequestResponse);
        referralRequest.setLastResponse(referralRequestResponse);
        referralRequest = referralRequestDao.save(referralRequest);

        var referral = referralRequest.getReferral();
        ReferralStatus previousStatus = referral.getReferralStatus();
        if (!referralRequestDao.existsByReferralAndResponseNot(referral, ReferralResponse.DECLINED)) {
            updateReferral(referral, ReferralStatus.DECLINED, referralRequestResponse);
        } else if (referral.getReferralStatus() == ReferralStatus.PRE_ADMIT) {
            updateReferral(referral, ReferralStatus.PENDING, referralRequestResponse);
        }
        referralNotificationService.sendDeclinedNotification(referralRequest, previousStatus);
    }

    @Override
    public void cancel(Referral referral, Employee loggedUser) {
        validateReferralAction(referral, ReferralAction.CANCEL);
        referral = updateReferral(referral, loggedUser);
        referralNotificationService.sendCanceledNotification(referral);
    }

    private void validateReferralRequestAction(ReferralRequest referralRequest, ReferralAction action) {
        if (!isAvailableAction(referralRequest, action)) {
            throw new BusinessException(String.format("Action %s on referral request with response %s is not allowed", action.name(),
                    Optional.ofNullable(referralRequest.getLastResponse()).map(r -> r.getResponse().getValue())
                            .orElse("[null]")));
        }
    }

    private void validateReferralAction(Referral referral, ReferralAction action) {
        if (!isAvailableAction(referral, action)) {
            throw new BusinessException(String.format("Action %s on referral with status %s is not allowed", action.name(),
                    referral.getReferralStatus().getDisplayName()));
        }
    }

    private Referral updateReferral(Referral referral, ReferralStatus newStatus, ReferralRequestResponse updatedBy) {
        return updateReferral(referral, newStatus, updatedBy, null);
    }

    private Referral updateReferral(Referral referral, Employee cancelledBy) {
        return updateReferral(referral, ReferralStatus.CANCELED, null, cancelledBy);
    }

    private Referral updateReferral(Referral referral, ReferralStatus newStatus, ReferralRequestResponse updatedBy, Employee cancelledBy) {
        addNewHistoryRecord(referral);
        referral.setUpdatedByResponse(updatedBy);
        referral.setReferralStatus(newStatus);
        referral.setModifiedDate(Instant.now());
        referral.setCancelledBy(cancelledBy);
        return referralDao.save(referral);
    }

    private ReferralRequestResponse createReferralRequestResponse(ReferralRequest referralRequest, Employee loggedUser, Instant now, ReferralResponse referralResponse) {
        ReferralRequestResponse referralRequestResponse = new ReferralRequestResponse();
        referralRequestResponse.setResponseDatetime(now);
        referralRequestResponse.setResponse(referralResponse);
        referralRequestResponse.setEmployee(loggedUser);
        referralRequestResponse.setReferralRequest(referralRequest);
        return referralRequestResponse;
    }

    private void addNewHistoryRecord(Referral referral) {
        referral.getReferralHistories().add(createHistoryItem(referral));
    }

    private Referral setStatusAndUpdate(Referral referral, ReferralStatus referralStatus) {
        referral.setReferralStatus(referralStatus);
        referral.setModifiedDate(Instant.now());
        return referralDao.save(referral);
    }

    private ReferralHistory createHistoryItem(Referral referral) {
        ReferralHistory referralHistory = new ReferralHistory();
        referralHistory.setReferral(referral);
        referralHistory.setReferralStatus(referral.getReferralStatus());
        referralHistory.setUpdatedByResponse(referral.getUpdatedByResponse());
        referralHistory.setModifiedDate(referral.getModifiedDate());
        return referralHistory;
    }

    @Override
    @Transactional(readOnly = true)
    public ReferralSecurityAwareEntity findSecurityAwareEntity(Long id) {
        return referralDao.findById(id, ReferralSecurityAwareEntity.class).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReferralSecurityAwareEntity> findSecurityAwareEntities(Collection<Long> ids) {
        return referralDao.findByIdIn(ids, ReferralSecurityAwareEntity.class);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsAccessibleReferralRequest(Long clientId, PermissionFilter permissionFilter) {
        var hasAccess = referralSpecificationGenerator.hasAccessToInboundReferrals(permissionFilter);
        var byClientId = referralSpecificationGenerator.byClientId(clientId);
        return referralDao.count(byClientId.and(hasAccess)) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findRequestById(Long requestId, Class<P> projection) {
        return referralRequestDao.findById(requestId, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findReferralById(Long referralId, Class<P> projection) {
        return referralDao.findById(referralId, projection).orElseThrow();
    }
}
