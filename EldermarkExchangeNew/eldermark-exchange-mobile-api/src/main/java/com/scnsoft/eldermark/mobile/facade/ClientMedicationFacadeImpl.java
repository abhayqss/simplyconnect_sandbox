package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.ClientMedicationCount;
import com.scnsoft.eldermark.beans.ClientMedicationFilter;
import com.scnsoft.eldermark.beans.ClientMedicationStatus;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientMedicationSecurityAwareEntity;
import com.scnsoft.eldermark.dto.medication.SaveMedicationRequest;
import com.scnsoft.eldermark.entity.medication.ClientMedication;
import com.scnsoft.eldermark.mobile.dto.ccd.medication.MedicationDto;
import com.scnsoft.eldermark.mobile.dto.ccd.medication.MedicationListItemDto;
import com.scnsoft.eldermark.service.ClientMedicationService;
import com.scnsoft.eldermark.service.MedicationService;
import com.scnsoft.eldermark.service.security.ClientMedicationSecurityService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledValueEntityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Transactional
public class ClientMedicationFacadeImpl implements ClientMedicationFacade {

    @Autowired
    private ClientMedicationService clientMedicationService;

    @Autowired
    private MedicationService medicationService;

    @Autowired
    private Converter<ClientMedication, MedicationDto> medicationDtoConverter;

    @Autowired
    private Converter<ClientMedication, MedicationListItemDto> medicationListItemDtoConverter;

    @Autowired
    private Converter<MedicationDto, SaveMedicationRequest> saveMedicationRequestConverter;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private ClientMedicationSecurityService clientMedicationSecurityService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@medicationSecurityService.canViewList() && " +
            "@medicationSecurityService.canViewOfClientIfPresent(#filter)")
    public Page<MedicationListItemDto> find(@P("filter") ClientMedicationFilter filter, Pageable pageRequest) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return clientMedicationService.find(filter, permissionFilter, pageRequest).map(medicationListItemDtoConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@medicationSecurityService.canViewList() && " +
            "@medicationSecurityService.canViewOfClient(#clientId)")
    public List<NamedTitledValueEntityDto<Long>> countGroupedByStatus(@P("clientId") Long clientId) {
        var filter = buildClientMedicationFilterAllIncluded(clientId);
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var counts = clientMedicationService.countGroupedByStatus(filter, permissionFilter);
        return Stream.of(ClientMedicationStatus.values())
                .map(clientMedicationStatus ->
                        new NamedTitledValueEntityDto<>(
                                clientMedicationStatus.name(),
                                clientMedicationStatus.getTitle(),
                                findCountByStatus(counts, clientMedicationStatus))
                )
                .collect(Collectors.toList());
    }

    private Long findCountByStatus(List<ClientMedicationCount> counts, ClientMedicationStatus status) {
        return counts.stream()
                .filter(clientProblemCount -> clientProblemCount.getStatus() == status)
                .findFirst().map(ClientMedicationCount::getCount).orElse(0L);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@medicationSecurityService.canView(#id)")
    public MedicationDto findById(@P("id") Long id) {
        return medicationDtoConverter.convert(clientMedicationService.findById(id));
    }

    @Override
    @Transactional
    @PreAuthorize(
            "#dto.getId() == null " +
                    "? @medicationSecurityService.canAdd(T(com.scnsoft.eldermark.beans.security.projection.entity.ClientMedicationSecurityAwareEntity).of(#dto.clientId)) " +
                    ": @medicationSecurityService.canEdit(#dto.id)"
    )
    public Long save(MedicationDto dto) {
        var request = Objects.requireNonNull(saveMedicationRequestConverter.convert(dto));
        request.setAuthor(loggedUserService.getCurrentEmployee());
        return medicationService.save(request).getId();
    }

    public ClientMedicationFilter buildClientMedicationFilterAllIncluded(@P("clientId") Long clientId) {
        var filter = new ClientMedicationFilter();
        filter.setClientId(clientId);
        filter.setIncludeActive(true);
        filter.setIncludeInactive(true);
        filter.setIncludeUnknown(true);
        return filter;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canView(Long clientId) {
        return clientMedicationSecurityService.canViewOfClient(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAdd(Long clientId) {
        return clientMedicationSecurityService.canAdd(ClientMedicationSecurityAwareEntity.of(clientId));
    }
}
