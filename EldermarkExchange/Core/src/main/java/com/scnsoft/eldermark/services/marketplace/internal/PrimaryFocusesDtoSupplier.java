package com.scnsoft.eldermark.services.marketplace.internal;

import com.scnsoft.eldermark.dao.marketplace.PrimaryFocusDao;
import com.scnsoft.eldermark.entity.marketplace.PrimaryFocus;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.scnsoft.eldermark.dao.marketplace.PrimaryFocusDao.ORDER_BY_DISPLAY_NAME;

/**
 * @author phomal
 * Created on 11/28/2017.
 */
@Component
public class PrimaryFocusesDtoSupplier extends AbstractDtoListSupplier {

    @Autowired
    private PrimaryFocusDao primaryFocusDao;

    @Override
    public List<KeyValueDto> get() {
        List<PrimaryFocus> primaryFocuses = primaryFocusDao.findAll(new Sort(ORDER_BY_DISPLAY_NAME));
        EntityListUtils.moveItemToEnd(primaryFocuses, "other");
        return EntityListToDtoListConverter.convert(primaryFocuses);
    }

}
