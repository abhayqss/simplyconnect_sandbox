package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.shared.marketplace.InsurancePlanDto;

import java.util.List;

/**
 * @author stsiushkevich
 */

public interface InNetworkInsurancePlanService {
    List<InsurancePlanDto> findAllByInsuranceId(Long insuranceId);
}
