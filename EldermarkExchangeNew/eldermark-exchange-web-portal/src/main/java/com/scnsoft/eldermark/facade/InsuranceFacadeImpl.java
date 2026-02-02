package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.InsuranceNetworkDto;
import com.scnsoft.eldermark.dto.InsurancePlanDto;
import com.scnsoft.eldermark.entity.InNetworkInsurance;
import com.scnsoft.eldermark.entity.InNetworkInsurance_;
import com.scnsoft.eldermark.entity.InsuranceNetworkName;
import com.scnsoft.eldermark.entity.InsurancePlan;
import com.scnsoft.eldermark.service.InsuranceNetworkService;
import com.scnsoft.eldermark.service.InsuranceService;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
//security - insurances are directory data and don't require permissions checks
public class InsuranceFacadeImpl implements InsuranceFacade {

    @Autowired
    private InsuranceService insuranceService;

    @Autowired
    private InsuranceNetworkService insuranceNetworkService;

    @Autowired
    private ListAndItemConverter<InNetworkInsurance, InsuranceNetworkDto> networkDtoListConverter;

    @Autowired
    private ListAndItemConverter<InsurancePlan, InsurancePlanDto> insurancePlanDtoConverter;

    @Override
    public List<InsuranceNetworkDto> getNetworks(String title) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "displayName"));
        return networkDtoListConverter.convertList(insuranceService.find(title, pageable).getContent());
    }

    @Override
    public List<InsurancePlanDto> getPaymentPlans(Long insuranceNetworkId) {
        List<InsurancePlan> plans;
        if (insuranceNetworkId != null) {
            plans = insuranceService.findPaymentPlansByNetwork(insuranceNetworkId);
        } else {
            plans = insuranceService.findAllPaymentPlans();
        }
        return insurancePlanDtoConverter.convertList(plans);
    }

    @Override
    public Page<InsuranceNetworkDto> find(String title, Pageable pageable) {
        var insuranceNetworks = insuranceService.find(title, PaginationUtils.sortByDefault(pageable, Sort.by(InNetworkInsurance_.DISPLAY_NAME)));
        return insuranceNetworks.map(networkDtoListConverter::convert);
    }

    @Override
    public List<String> findAggregatedNamesLike(Long organizationId, String value) {
        return insuranceNetworkService.findNamesLike(organizationId, value).stream()
                .map(InsuranceNetworkName::getName).collect(Collectors.toList());
    }
}
