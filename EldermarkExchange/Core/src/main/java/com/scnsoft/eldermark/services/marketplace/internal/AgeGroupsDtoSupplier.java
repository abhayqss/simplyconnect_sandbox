package com.scnsoft.eldermark.services.marketplace.internal;

import com.scnsoft.eldermark.dao.marketplace.AgeGroupDao;
import com.scnsoft.eldermark.entity.marketplace.AgeGroup;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.scnsoft.eldermark.dao.marketplace.AgeGroupDao.ORDER_BY_DISPLAY_ORDER;

/**
 * @author phomal
 * Created on 11/28/2017.
 */
@Component
public class AgeGroupsDtoSupplier extends AbstractDtoListSupplier {

    @Autowired
    private AgeGroupDao ageGroupDao;

    @Override
    public List<KeyValueDto> get() {
        List<AgeGroup> ageGroups = ageGroupDao.findAll(new Sort(ORDER_BY_DISPLAY_ORDER));
        return EntityListToDtoListConverter.convert(ageGroups);
    }

}
