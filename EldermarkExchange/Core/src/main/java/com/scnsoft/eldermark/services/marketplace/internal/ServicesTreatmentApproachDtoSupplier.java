package com.scnsoft.eldermark.services.marketplace.internal;

import com.scnsoft.eldermark.dao.marketplace.ServicesTreatmentApproachDao;
import com.scnsoft.eldermark.entity.marketplace.ServicesTreatmentApproach;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.PrimaryFocusKeyValueDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.scnsoft.eldermark.dao.marketplace.ServicesTreatmentApproachDao.ORDER_BY_DISPLAY_NAME;

/**
 * @author phomal
 * Created on 11/28/2017.
 */
@Component
public class ServicesTreatmentApproachDtoSupplier extends AbstractDtoListSupplier {

    @Autowired
    private ServicesTreatmentApproachDao servicesTreatmentApproachDao;

    @Override
    public List<PrimaryFocusKeyValueDto> get() {
        List<ServicesTreatmentApproach> servicesTreatmentApproaches = servicesTreatmentApproachDao.findAll(new Sort(ORDER_BY_DISPLAY_NAME));
        return EntityListToDtoListConverter.convertPrimaryFocusKeyValueDto(servicesTreatmentApproaches);
    }

}
