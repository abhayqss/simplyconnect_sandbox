package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.InNetworkInsurance;
import com.scnsoft.eldermark.entity.InsuranceNetworkName;

import java.util.List;

public interface InsuranceNetworkService {

    InNetworkInsurance getById(Long id);

    List<InsuranceNetworkName> findNamesLike(Long organizationId, String value);

}
