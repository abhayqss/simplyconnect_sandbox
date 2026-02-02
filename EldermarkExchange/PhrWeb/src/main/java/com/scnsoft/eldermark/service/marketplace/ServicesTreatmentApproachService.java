package com.scnsoft.eldermark.service.marketplace;

import com.scnsoft.eldermark.dao.marketplace.ServicesTreatmentApproachDao;
import com.scnsoft.eldermark.entity.marketplace.ServicesTreatmentApproach;
import com.scnsoft.eldermark.web.entity.ServicesTreatmentApproachInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.scnsoft.eldermark.dao.marketplace.ServicesTreatmentApproachDao.ORDER_BY_DISPLAY_NAME;

@Service
@Transactional
public class ServicesTreatmentApproachService extends BaseDisplayableNamedKeyEntityService<ServicesTreatmentApproachInfoDto, ServicesTreatmentApproach> {

    @Autowired
    private ServicesTreatmentApproachDao servicesTreatmentApproachDao;

    public List<ServicesTreatmentApproachInfoDto> listServicesTreatmentApproaches() {
        List<ServicesTreatmentApproach> servicesTreatmentApproaches = servicesTreatmentApproachDao.findAll(new Sort(ORDER_BY_DISPLAY_NAME));
        return transform(servicesTreatmentApproaches);
    }

    @Override
    protected ServicesTreatmentApproachInfoDto createNewDto() {
        return new ServicesTreatmentApproachInfoDto();
    }
}
