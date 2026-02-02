package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.LabResearchOrderFilter;
import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.lab.*;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.basic.DisplayableNamedEntity;
import com.scnsoft.eldermark.entity.lab.*;
import com.scnsoft.eldermark.entity.lab.review.LabResearchOrderBulkReviewListItem;
import com.scnsoft.eldermark.service.CareCoordinationConstants;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.LabResearchOrderService;
import com.scnsoft.eldermark.service.security.LabSecurityService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LabResearchOrderFacadeImpl implements LabResearchOrderFacade {

    @Autowired
    private LabResearchOrderService labResearchOrderService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private LabSecurityService labSecurityService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private Converter<LabResearchOrderListItem, LabResearchOrderListItemDto> labOrderLabResearchOrderListItemDtoConverter;

    @Autowired
    private Converter<LabResearchOrderDto, LabResearchOrder> labResearchOrderConverter;

    @Autowired
    private Converter<LabResearchOrder, LabResearchOrderDto> labResearchOrderDtoConverter;

    @Autowired
    private Converter<Address, AddressDto> addressDtoConverter;

    @Autowired
    private Converter<LabResearchOrderObservationResult, LabResearchTestResultListItemDto> labResultDtoConverter;

    @Autowired
    private ListAndItemConverter<LabResearchOrderBulkReviewListItem, LabResearchOrderBulkReviewListItemDto> labResearchOrderBulkReviewListItemDtoConverter;

    @Override
    @Transactional
    @PreAuthorize("@labSecurityService.canAdd(#dto)")
    public Long add(@P("dto") LabResearchOrderDto dto) {
        clientService.validateActive(dto.getClientId());
        return labResearchOrderService.create(labResearchOrderConverter.convert(dto)).getId();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@labSecurityService.canView(#id)")
    public LabResearchOrderDto findById(@P("id") Long id) {
        return labResearchOrderDtoConverter.convert(labResearchOrderService.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@labSecurityService.canViewList(#filter.organizationId)")
    public Page<LabResearchOrderListItemDto> find(@P("filter") LabResearchOrderFilter filter, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return labResearchOrderService.findLabOrders(filter, permissionFilter, PaginationUtils.applyEntitySort(pageable, LabResearchOrderListItemDto.class))
                .map(labOrderLabResearchOrderListItemDtoConverter::convert);
    }

    @Override
    @Transactional
    @PreAuthorize("@labSecurityService.canReview(#reviewDto.orderIds)")
    public void review(@P("reviewDto") LabResearchReviewDto reviewDto) {
        for (Long labOrderId : reviewDto.getOrderIds()) {
            labResearchOrderService.review(labOrderId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@labSecurityService.canViewResults(#labOrderId)")
    public Page<LabResearchTestResultListItemDto> findTestResults(@P("labOrderId") Long labOrderId, Pageable pageable) {
        return labResearchOrderService.findLabResults(labOrderId, PaginationUtils.sortByDefault(pageable, Sort.by(LabResearchOrderObservationResult_.NAME)))
                .map(labResultDtoConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    //directory data, no permissions check
    public List<LabIcd10GroupDto> findIcdGroupCodes() {
        return labResearchOrderService.findIcdGroupCodes()
                .stream()
                .map(labIcd10Group -> new LabIcd10GroupDto(labIcd10Group.getId(), labIcd10Group.getTitle(), labIcd10Group.getName(), labIcd10Group.getCodes()
                        .stream()
                        .sorted(Comparator.comparing(LabIcd10Code::getOrder))
                        .map(labIcd10Code -> new IdentifiedNamedTitledEntityDto(labIcd10Code.getId(), labIcd10Code.getCode(), labIcd10Code.getTitle()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    //directory data, no permissions check
    public List<IdentifiedNamedTitledEntityDto> findSpecimens() {
        return labResearchOrderService.findSpecimens()
                .stream()
                .map(specimenType -> new IdentifiedNamedTitledEntityDto(specimenType.getId(), specimenType.getName(), specimenType.getTitle()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canView(#clientId)")
    public LabResearchOrderDto findDefault(@P("clientId") Long clientId) {
        var result = new LabResearchOrderDto();
        var clientDto = new ClientSummaryLabsAdaptDto();

        var client = clientService.findById(clientId);
        clientDto.setId(client.getId());
        clientDto.setFirstName(client.getFirstName());
        clientDto.setLastName(client.getLastName());
        clientDto.setFullName(client.getFullName());
        if (client.getGender() != null) {
            clientDto.setGenderId(client.getGender().getId());
            clientDto.setGenderTitle(client.getGender().getDisplayName());
        }
        if (client.getRace() != null && CareCoordinationConstants.AVAILABLE_RACES.contains(client.getRace().getDisplayName())) {
            clientDto.setRaceId(client.getRace().getId());
            clientDto.setRaceTitle(client.getRace().getDisplayName());
        }
        clientDto.setBirthDate(DateTimeUtils.formatLocalDate(client.getBirthDate()));
        clientDto.setSsn(client.getSsnLastFourDigits());
        if (CollectionUtils.isNotEmpty(client.getPerson().getAddresses())) {
            clientDto.setAddress(
                    addressDtoConverter.convert(client.getPerson().getAddresses().get(0))
            );
        }

        clientDto.setInsuranceNetwork(
                Optional.ofNullable(client.getInNetworkInsurance())
                        .map(DisplayableNamedEntity::getDisplayName)
                        .orElse(null)
        );
        clientDto.setPolicyNumber(client.getMemberNumber());

        var policyHolder = LabOrderPolicyHolder.SELF;
        clientDto.setPolicyHolderRelationName(policyHolder.name());
        clientDto.setPolicyHolderRelationTitle(policyHolder.getDisplayName());


        if (client.getPerson() != null) {
            clientDto.setPhone(PersonTelecomUtils.findValue(client.getPerson(), PersonTelecomCode.HP)
                    .orElse(null));

        }
        result.setClient(clientDto);

        var community = client.getCommunity();
        result.setClinic(community.getName());
        result.setClinicAddress(CareCoordinationUtils.getFistNotNull(community.getAddresses())
                .map(addr -> addr.getDisplayAddress(", ")).orElse(null));

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@labSecurityService.canAddToCommunity(#communityId)")
    public List<IdentifiedTitledEntityDto> findCollectorSites(@P("communityId") Long communityId) {
        var organizationId = communityService.findById(communityId, OrganizationIdAware.class).getOrganizationId();
        return communityService.findAllByOrgId(organizationId).stream()
                .map(community -> new IdentifiedTitledEntityDto(community.getId(), community.getName())).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddToCommunity(Long communityId) {
        return labSecurityService.canAddToCommunity(communityId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canView() {
        return labSecurityService.canViewLabs();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@labSecurityService.canAddToCommunity(#communityId)")
    public LabResearchOrderOrganizationUniquenessDto validateUniqueInOrganization(String requisitionNumber, @P("communityId") Long communityId) {
        boolean requisitionNumberUnique = labResearchOrderService.isRequisitionNumberUniqueInOrganization(requisitionNumber, communityService.findById(communityId).getOrganizationId());
        return new LabResearchOrderOrganizationUniquenessDto(requisitionNumberUnique);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddToClient(Long clientId) {
        Client client = clientService.findById(clientId);
        return labSecurityService.canAddToCommunity(client.getCommunityId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canReview(Long organizationId) {
        return labSecurityService.canReviewInOrganization(organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@labSecurityService.canViewList(#filter.organizationId)")
    public long count(@P("filter") LabResearchOrderFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return labResearchOrderService.count(filter, permissionFilter);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@labSecurityService.canReviewInOrganization(#organizationId)")
    public List<LabResearchOrderBulkReviewListItemDto> findPendingReview(@P("organizationId") Long organizationId, List<Long> communityIds) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var filter = new LabResearchOrderFilter();
        filter.setCommunityIds(communityIds);
        filter.setStatuses(Collections.singletonList(LabResearchOrderStatus.PENDING_REVIEW));
        filter.setOrganizationId(organizationId);
        var sort = Sort.by(Sort.Order.desc(LabResearchOrder_.ORDER_DATE));
        var result = labResearchOrderService.findReviewOrders(filter, permissionFilter, sort);
        return labResearchOrderBulkReviewListItemDtoConverter.convertList(result);
    }
}
