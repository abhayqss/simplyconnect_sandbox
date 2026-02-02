package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.InNetworkInsuranceDao;
import com.scnsoft.eldermark.dao.InsuranceNetworkNameDao;
import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.entity.InNetworkInsurance;
import com.scnsoft.eldermark.entity.InsuranceNetworkName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InsuranceNetworkServiceImpl implements InsuranceNetworkService {

    @Autowired
    private InNetworkInsuranceDao inNetworkInsuranceDao;

    @Autowired
    private InsuranceNetworkNameDao insuranceNetworkNameDao;

    @Override
    @Transactional(readOnly = true)
    public InNetworkInsurance getById(Long id) {
        return inNetworkInsuranceDao.getOne(id);
    }

    @Override
    public List<InsuranceNetworkName> findNamesLike(Long organizationId, String value) {
        return insuranceNetworkNameDao.findAllByNameLikeAndOrganizationId(SpecificationUtils.wrapWithWildcards(value != null ? value : ""), organizationId);
    }
}
