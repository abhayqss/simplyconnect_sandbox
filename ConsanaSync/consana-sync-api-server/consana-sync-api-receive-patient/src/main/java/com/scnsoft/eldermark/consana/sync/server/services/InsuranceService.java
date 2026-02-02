package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.model.entity.ConsanaResidentInsurance;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;

public interface InsuranceService {

    Long countResidentInsurances(Long residentId);

    ConsanaResidentInsurance updateInsurance(ConsanaResidentInsurance insurance, Resident resident);

}
