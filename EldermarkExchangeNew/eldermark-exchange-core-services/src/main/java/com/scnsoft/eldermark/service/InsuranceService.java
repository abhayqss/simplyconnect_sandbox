package com.scnsoft.eldermark.service;

import java.util.List;

import com.scnsoft.eldermark.entity.InNetworkInsurance;
import com.scnsoft.eldermark.entity.InsurancePlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InsuranceService {

    InNetworkInsurance getNetwork(Long networkId);

    Page<InNetworkInsurance> find(String title, Pageable pageable);
    
    List<InsurancePlan> getInsurancePlansListByIds(List<Long> listOfIds);

    List<InNetworkInsurance> getInNetworkInsurancesListByIds(List<Long> listOfIds);

    List<InsurancePlan> findAllPaymentPlans();

    List<InsurancePlan> findPaymentPlansByNetwork(Long insuranceNetworkId);
}
