package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.InNetworkInsuranceDao;
import com.scnsoft.eldermark.dao.InsurancePlanDao;
import com.scnsoft.eldermark.entity.InNetworkInsurance;
import com.scnsoft.eldermark.entity.InsurancePlan;
import com.scnsoft.eldermark.service.internal.EntityListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InsuranceServiceImpl implements InsuranceService {

	@Autowired
	private InNetworkInsuranceDao inNetworkInsuranceDao;

	@Autowired
	private InsurancePlanDao insurancePlanDao;

	@Override
	public InNetworkInsurance getNetwork(Long networkInsuranceId) {
		return inNetworkInsuranceDao.getOne(networkInsuranceId);

	}

	@Override
	public Page<InNetworkInsurance> find(String title, Pageable pageable) {
        if (StringUtils.isNoneEmpty(title)) {
            return inNetworkInsuranceDao.findByDisplayNameLike("%" + title + "%",pageable);
        } else {
            Page<InNetworkInsurance> inNetworkInsurances = inNetworkInsuranceDao.findAll(pageable);
            List<InNetworkInsurance> content = new ArrayList<>(inNetworkInsurances.getContent());
            EntityListUtils.moveItemToStart(content, "Cash or self-payment");
            return new PageImpl<>(content, pageable, inNetworkInsurances.getTotalElements());
        }
	}

	@Override
	public List<InsurancePlan> getInsurancePlansListByIds(List<Long> listOfIds) {
		return insurancePlanDao.findAllById(listOfIds);
	}

	@Override
	public List<InNetworkInsurance> getInNetworkInsurancesListByIds(List<Long> listOfIds) {
		return inNetworkInsuranceDao.findAllById(listOfIds);
	}

	@Override
	public List<InsurancePlan> findAllPaymentPlans() {
		return insurancePlanDao.findAll();
	}

	@Override
	public List<InsurancePlan> findPaymentPlansByNetwork(Long insuranceNetworkId) {
		return insurancePlanDao.findAllByInNetworkInsuranceId(insuranceNetworkId);
	}

}
