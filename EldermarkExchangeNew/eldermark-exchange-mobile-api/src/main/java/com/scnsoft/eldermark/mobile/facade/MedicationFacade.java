package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.mobile.dto.medication.MedicationSearchResultDto;
import com.scnsoft.eldermark.mobile.dto.medication.SearchMedicationFilter;

import java.util.List;

public interface MedicationFacade {

    List<MedicationSearchResultDto> find(SearchMedicationFilter filter);

    MedicationSearchResultDto findByMediSpanId(String mediSpanId);
}
