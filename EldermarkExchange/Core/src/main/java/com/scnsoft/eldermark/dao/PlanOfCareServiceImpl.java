package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.PlanOfCareDao;
import com.scnsoft.eldermark.entity.CcdSection;
import com.scnsoft.eldermark.entity.PlanOfCare;
import com.scnsoft.eldermark.services.ccd.section.PlanOfCareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanOfCareServiceImpl implements PlanOfCareService{
    @Autowired
    private PlanOfCareDao planOfCareDao;

    @Override
    public String getFreeTextById(Long id) {
        return planOfCareDao.get(id).getFreeText();
    }

    @Override
    public CcdSection getSection() {
        return CcdSection.PLAN_OF_CARE;
    }
}
