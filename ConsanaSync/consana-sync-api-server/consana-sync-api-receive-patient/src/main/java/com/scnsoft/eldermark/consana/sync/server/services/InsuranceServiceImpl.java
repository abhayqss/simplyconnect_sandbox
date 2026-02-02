package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.dao.ConsanaResidentInsuranceDao;
import com.scnsoft.eldermark.consana.sync.server.model.entity.ConsanaResidentInsurance;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(noRollbackFor = Exception.class)
public class InsuranceServiceImpl implements InsuranceService {

    @Autowired
    private ConsanaResidentInsuranceDao consanaResidentInsuranceDao;

    @Override
    public Long countResidentInsurances(Long residentId) {
        return consanaResidentInsuranceDao.countByResidentId(residentId);
    }

    @Override
    public ConsanaResidentInsurance updateInsurance(ConsanaResidentInsurance insurance, Resident resident) {
        insurance.setResident(resident);
        return consanaResidentInsuranceDao.save(insurance);
    }
}
