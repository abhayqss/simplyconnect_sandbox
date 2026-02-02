package com.scnsoft.eldermark.service.medispan;

import com.scnsoft.eldermark.service.medispan.dto.MedicationSearchResult;

import java.util.List;
import java.util.Optional;

public interface MedicationSearchService {

    List<MedicationSearchResult> findByName(String name, int count, int offset);

    Optional<MedicationSearchResult> findByNdc(String ndc);

    Optional<MedicationSearchResult> findByMediSpanId(String mediSpanId);
}
