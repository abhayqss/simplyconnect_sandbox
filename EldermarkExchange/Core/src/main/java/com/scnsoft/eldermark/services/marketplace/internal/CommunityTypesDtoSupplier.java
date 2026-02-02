package com.scnsoft.eldermark.services.marketplace.internal;

import com.scnsoft.eldermark.dao.marketplace.CommunityTypeDao;
import com.scnsoft.eldermark.entity.marketplace.CommunityType;
import com.scnsoft.eldermark.shared.carecoordination.PrimaryFocusKeyValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.scnsoft.eldermark.dao.marketplace.CommunityTypeDao.ORDER_BY_DISPLAY_NAME;

/**
 * @author phomal
 * Created on 11/28/2017.
 */
@Component
public class CommunityTypesDtoSupplier extends AbstractDtoListSupplier {

    @Autowired
    private CommunityTypeDao communityTypeDao;

    @Override
    public List<PrimaryFocusKeyValueDto> get() {
        List<CommunityType> communityTypes = communityTypeDao.findAll(new Sort(ORDER_BY_DISPLAY_NAME));
    	EntityListUtils.moveItemToEnd(communityTypes, "other");
    	return EntityListToDtoListConverter.convertPrimaryFocusKeyValueDto(communityTypes);
    }

}
