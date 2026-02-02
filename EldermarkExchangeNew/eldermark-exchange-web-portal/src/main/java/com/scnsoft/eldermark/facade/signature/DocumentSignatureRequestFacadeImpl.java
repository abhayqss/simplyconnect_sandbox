package com.scnsoft.eldermark.facade.signature;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.security.projection.dto.DocumentSignatureRequestSecurityFieldsAware;
import com.scnsoft.eldermark.dto.RoleDto;
import com.scnsoft.eldermark.dto.signature.*;
import com.scnsoft.eldermark.dto.singature.SubmitTemplateSignatureRequest;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureHistory;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureHistoryService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestSecurityService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DocumentSignatureRequestFacadeImpl implements DocumentSignatureRequestFacade {

    @Autowired
    private DocumentSignatureRequestService signatureRequestService;

    @Autowired
    private DocumentSignatureHistoryService signatureHistoryService;

    @Autowired
    private Converter<CareTeamRole, RoleDto> careTeamRoleEntityConverter;

    @Autowired
    private Converter<DocumentSignatureRequest, DocumentSignatureRequestInfoDto> signatureRequestConverter;

    @Autowired
    private Converter<SubmitTemplateSignatureRequestsDto, List<SubmitTemplateSignatureRequest>> submitSignatureRequestsDtoConverter;

    @Autowired
    private Converter<DocumentSignatureHistory, DocumentSignatureHistoryDto> signatureHistoryConverter;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private DocumentSignatureRequestSecurityService documentSignatureRequestSecurityService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    @PreAuthorize("@documentSignatureRequestSecurityService.canSign(#requestId)")
    @Transactional(readOnly = true)
    public DocumentSignatureRequestInfoDto findInfoById(Long requestId) {
        var request = signatureRequestService.findById(requestId);
        return signatureRequestConverter.convert(request);
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canView(#documentId)")
    @Transactional(readOnly = true)
    public Page<DocumentSignatureHistoryDto> findHistoryByDocumentId(Long documentId, Pageable pageable, Integer timezoneOffset) {

        return signatureHistoryService.findByDocumentId(
                documentId,
                PaginationUtils.applyEntitySort(pageable, DocumentSignatureHistoryDto.class)
        ).map(entity -> {
            var dto = signatureHistoryConverter.convert(entity);
            dto.setComments(signatureHistoryService.generateCommentsForHistory(entity, timezoneOffset));
            return dto;
        });
    }

    @Override
    @PreAuthorize("@documentSignatureRequestSecurityService.canAddAll(#dto.toSecurityFieldsAwareList())")
    @Transactional
    public List<Long> submitRequests(SubmitTemplateSignatureRequestsDto dto) {
        clientService.validateActive(dto.getClientId());
        var currentEmployee = loggedUserService.getCurrentEmployee();
        var requests = Objects.requireNonNull(submitSignatureRequestsDtoConverter.convert(dto)).stream()
                .map(request -> {
                    request.setRequestedBy(currentEmployee);
                    return request;
                })
                .collect(Collectors.toList());
        return signatureRequestService.submitRequests(requests)
                .stream()
                .map(DocumentSignatureRequest::getId)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@documentSignatureRequestSecurityService.canRenew(#dto.requestId)")
    @Transactional
    public Long renewRequest(DocumentSignatureRequestRenewDto dto) {
        return signatureRequestService.renewRequest(
                dto.getRequestId(),
                DateTimeUtils.toInstant(dto.getExpirationDate()),
                loggedUserService.getCurrentEmployee()
        ).getId();
    }

    @Override
    @PreAuthorize("@documentSignatureRequestSecurityService.canCancel(#requestId)")
    @Transactional
    public void cancelRequest(Long requestId) {
        signatureRequestService.cancelRequest(requestId, loggedUserService.getCurrentEmployee());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getAllowedRecipientRoles() {
        return signatureRequestService.getAllowedRecipientRoles().stream()
                .map(careTeamRoleEntityConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize(
            "@documentSignatureRequestSecurityService.canAdd(T(com.scnsoft.eldermark.beans.security.projection.dto.DocumentSignatureRequestSecurityFieldsAware)" +
                    ".of(#clientId, T(com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestSecurityService).ANY_TEMPLATE, #documentId))"
    )
    @Transactional(readOnly = true)
    public List<IdentifiedNamedEntityDto> getAllowedRequestRecipientContacts(Long clientId, Long documentId) {
        return signatureRequestService.getAllowedRecipientEmployees(clientId, documentId).stream()
                .map(it -> new IdentifiedNamedEntityDto(it.getId(), it.getFullName()))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@documentSignatureRequestSecurityService.canResendPin(#requestId)")
    @Transactional
    public DocumentSignatureResendPinResponseDto resendPin(Long requestId) {
        validateClientActive(requestId);
        var notification = signatureRequestService.resendPin(requestId);
        return new DocumentSignatureResendPinResponseDto(notification.getPhoneNumber());
    }

    @Override
    @PreAuthorize("#filterDto.organizationId != null " +
            "? @organizationSecurityService.canEditFeatures(#filterDto.organizationId)" +
            ": @communitySecurityService.canEditSignatureConfig(#filterDto.communityId)")
    @Transactional(readOnly = true)
    public Long count(DocumentSignatureRequestFilterDto filterDto) {

        var statuses = filterDto.getStatuses().stream()
                .flatMap(it -> it.requestStatuses().stream())
                .collect(Collectors.toList());

        if (filterDto.getOrganizationId() != null) {
            return signatureRequestService.countByOrganizationIdAndStatuses(filterDto.getOrganizationId(), statuses);
        } else {
            return signatureRequestService.countByCommunityIdAndStatuses(filterDto.getCommunityId(), statuses);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAdd(Long clientId) {
        return documentSignatureRequestSecurityService.canAdd(DocumentSignatureRequestSecurityFieldsAware.of(
                clientId,
                DocumentSignatureRequestSecurityService.ANY_TEMPLATE
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddForOrganization(Long organizationId) {
        return documentSignatureRequestSecurityService.canAddForOrganization(organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdentifiedTitledEntityDto> findOrganizationsAvailableForSignatureRequest() {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return organizationService.findAvailableForSignatureRequest(permissionFilter, IdNameAware.class).stream()
                .map(it -> new IdentifiedTitledEntityDto(it.getId(), it.getName()))
                .sorted(Comparator.comparing(IdentifiedTitledEntityDto::getTitle))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdentifiedTitledEntityDto> findCommunitiesAvailableForSignatureRequest(Long organizationId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return communityService.findAvailableForSignatureRequest(organizationId, permissionFilter, IdNameAware.class).stream()
                .map(it -> new IdentifiedTitledEntityDto(it.getId(), it.getName()))
                .sorted(Comparator.comparing(IdentifiedTitledEntityDto::getTitle))
                .collect(Collectors.toList());
    }

    private void validateClientActive(Long requestId) {
        var clientIdAware = signatureRequestService.findById(requestId, ClientIdAware.class);
        clientService.validateActive(clientIdAware.getClientId());
    }
}
