package com.scnsoft.eldermark.services.marketplace.internal;

import com.scnsoft.eldermark.dao.marketplace.AncillaryServiceDao;
import com.scnsoft.eldermark.entity.marketplace.AncillaryService;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.scnsoft.eldermark.dao.marketplace.AncillaryServiceDao.ORDER_BY_DISPLAY_NAME;

/**
 * @author phomal
 * Created on 11/28/2017.
 */
@Component
public class AncillaryServicesDtoSupplier extends AbstractDtoListSupplier {

    @Autowired
    private AncillaryServiceDao ancillaryServiceDao;

    @Override
    public List<KeyValueDto> get() {
        List<AncillaryService> ancillaryServices = ancillaryServiceDao.findAll(new Sort(ORDER_BY_DISPLAY_NAME));
        return EntityListToDtoListConverter.convert(ancillaryServices);
    }

}
