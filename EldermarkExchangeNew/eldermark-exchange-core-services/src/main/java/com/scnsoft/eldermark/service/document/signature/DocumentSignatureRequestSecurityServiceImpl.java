package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.ClientActiveSecurityAware;
import com.scnsoft.eldermark.beans.projection.DocumentSignatureRequestFromAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIsSignatureEnabledAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.dto.DocumentSignatureRequestSecurityFieldsAware;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.entity.signature.SignatureRequestNotificationMethod;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.service.document.ClientDocumentSecurityService;
import com.scnsoft.eldermark.service.security.BaseSecurityService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("documentSignatureRequestSecurityService")
public class DocumentSignatureRequestSecurityServiceImpl extends BaseSecurityService implements DocumentSignatureRequestSecurityService {

    private static final Set<Permission> ADD_SIGNATURE_REQUEST_PERMISSION_LIST = EnumSet.of(
            DOCUMENT_SIGNATURE_REQUEST_ADD_ALL_EXCEPT_OPTED_OUT,
            DOCUMENT_SIGNATURE_REQUEST_ADD_IF_ASSOCIATED_ORGANIZATION,
            DOCUMENT_SIGNATURE_REQUEST_ADD_IF_ASSOCIATED_COMMUNITY,
            DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_RP_COMMUNITY_CTM,
            DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM,
            DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_RP_CLIENT_CTM,
            DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_REGULAR_CLIENT_CTM);

    @Autowired
    private DocumentSignatureTemplateService signatureTemplateService;

    @Autowired
    private DocumentSignatureRequestService signatureRequestService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientDocumentSecurityService clientDocumentSecurityService;

    @Autowired
    private OrganizationService organizationService;

    @Override
    @Transactional(readOnly = true)
    public boolean canAdd(DocumentSignatureRequestSecurityFieldsAware dto) {
        if (dto.getClientId() == null) {
            return canAddForMultipleClients();
        } else {
            return canAddForClient(dto);
        }
    }

    private boolean canAddForMultipleClients() {
        return hasAnyPermission(ADD_SIGNATURE_REQUEST_PERMISSION_LIST);
    }

    private boolean canAddForClient(DocumentSignatureRequestSecurityFieldsAware dto) {
        if (dto.getDocumentId() != null && !clientDocumentSecurityService.canView(dto.getDocumentId())) {
            return false;
        }

        var client = clientService.findById(dto.getClientId(), ClientActiveSecurityAware.class);

        if (!client.getActive() || !isEligibleForDiscoveryCommunity(client.getCommunityId())) {
            return false;
        }

        var filter = currentUserFilter();

        if (dto.getDocumentId() == null && !isTemplateAvailableForCommunity(dto.getTemplateId(), client.getCommunityId(), filter)) {
            return false;
        }

        if (filter.hasPermission(DOCUMENT_SIGNATURE_REQUEST_ADD_ALL_EXCEPT_OPTED_OUT) && isClientOptedIn(client)) {
            return true;
        }

        if (filter.hasPermission(DOCUMENT_SIGNATURE_REQUEST_ADD_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(DOCUMENT_SIGNATURE_REQUEST_ADD_IF_ASSOCIATED_ORGANIZATION);

            if (isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_SIGNATURE_REQUEST_ADD_IF_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(DOCUMENT_SIGNATURE_REQUEST_ADD_IF_ASSOCIATED_COMMUNITY);

            if (isAnyCreatedUnderCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = filter.getEmployees(DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_RP_COMMUNITY_CTM);

            if (isAnyInCommunityCareTeam(
                    employees,
                    client.getCommunityId(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM)) {
            var employees = filter.getEmployees(DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM);

            if (isAnyInCommunityCareTeam(
                    employees,
                    client.getCommunityId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = filter.getEmployees(DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_RP_CLIENT_CTM);

            if (isAnyInClientCareTeam(
                    employees,
                    client,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (filter.hasPermission(DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = filter.getEmployees(DOCUMENT_SIGNATURE_REQUEST_ADD_IF_CURRENT_REGULAR_CLIENT_CTM);

            if (isAnyInClientCareTeam(
                    employees,
                    client,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddForOrganization(Long organizationId) {
        var organization =
                organizationService.findById(organizationId, OrganizationIsSignatureEnabledAware.class);
        if (!organization.getIsSignatureEnabled()) {
            return false;
        }

        if (!hasAnyPermission(ADD_SIGNATURE_REQUEST_PERMISSION_LIST)) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddAll(List<DocumentSignatureRequestSecurityFieldsAware> dtos) {
        return dtos.stream().allMatch(this::canAdd);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canSign(Long requestId) {
        return canSign(signatureRequestService.findById(requestId, DocumentSignatureRequestFromAware.class));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canSign(DocumentSignatureRequestFromAware request) {

        if (!isSignatureEnabledForClient(request.getClientId())) {
            return false;
        }

        var filter = currentUserFilter();

        if (request.getNotificationMethod() == SignatureRequestNotificationMethod.SIGN_NOW) {
            return filter.getAllEmployeeIds().contains(request.getRequestedById());
        } else {
            if (request.getRequestedFromEmployeeId() != null) {
                return filter.getAllEmployeeIds().contains(request.getRequestedFromEmployeeId());
            } else {
                var requestedFromEmployeeIds = clientService.findById(request.getRequestedFromClientId()).getAssociatedEmployeeIds();
                return CollectionUtils.containsAny(filter.getAllEmployeeIds(), requestedFromEmployeeIds);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canCancel(Long requestId) {
        return canModify(requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canResendPin(Long requestId) {
        return canModify(requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canRenew(Long requestId) {
        return canModify(requestId);
    }

    private boolean canModify(Long requestId) {
        var request = signatureRequestService.findById(requestId, DocumentSignatureRequestDetailsAware.class);
        var clientId = clientService.findByOrganizationAlternativeIdAndLegacyId(
                        request.getDocumentClientOrganizationAlternativeId(),
                        request.getDocumentClientLegacyId()
                )
                .map(IdAware::getId)
                .orElseThrow();

        return canAdd(DocumentSignatureRequestSecurityFieldsAware.of(clientId, request.getSignatureTemplateId(), request.getDocumentId()));
    }

    private boolean isTemplateAvailableForCommunity(Long templateId, Long communityId, PermissionFilter permissionFilter) {
        return Objects.equals(templateId, ANY_TEMPLATE)
                ? signatureTemplateService.existsByCommunityId(communityId, permissionFilter)
                : signatureTemplateService.getProjectedTemplatesByCommunityId(communityId, IdAware.class, null, permissionFilter).stream()
                .map(IdAware::getId)
                .anyMatch(it -> Objects.equals(it, templateId));
    }

    private boolean isSignatureEnabledForClient(Long clientId) {
        var client = clientService.findById(clientId, ClientIsSignatureEnableAware.class);
        return client != null && client.getOrganizationIsSignatureEnabled();
    }

    private interface ClientIsSignatureEnableAware {
        boolean getOrganizationIsSignatureEnabled();
    }

    private interface DocumentSignatureRequestDetailsAware {
        Long getSignatureTemplateId();

        String getDocumentClientOrganizationAlternativeId();

        String getDocumentClientLegacyId();

        Long getDocumentId();
    }
}
