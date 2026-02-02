package com.scnsoft.eldermark.services.marketplace.internal;

import com.scnsoft.eldermark.dao.marketplace.EmergencyServiceDao;
import com.scnsoft.eldermark.entity.marketplace.EmergencyService;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.scnsoft.eldermark.dao.marketplace.EmergencyServiceDao.ORDER_BY_DISPLAY_NAME;

/**
 * @author phomal
 * Created on 11/28/2017.
 */
@Component
public class EmergencyServicesDtoSupplier extends AbstractDtoListSupplier {

    @Autowired
    private EmergencyServiceDao emergencyServiceDao;

    @Override
    public List<KeyValueDto> get() {
        List<EmergencyService> emergencyServices = emergencyServiceDao.findAll(new Sort(ORDER_BY_DISPLAY_NAME));
        return EntityListToDtoListConverter.convert(emergencyServices);
    }

}
