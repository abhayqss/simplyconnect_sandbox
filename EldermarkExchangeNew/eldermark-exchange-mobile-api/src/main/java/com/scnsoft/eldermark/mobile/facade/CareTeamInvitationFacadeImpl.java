package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.ClientCareTeamInvitationFilter;
import com.scnsoft.eldermark.entity.careteam.invitation.ClientCareTeamInvitationStatus;
import com.scnsoft.eldermark.entity.careteam.invitation.ConfirmInviteCareTeamMemberData;
import com.scnsoft.eldermark.entity.careteam.invitation.InviteCareTeamMemberData;
import com.scnsoft.eldermark.exception.CareTeamInvitationClientHieConsentValidationException;
import com.scnsoft.eldermark.mobile.dto.careteam.invitation.ClientCareTeamInboundInvitationListItemDto;
import com.scnsoft.eldermark.mobile.dto.careteam.invitation.ClientCareTeamInvitationDto;
import com.scnsoft.eldermark.mobile.dto.careteam.invitation.ClientCareTeamInvitationListItemDto;
import com.scnsoft.eldermark.mobile.dto.careteam.invitation.ClientCareTeamInvitationValidationDto;
import com.scnsoft.eldermark.mobile.projection.careteam.invitation.ClientCareTeamInboundInvitationListItem;
import com.scnsoft.eldermark.mobile.projection.careteam.invitation.ClientCareTeamInvitationDetails;
import com.scnsoft.eldermark.mobile.projection.careteam.invitation.ClientCareTeamInvitationListItem;
import com.scnsoft.eldermark.service.HieConsentPolicyUpdateService;
import com.scnsoft.eldermark.service.careteam.invitation.ClientCareTeamInvitationService;
import com.scnsoft.eldermark.service.security.ClientCareTeamInvitationSecurityService;
import com.scnsoft.eldermark.service.security.ClientHieConsentPolicySecurityService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.web.commons.dto.careteam.CareTeamInvitationConfirmDto;
import com.scnsoft.eldermark.web.commons.dto.careteam.CareTeamInvitationDto;
import com.scnsoft.eldermark.web.commons.dto.careteam.CareTeamInvitationResendDto;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.EnumSet;
import java.util.Objects;


@Service
public class CareTeamInvitationFacadeImpl implements CareTeamInvitationFacade {

    @Autowired
    private Converter<CareTeamInvitationDto, InviteCareTeamMemberData> careTeamInvitationConverter;

    @Autowired
    private Converter<CareTeamInvitationResendDto, InviteCareTeamMemberData> careTeamInvitationResendConverter;

    @Autowired
    private ClientCareTeamInvitationService clientCareTeamInvitationService;

    @Autowired
    private ClientCareTeamInvitationSecurityService clientCareTeamInvitationSecurityService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private Converter<ClientCareTeamInvitationListItem, ClientCareTeamInvitationListItemDto> clientCareTeamInvitationListItemDtoConverter;

    @Autowired
    private Converter<ClientCareTeamInboundInvitationListItem, ClientCareTeamInboundInvitationListItemDto> clientCareTeamInboundInvitationListItemDtoConverter;

    @Autowired
    private Converter<ClientCareTeamInvitationDetails, ClientCareTeamInvitationDto> clientCareTeamInvitationDtoConverter;

    @Autowired
    private Converter<CareTeamInvitationConfirmDto, ConfirmInviteCareTeamMemberData> confirmInviteCareTeamMemberDataConverter;

    @Autowired
    private ClientHieConsentPolicySecurityService clientHieConsentPolicySecurityService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamInvitationSecurityService.canViewList()")
    public Page<ClientCareTeamInvitationListItemDto> findInvitations(ClientCareTeamInvitationFilter filter,
                                                                     Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        return clientCareTeamInvitationService.find(filter,
                permissionFilter,
                ClientCareTeamInvitationListItem.class,
                PaginationUtils.applyEntitySort(pageable, ClientCareTeamInvitationListItemDto.class)
        ).map(clientCareTeamInvitationListItemDtoConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamInvitationSecurityService.canViewList()")
    public Page<ClientCareTeamInboundInvitationListItemDto> findInboundInvitations(Pageable pageable) {

        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        var filter = new ClientCareTeamInvitationFilter();

        filter.setStatuses(EnumSet.of(ClientCareTeamInvitationStatus.PENDING));
        filter.setTargetEmployeeId(loggedUserService.getCurrentEmployeeId());

        return clientCareTeamInvitationService.find(
            filter,
            permissionFilter,
            ClientCareTeamInboundInvitationListItem.class,
            PaginationUtils.applyEntitySort(pageable, ClientCareTeamInboundInvitationListItemDto.class)
        ).map(clientCareTeamInboundInvitationListItemDtoConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamInvitationSecurityService.canViewList()")
    public Long countInvitations(ClientCareTeamInvitationFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        return clientCareTeamInvitationService.count(filter, permissionFilter);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamInvitationSecurityService.canView(#id)")
    public ClientCareTeamInvitationDto findById(@P("id") Long id) {
        var invitation = clientCareTeamInvitationService.findById(id, ClientCareTeamInvitationDetails.class);
        return clientCareTeamInvitationDtoConverter.convert(invitation);
    }

    @Override
    @Transactional
    @PreAuthorize("@careTeamInvitationSecurityService.canInvite(#invitationDto.clientId)")
    public Long invite(CareTeamInvitationDto invitationDto) {
        var invitation = careTeamInvitationConverter.convert(invitationDto);
        return clientCareTeamInvitationService.invite(invitation).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canInvite(Long clientId) {
        return clientCareTeamInvitationSecurityService.canInvite(clientId);
    }

    @Override
    @Transactional
    public void confirmRegistration(CareTeamInvitationConfirmDto inviteDto) {
        clientCareTeamInvitationService.confirmInvitation(confirmInviteCareTeamMemberDataConverter.convert(inviteDto));
    }

    @Override
    @Transactional
    @PreAuthorize("@careTeamInvitationSecurityService.canResend(#dto.id)")
    public Long resendInvitation(CareTeamInvitationResendDto dto) {
        var data = Objects.requireNonNull(careTeamInvitationResendConverter.convert(dto));
        return clientCareTeamInvitationService.resendInvitation(dto.getId(), data).getId();
    }

    @Override
    @Transactional
    @PreAuthorize("@careTeamInvitationSecurityService.canCancel(#id)")
    public void cancelInvitation(Long id) {
        clientCareTeamInvitationService.cancelInvitation(id);
    }

    @Override
    @Transactional
    @PreAuthorize("@careTeamInvitationSecurityService.canAcceptOrDecline(#id)")
    public void acceptInvitation(@P("id") Long id) {
        clientCareTeamInvitationService.accept(id);
    }

    @Override
    @Transactional
    @PreAuthorize("@careTeamInvitationSecurityService.canAcceptOrDecline(#id)")
    public void declineInvitation(@P("id") Long id) {
        clientCareTeamInvitationService.decline(id);
    }

    @Override
    @Transactional
    @PreAuthorize("@clientHieConsentPolicySecurityService.canEdit(#clientId)")
    public boolean existsIncomingForHieConsentChange(@P("clientId") Long clientId) {
        return clientCareTeamInvitationService.existsIncomingForHieConsentChange(clientId);
    }

    @Override
    @Transactional
    @PreAuthorize("@careTeamInvitationSecurityService.canInvite(#clientId)")
    public ClientCareTeamInvitationValidationDto validateHieConsent(@P("clientId") Long clientId) {
        var dto = new ClientCareTeamInvitationValidationDto();

        try {
            clientCareTeamInvitationService.validateHieConsent(clientId);
            dto.setValid(true);
        } catch (CareTeamInvitationClientHieConsentValidationException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            dto.setValid(false);
            dto.setErrorMessage(ex.getMessage());
            if (!ex.isFailedThroughMatchingRecord()) {
                dto.setCanEditHieConsent(clientHieConsentPolicySecurityService.canEdit(clientId));
            }
        }

        return dto;
    }
}
