package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.phr.InNetworkInsuranceDao;
import com.scnsoft.eldermark.entity.marketplace.InsurancePlan;
import com.scnsoft.eldermark.entity.phr.InNetworkInsurance;
import com.scnsoft.eldermark.shared.marketplace.InNetworkInsuranceDto;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class InNetworkInsuranceServiceImpl implements InNetworkInsuranceService {

    @Autowired
    private InNetworkInsuranceDao inNetworkInsuranceDao;

    @Override
    public InNetworkInsuranceDto findById (Long id) {
        InNetworkInsurance network = inNetworkInsuranceDao.findById(id);
        return transformToDto(network);
    }

    @Override
    public List<InNetworkInsuranceDto> findByIds(List<Long> ids) {
        List<InNetworkInsurance> networks = inNetworkInsuranceDao.findInNetworkInsuranceByIdInOrderByDisplayNameAsc(ids);
        List<InNetworkInsuranceDto> dtos = new ArrayList<>();
        for (InNetworkInsurance network : networks) {
            dtos.add(transformToDto(network));
        }
        return dtos;
    }

    private InNetworkInsuranceDto transformToDto(InNetworkInsurance source) {
        InNetworkInsuranceDto target = new InNetworkInsuranceDto();
        target.setId(source.getId());
        target.setName(source.getDisplayName());
        target.setPopular(source.getPopular());
        if (!source.getInsurancePlans().isEmpty()) {
            Collections.sort(source.getInsurancePlans(), new Comparator<InsurancePlan>() {
                @Override
                public int compare(InsurancePlan o1, InsurancePlan o2) {
                    return o1.getDisplayName().compareTo(o2.getDisplayName());
                }
            });
        }
        target.setInsurancePlans(source.getInsurancePlans());
        List<Long> planIds = new ArrayList<>();
        CollectionUtils.collect(source.getInsurancePlans(), new BeanToPropertyValueTransformer("id"), planIds);
        target.setPlanIds(planIds);
        return target;
    }
}
