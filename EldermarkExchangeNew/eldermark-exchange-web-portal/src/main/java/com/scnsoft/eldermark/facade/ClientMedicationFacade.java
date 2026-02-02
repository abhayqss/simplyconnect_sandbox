package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ClientMedicationFilter;
import com.scnsoft.eldermark.dto.MedicationDto;
import com.scnsoft.eldermark.dto.MedicationListItemDto;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledValueEntityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientMedicationFacade {

    Page<MedicationListItemDto> find(ClientMedicationFilter filter, Pageable pageRequest);

    MedicationDto findById(Long id);

    List<NamedTitledValueEntityDto<Long>> countGroupedByStatus(ClientMedicationFilter filter);

}
