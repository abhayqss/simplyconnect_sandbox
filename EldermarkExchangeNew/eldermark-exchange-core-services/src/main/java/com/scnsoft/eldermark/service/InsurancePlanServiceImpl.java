package com.scnsoft.eldermark.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scnsoft.eldermark.dao.InsurancePlanDao;
import com.scnsoft.eldermark.entity.InsurancePlan;

@Service
public class InsurancePlanServiceImpl implements InsurancePlanService {

    @Autowired
    InsurancePlanDao InsurancePlanDao;

    @Override
    @Transactional(readOnly = true)
    public InsurancePlan findById(Long id) {
        return InsurancePlanDao.findById(id).orElse(null);
    }
}
