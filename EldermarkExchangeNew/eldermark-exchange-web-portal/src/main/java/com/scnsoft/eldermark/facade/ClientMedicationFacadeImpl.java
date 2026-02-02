package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ClientMedicationCount;
import com.scnsoft.eldermark.beans.ClientMedicationFilter;
import com.scnsoft.eldermark.beans.ClientMedicationStatus;
import com.scnsoft.eldermark.dto.MedicationDto;
import com.scnsoft.eldermark.dto.MedicationListItemDto;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledValueEntityDto;
import com.scnsoft.eldermark.entity.medication.ClientMedication;
import com.scnsoft.eldermark.service.ClientMedicationService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ClientMedicationFacadeImpl implements ClientMedicationFacade {

    @Autowired
    private ClientMedicationService clientMedicationService;

    @Autowired
    private Converter<ClientMedication, MedicationDto> medicationDtoConverter;

    @Autowired
    private Converter<ClientMedication, MedicationListItemDto> medicationListItemDtoConverter;

    @Autowired
    private PermissionFilterService permissionFilterService;

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
    @PreAuthorize("@medicationSecurityService.canView(#id)")
    public MedicationDto findById(@P("id") Long id) {
        return medicationDtoConverter.convert(clientMedicationService.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@medicationSecurityService.canViewList() && " +
            "@medicationSecurityService.canViewOfClientIfPresent(#filter)")
    public List<NamedTitledValueEntityDto<Long>> countGroupedByStatus(@P("filter") ClientMedicationFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var statistics = clientMedicationService.countGroupedByStatus(filter, permissionFilter);
        return Stream.of(ClientMedicationStatus.values())
                .map(clientMedicationStatus -> new NamedTitledValueEntityDto<>(clientMedicationStatus.name(), clientMedicationStatus.getTitle(), findCountByStatus(statistics, clientMedicationStatus)))
                .collect(Collectors.toList());
    }

    private Long findCountByStatus(List<ClientMedicationCount> statistics, ClientMedicationStatus status) {
        return statistics.stream().filter(clientProblemCount -> clientProblemCount.getStatus() == status).findFirst().map(ClientMedicationCount::getCount).orElse(0L);
    }

}
