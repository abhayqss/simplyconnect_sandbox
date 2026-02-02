package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.InsuranceNetworkDto;
import com.scnsoft.eldermark.dto.InsurancePlanDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InsuranceFacade {

    List<InsuranceNetworkDto> getNetworks(String title);

    List<InsurancePlanDto> getPaymentPlans(Long networkId);

    Page<InsuranceNetworkDto> find(String title, Pageable pageable);

    List<String> findAggregatedNamesLike(Long organizationId, String value);
}
