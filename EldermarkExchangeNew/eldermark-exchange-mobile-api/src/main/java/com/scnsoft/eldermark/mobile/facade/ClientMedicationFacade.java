package com.scnsoft.eldermark.mobile.facade;


import com.scnsoft.eldermark.beans.ClientMedicationFilter;
import com.scnsoft.eldermark.mobile.dto.ccd.medication.MedicationDto;
import com.scnsoft.eldermark.mobile.dto.ccd.medication.MedicationListItemDto;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledValueEntityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientMedicationFacade {

    Page<MedicationListItemDto> find(ClientMedicationFilter filter, Pageable pageRequest);

    List<NamedTitledValueEntityDto<Long>> countGroupedByStatus(Long clientId);

    MedicationDto findById(Long id);

    Long save(MedicationDto dto);

    boolean canView(Long clientId);

    boolean canAdd(Long clientId);
}
