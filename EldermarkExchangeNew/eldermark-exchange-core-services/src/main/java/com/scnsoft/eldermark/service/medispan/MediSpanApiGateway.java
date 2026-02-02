package com.scnsoft.eldermark.service.medispan;

import com.scnsoft.eldermark.service.medispan.dto.*;

public interface MediSpanApiGateway {

    MediSpanResponse<MediSpanDoseForm> getDoseForms(MediSpanRequest request);

    MediSpanResponse<MediSpanDispensableDrug> getDispensableDrugs(MediSpanRequest request);

    MediSpanResponse<MediSpanPackagedDrug> getPackagedDrugs(MediSpanRequest request);

    MediSpanResponse<MediSpanRoutedDrug> getRoutedDrugs(MediSpanRequest request);

    MediSpanResponse<MediSpanRoute> getRoutes(MediSpanRequest request);
}
