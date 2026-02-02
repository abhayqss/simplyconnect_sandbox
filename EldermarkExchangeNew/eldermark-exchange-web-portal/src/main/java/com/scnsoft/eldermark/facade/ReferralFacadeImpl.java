package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ReferralFilter;
import com.scnsoft.eldermark.converter.base.ItemConverter;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dao.ReferralRequestDao;
import com.scnsoft.eldermark.dao.referral.ReferralListItemAware;
import com.scnsoft.eldermark.dao.referral.ReferralRequestListItemAware;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.document.ClientDocumentReferralAware;
import com.scnsoft.eldermark.entity.referral.*;
import com.scnsoft.eldermark.service.document.ClientDocumentService;
import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;
import com.scnsoft.eldermark.dto.referral.*;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.service.*;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.service.security.ReferralSecurityService;
import com.scnsoft.eldermark.util.DocumentUtils;
import com.scnsoft.eldermark.util.StreamUtils;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class ReferralFacadeImpl implements ReferralFacade {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("TIFF", "TIF", "PDF", "JPEG", "GIF", "PNG", "JPG", "DOCX", "DOC");
    private static final Sort REFERRAL_REQUESTING_COMMUNITY_NAME_SORT =
            Sort.by(String.join(".", ReferralRequest_.REFERRAL, Referral_.REQUESTING_COMMUNITY, Community_.NAME));

    @Autowired
    private ReferralService referralService;

    @Autowired
    private ReferralRequestDao referralRequestDao;

    @Autowired
    private ClientService clientService;

    @Autowired
    private Converter<ReferralDto, Referral> referralEntityConverter;

    @Autowired
    private Converter<ReferralRequest, ReferralDto> inboundReferralDtoConverter;

    @Autowired
    private Converter<Referral, ReferralDto> outboundReferralDtoConverter;

    @Autowired
    private Converter<ReferralRequestListItemAware, ReferralListItemDto> inboundReferralListItemDtoConverter;

    @Autowired
    private Converter<ReferralListItemAware, ReferralListItemDto> outboundReferralListItemDtoConverter;

    @Autowired
    private Converter<Address, AddressDto> addressDtoConverter;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private Converter<ReferralInfoRequest, ReferralCommunicationListItemDto> refCommunicationListConverter;

    @Autowired
    private Converter<ReferralCommunicationDto, ReferralInfoRequest> referralInfoEntityConverter;

    @Autowired
    private Converter<ReferralInfoRequest, ReferralCommunicationDto> referralCommunicationDtoConverter;

    @Autowired
    private ItemConverter<ReferralRequest, ReferralSharedWithListItemDto> referralSharedWithListItemDtoConverter;

    @Autowired
    private Converter<ReferralRequest, ReferralSharedWithDetailsDto> referralSharedWithDetailsDtoConverter;

    @Autowired
    private ReferralSecurityService referralSecurityService;

    @Autowired
    private ServicePlanService servicePlanService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private ListAndItemConverter<MultipartFile, ReferralAttachment> referralAttachmentEntityConverter;

    @Autowired
    private ReferralAttachmentService referralAttachmentService;

    @Autowired
    private ClientDocumentService clientDocumentService;

    @Autowired
    private ListAndItemConverter<ClientDocumentReferralAware, ReferralAttachment> referralAttachmentClientDocumentConverter;

    @Override
    @PreAuthorize("@referralSecurityService.canAdd(#referralDto)")
    public Long add(@P("referralDto") ReferralDto referralDto) {
        referralDto.setAttachmentFiles(validateAndReduceAttachments(referralDto.getAttachmentFiles()));
        var referral = referralEntityConverter.convert(referralDto);
        var savedReferral = referralService.submit(referral);
        var attachments = referralAttachmentEntityConverter.convertList(referralDto.getAttachmentFiles());
        var clientDocuments = clientDocumentService.findAllById(referralDto.getAttachedClientDocumentIds(), ClientDocumentReferralAware.class);
        if (CollectionUtils.isNotEmpty(clientDocuments)) {
            var clientDocumentAttachments = referralAttachmentClientDocumentConverter.convertList(clientDocuments);
            attachments.addAll(clientDocumentAttachments);
        }
        attachments.forEach(attachment -> {
            attachment.setReferral(savedReferral);
            referralAttachmentService.saveWithContent(attachment);
        });
        return savedReferral.getId();
    }

    private List<MultipartFile> validateAndReduceAttachments(List<MultipartFile> files) {
        return Stream.ofNullable(files)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .peek(file -> DocumentUtils.validateUploadedFile(file, ALLOWED_EXTENSIONS))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@clientSecurityService.canView(#clientId)")
    public ReferralDto findDefault(@P("clientId") Long clientId) {
        var result = new ReferralDto();
        var clientDto = new ReferralClientDto();
        result.setClient(clientDto);

        var client = clientService.getById(clientId);
        clientDto.setId(clientId);
        clientDto.setLocation(client.getCommunity().getName());
        clientDto.setLocationPhone(client.getCommunity().getPhone());
        clientDto.setFullName(client.getFullName());

        clientDto.setAddress(new AddressDto());
        if (CollectionUtils.isNotEmpty(client.getPerson().getAddresses())) {
            clientDto.setAddress(
                    addressDtoConverter.convert(client.getPerson().getAddresses().get(0))
            );
        }

        if (client.getInNetworkInsurance() != null) {
            clientDto.setInsuranceNetworkTitle(client.getInNetworkInsurance().getDisplayName());
        }

        result.setPerson(loggedUserService.getCurrentEmployee().getFullName());
        result.setCommunityTitle(client.getCommunity().getName());

        result.setDate(Instant.now().toEpochMilli());
        result.setIsFacesheetShared(true);
        result.setHasSharedServicePlan(servicePlanService.existsUnarchivedSharedWithClientForClient(clientId));
        return result;
    }

    @Override
    @PreAuthorize("@referralSecurityService.canViewInboundInfoRequestList()")
    public Page<ReferralCommunicationListItemDto> findInboundInfoRequests(Long requestId, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return referralService.findInboundInfoRequests(
                requestId,
                permissionFilter,
                PaginationUtils.applyEntitySort(pageable, ReferralCommunicationListItemDto.class)
        )
                .map(refCommunicationListConverter::convert);

    }

    @Override
    @PreAuthorize("@referralSecurityService.canViewOutboundInfoRequestList()")
    public Page<ReferralCommunicationListItemDto> findOutboundInfoRequests(@P("referralId") Long referralId, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return referralService.findOutboundInfoRequests(referralId, permissionFilter, PaginationUtils.applyEntitySort(pageable, ReferralCommunicationListItemDto.class))
                .map(refCommunicationListConverter::convert);
    }

    @Override
    @PreAuthorize("@referralSecurityService.canRequestInfo(#dto.referralRequestId)")
    public Long sendInfoRequest(@P("dto") ReferralCommunicationDto dto) {
        validateRequestVisibility(dto.getReferralRequestId());
        var infoRequest = referralInfoEntityConverter.convert(dto);
        return referralService.saveInfoRequest(infoRequest).getId();
    }

    @Override
    @PreAuthorize("@referralSecurityService.canRespondToInfoRequest(#dto.id)")
    public void respondToInfoRequest(@P("dto") ReferralCommunicationDto dto) {
        var infoRequest = referralInfoEntityConverter.convert(dto);
        referralService.respondToInfoRequest(infoRequest);
    }

    @Override
    @PreAuthorize("@referralSecurityService.canViewInboundInfoRequest(#infoRequestId)")
    public ReferralCommunicationDto getInboundInfoRequest(@P("infoRequestId") Long infoRequestId) {
        var infoRequest = referralService.findInfoRequest(infoRequestId);
        validateRequestVisibility(infoRequest.getReferralRequest()); //todo move validation to service
        return referralCommunicationDtoConverter.convert(infoRequest);
    }

    @Override
    @PreAuthorize("@referralSecurityService.canViewOutboundInfoRequest(#infoRequestId)")
    public ReferralCommunicationDto getOutboundInfoRequest(@P("infoRequestId") Long infoRequestId) {
        var infoRequest = referralService.findInfoRequest(infoRequestId);
        return referralCommunicationDtoConverter.convert(infoRequest);
    }

    @Override
    @PreAuthorize("@referralSecurityService.canReassign(#requestId)")
    public List<IdentifiedTitledEntityDto> findPossibleAssignees(@P("requestId") Long requestId) {
        return referralService.findPossibleAssignees(referralRequestDao.getOne(requestId),
                Sort.by(Employee_.FIRST_NAME, Employee_.LAST_NAME)).stream()
                .map(employee -> new IdentifiedTitledEntityDto(employee.getId(), employee.getFullName()))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@referralSecurityService.canReassign(#requestId)")
    public void assign(@P("requestId") Long requestId, Long employeeId) {
        referralService.changeAssignee(referralRequestDao.getOne(requestId), employeeId);
    }

    @Override
    @PreAuthorize("@referralSecurityService.canViewOutboundList()")
    public Page<ReferralListItemDto> findOutbounds(ReferralFilter filter, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        pageable = PaginationUtils.applyEntitySort(pageable, ReferralListItemDto.class, Referral.class);
        return referralService
                .findListItemOutbounds(filter, permissionFilter, pageable)
                .map(outboundReferralListItemDtoConverter::convert);
    }

    @Override
    @PreAuthorize("@referralSecurityService.canViewInboundList()")
    public Page<ReferralListItemDto> findInbounds(ReferralFilter filter, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        pageable = PaginationUtils.applyEntitySort(pageable, ReferralListItemDto.class, ReferralRequest.class);
        return referralService
                .findListItemInbounds(filter, permissionFilter, pageable)
                .map(inboundReferralListItemDtoConverter::convert);
    }

    @Override
    @PreAuthorize("@referralSecurityService.canViewOutbound(#referralId)")
    public ReferralDto findOutboundById(@P("referralId") Long referralId) {
        return outboundReferralDtoConverter.convert(referralService.findById(referralId));
    }

    @Override
    @PreAuthorize("@referralSecurityService.canViewInbound(#requestId)")
    public ReferralDto findInboundById(@P("requestId") Long requestId) {
        var request = referralService.findRequestById(requestId);
        validateRequestVisibility(request);
        return inboundReferralDtoConverter.convert(request);
    }

    @Override
    @PreAuthorize("@referralSecurityService.canViewOutbound(#referralId)")
    public Page<ReferralSharedWithListItemDto> findOutboundReferralRequests(@P("referralId") Long referralId, Pageable pageable) {
        return referralService
                .findOutboundRequests(referralId, PaginationUtils.applyEntitySort(pageable, ReferralSharedWithListItemDto.class))
                .map(referralSharedWithListItemDtoConverter::convert);
    }

    @Override
    @PreAuthorize("@referralSecurityService.canViewOutbound(@referralServiceImpl.findRequestById(#requestId).referral.id)")
    public ReferralSharedWithDetailsDto findOutboundRequestById(@P("requestId") Long requestId) {
        return referralSharedWithDetailsDtoConverter.convert(referralService.findRequestById(requestId));
    }

    @Override
    public boolean canAddToCommunity(Long communityId) {
        return referralSecurityService.canAddToCommunity(communityId);
    }

    @Override
    @PreAuthorize("@referralSecurityService.canViewOutboundList()")
    public List<IdentifiedTitledEntityDto> findRecipients(ReferralFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        return referralService.findRecipients(filter, permissionFilter)
                .stream()
                .map(communityName -> new IdentifiedTitledEntityDto(communityName.getId(), communityName.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@referralSecurityService.canViewInboundList()")
    public List<IdentifiedTitledEntityDto> findSenders(ReferralFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var pageable = PaginationUtils.setSort(Pageable.unpaged(), REFERRAL_REQUESTING_COMMUNITY_NAME_SORT);

        return referralService.findCommunitiesInbound(filter, permissionFilter, pageable)
                .stream()
                .map(referralRequest -> new IdentifiedTitledEntityDto(
                        referralRequest.getReferralRequestingCommunityId(),
                        referralRequest.getReferralRequestingCommunityName()
                ))
                .filter(StreamUtils.distinctByKey(IdentifiedTitledEntityDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@referralSecurityService.canPreadmit(#requestId)")
    public void preadmit(@P("requestId") Long requestId) {
        var loggedUser = loggedUserService.getCurrentEmployee();
        var request = referralService.findRequestById(requestId);
        referralService.preadmit(request, loggedUser);
    }

    @Override
    @PreAuthorize("@referralSecurityService.canAccept(#requestId)")
    public void accept(@P("requestId") Long requestId, ReferralAcceptDto dto) {
        var loggedUser = loggedUserService.getCurrentEmployee();
        var request = referralService.findRequestById(requestId);
        referralService.accept(request, loggedUser, dto.getServiceStartDate(), dto.getServiceEndDate(), dto.getComment());
    }

    @Override
    @PreAuthorize("@referralSecurityService.canDecline(#requestId)")
    public void decline(@P("requestId") Long requestId, ReferralDeclineDto referralDeclineDto) {
        var loggedUser = loggedUserService.getCurrentEmployee();
        var request = referralService.findRequestById(requestId);
        referralService.decline(request, loggedUser, referralDeclineDto.getReferralDeclineReasonId(), referralDeclineDto.getComment());
    }

    @Override
    @PreAuthorize("@referralSecurityService.canCancel(#referralId)")
    public void cancel(@P("referralId") Long referralId) {
        var loggedUser = loggedUserService.getCurrentEmployee();
        var referral = referralService.findById(referralId);
        referralService.cancel(referral, loggedUser);
    }

    private void validateRequestVisibility(Long requestId) {
        validateRequestVisibility(referralService.getRequestById(requestId));
    }

    private void validateRequestVisibility(ReferralRequest request) {
        if (!referralService.isVisibleInboundByStatus(request)) {
            throw new BusinessException(BusinessExceptionType.NOT_FOUND);
        }
    }

    @Override
    @PreAuthorize("@referralSecurityService.hasAddPermissions()")
    public List<IdentifiedTitledEntityDto> getReferralOrganizations(Long targetCommunityId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var targetCommunity = communityService.get(targetCommunityId);
        return organizationService.findAllowedReferralMarketplaceOrganizations(permissionFilter, targetCommunity)
                .stream()
                .map(org -> new IdentifiedTitledEntityDto(org.getId(), org.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@referralSecurityService.hasAddPermissions()")
    public List<IdentifiedTitledEntityDto> getReferralCommunities(Long targetCommunityId, Long organizationId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return communityService.findAllowedReferralMarketplaceCommunities(permissionFilter, targetCommunityId, organizationId)
                .stream()
                .map(com -> new IdentifiedTitledEntityDto(com.getId(), com.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@referralSecurityService.canDownloadOutboundAttachment(#attachmentId)")
    public FileBytesDto downloadOutboundReferralAttachmentById(@P("attachmentId") Long attachmentId) {
        return downloadReferralAttachmentById(attachmentId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@referralSecurityService.canDownloadInboundAttachment(#requestId, #attachmentId)")
    public FileBytesDto downloadInboundReferralAttachmentById(@P("requestId") Long requestId, @P("attachmentId") Long attachmentId) {
        return downloadReferralAttachmentById(attachmentId);
    }

    private FileBytesDto downloadReferralAttachmentById(Long attachmentId) {
        var attachment = referralAttachmentService.findByIdWithContent(attachmentId);
        return new FileBytesDto(attachment.getContent(), attachment.getMimeType() != null ? MediaType.valueOf(attachment.getMimeType()) : null);
    }
}
