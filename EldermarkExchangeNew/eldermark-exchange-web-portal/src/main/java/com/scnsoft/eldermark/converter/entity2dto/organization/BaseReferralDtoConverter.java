package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.ClientProblemFilter;
import com.scnsoft.eldermark.converter.AddressDtoConverter;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.BaseAttachmentDto;
import com.scnsoft.eldermark.dto.referral.ReferralClientDto;
import com.scnsoft.eldermark.dto.referral.ReferralDto;
import com.scnsoft.eldermark.dto.referral.ReferralListItemDto;
import com.scnsoft.eldermark.dto.referral.ReferralMarketplaceDto;
import com.scnsoft.eldermark.entity.BaseAttachment;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.referral.Referral;
import com.scnsoft.eldermark.entity.referral.ReferralRequest;
import com.scnsoft.eldermark.entity.referral.ReferralResponse;
import com.scnsoft.eldermark.entity.referral.ReferralStatus;
import com.scnsoft.eldermark.service.ClientProblemService;
import com.scnsoft.eldermark.service.ReferralService;
import com.scnsoft.eldermark.service.ServicePlanService;
import com.scnsoft.eldermark.service.security.ClientProblemSecurityService;
import com.scnsoft.eldermark.service.security.ClientSecurityService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BaseReferralDtoConverter {

    @Autowired
    private AddressDtoConverter addressDtoConverter;

    @Autowired
    private ClientProblemService clientProblemService;

    @Autowired
    private ClientProblemSecurityService clientProblemSecurityService;

    @Autowired
    private ClientSecurityService clientSecurityService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private Converter<ReferralRequest, ReferralMarketplaceDto> referralMarketplaceDtoConverter;

    @Autowired
    private ServicePlanService servicePlanService;

    @Autowired
    private ListAndItemConverter<BaseAttachment, BaseAttachmentDto> baseAttachmentDtoConverter;

    @Autowired
    private ReferralService referralService;

    protected void fill(Referral source, ReferralDto target) {
        target.setId(source.getId());
        target.setPriorityId(source.getPriority().getId());
        target.setPriorityName(source.getPriority().getCode());
        target.setPriorityTitle(source.getPriority().getDisplayName());
        target.setServiceTitle(source.getServiceName());
        target.setClient(fillClient(source.getClient(), source));
        target.setPerson(source.getReferringIndividual());
        target.setCommunityTitle(source.getRequestingCommunity().getName());
        target.setOrganizationEmail(source.getRequestingOrganizationEmail());
        target.setOrganizationPhone(source.getRequestingOrganizationPhone());
        target.setIsServicePlanShared(source.isServicePlanShared());
        target.setIsCcdShared(source.isCcdShared());
        target.setIsFacesheetShared(source.isFacesheetShared());
        target.setDate(source.getRequestDatetime().toEpochMilli());
        target.setInstructions(source.getReferralInstructions());
        target.setHasSharedServicePlan(
                source.getClient() != null &&
                        servicePlanService.existsUnarchivedSharedWithClientForClient(source.getClient().getId())
        );
        if (source.isMarketplace()) {
            source.getReferralRequestIds().stream()
                    .min(Long::compare)
                    .map(referralService::getRequestById)
                    .map(referralMarketplaceDtoConverter::convert)
                    .ifPresent(target::setMarketplace);
        }

        target.setAttachments(baseAttachmentDtoConverter.convertList(source.getAttachments()));
    }

    protected void fill(Referral source, ReferralListItemDto target) {
        target.setId(source.getId());
        if (source.getClient() != null) {
            target.setName(source.getClient().getFullName());
        } else {
            target.setName(source.getRequestingEmployee().getFullName());
        }
        target.setServiceTitle(source.getServiceName());
        target.setDate(source.getRequestDatetime().toEpochMilli());
        target.setPriorityName(source.getPriority().getCode());
        target.setPriorityTitle(source.getPriority().getDisplayName());
    }

    protected ReferralStatus resolveInboundStatus(ReferralRequest source) {
        if (source.getLastResponse() != null && source.getLastResponse().getResponse().equals(ReferralResponse.DECLINED)) {
            return source.getLastResponse().getResponse().getReferralStatus();
        } else {
            return source.getReferral().getReferralStatus();
        }
    }

    private ReferralClientDto fillClient(Client source, Referral referral) {
        if (source == null) {
            return null;
        }
        ReferralClientDto target = new ReferralClientDto();
        target.setId(source.getId());
        target.setFullName(source.getFullName());
        target.setGender(source.getGender() != null ? source.getGender().getDisplayName() : null);
        target.setBirthDate(DateTimeUtils.formatLocalDate(source.getBirthDate()));
        target.setDiagnoses(loadDiagnoses(source));
        target.setLocation(referral.getClientLocation());
        target.setLocationPhone(referral.getLocationPhone());
        target.setAddress(addressDtoConverter.fromAddressFields(referral.getState(), referral.getCity(), referral.getAddress(), referral.getZipCode()));
        target.setInsuranceNetworkTitle(referral.getInNetworkInsurance());
        target.setCanView(clientSecurityService.canView(source.getId()));
        return target;
    }

    private List<String> loadDiagnoses(Client source) {
        if (!clientProblemSecurityService.canViewOfClientIfPresent(source::getId)) {
            return Collections.emptyList();
        }

        var problemFilter = new ClientProblemFilter();
        problemFilter.setClientId(source.getId());
        problemFilter.setIncludeActive(true);

        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        return clientProblemService.find(problemFilter, permissionFilter, ProblemProjection.class).stream()
                .map(ProblemProjection::getProblem)
                .collect(Collectors.toList());
    }

    public interface ProblemProjection {
        String getProblem();
    }
}
