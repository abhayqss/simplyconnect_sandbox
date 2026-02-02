package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.marketplace.InsurancePlanDao;
import com.scnsoft.eldermark.entity.marketplace.InsurancePlan;
import com.scnsoft.eldermark.shared.marketplace.InsurancePlanDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stsiushkevich
 */

@Service
@Transactional
public class InNetworkInsurancePlanServiceImpl implements InNetworkInsurancePlanService {

    @Autowired
    InsurancePlanDao insurancePlanDao;

    @Override
    public List<InsurancePlanDto> findAllByInsuranceId(Long insuranceId) {
        List<InsurancePlanDto> dtos = new ArrayList<>();

        List<InsurancePlan> plans = insurancePlanDao.getAllByInNetworkInsuranceIdOrderByDisplayNameAsc(insuranceId);

        for (InsurancePlan plan: plans) {
            dtos.add(transformToDto(plan));
        }

        return dtos;
    }

    private InsurancePlanDto transformToDto(InsurancePlan domain) {
        InsurancePlanDto dto = new InsurancePlanDto();
        dto.setId(domain.getId());
        dto.setName(domain.getDisplayName());
        dto.setInsuranceId(domain.getInNetworkInsuranceId());
        return dto;
    }
}
