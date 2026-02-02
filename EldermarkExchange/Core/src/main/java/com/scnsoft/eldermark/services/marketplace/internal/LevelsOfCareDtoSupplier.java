package com.scnsoft.eldermark.services.marketplace.internal;

import com.scnsoft.eldermark.dao.marketplace.LevelOfCareDao;
import com.scnsoft.eldermark.entity.marketplace.LevelOfCare;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.scnsoft.eldermark.dao.marketplace.LevelOfCareDao.ORDER_BY_DISPLAY_NAME;

/**
 * @author phomal
 * Created on 11/28/2017.
 */
@Component
public class LevelsOfCareDtoSupplier extends AbstractDtoListSupplier {

    @Autowired
    private LevelOfCareDao levelOfCareDao;

    @Override
    public List<KeyValueDto> get() {
        List<LevelOfCare> levelsOfCare = levelOfCareDao.findAll(new Sort(ORDER_BY_DISPLAY_NAME));
        return EntityListToDtoListConverter.convert(levelsOfCare);
    }

}
