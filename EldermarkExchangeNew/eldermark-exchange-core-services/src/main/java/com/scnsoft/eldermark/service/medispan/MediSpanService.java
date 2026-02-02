package com.scnsoft.eldermark.service.medispan;

import com.scnsoft.eldermark.service.medispan.dto.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MediSpanService {

    // Routed drugs

    List<MediSpanRoutedDrug> findRoutedDrugByMediSpanIds(Collection<String> mediSpanIds, List<String> fields);

    Optional<MediSpanRoutedDrug> findRoutedDrugByMediSpanId(String mediSpanId, List<String> fields);

    // Dispensable drugs

    Optional<MediSpanDispensableDrug> findDispensableDrugByMediSpanId(String name, List<String> fields);

    List<MediSpanDispensableDrug> findDispensableDrugsByName(String name, List<String> fields);

    List<MediSpanDispensableDrug> findDispensableDrugsByName(String name, List<String> fields, long count, long offset);

    // Packaged drugs

    List<MediSpanPackagedDrug> findPackagedDrugsPpids(Collection<String> ppids, List<String> fields);

    Optional<MediSpanPackagedDrug> findPackagedDrugByNdc(String ndc, List<String> fields);

    // Other

    List<MediSpanRoute> findRoutesByIds(Collection<String> ids, List<String> fields);

    Optional<MediSpanRoute> findRouteById(String id);

    List<MediSpanDoseForm> findDoseFormsByMediSpanIds(Collection<String> mediSpanIds, List<String> fields);

    Optional<MediSpanDoseForm> findDoseFormByMediSpanId(String mediSpanId);
}
