package com.scnsoft.eldermark.service.marketplace;

import com.scnsoft.eldermark.dao.marketplace.PrimaryFocusDao;
import com.scnsoft.eldermark.entity.marketplace.PrimaryFocus;
import com.scnsoft.eldermark.web.entity.CareTypeInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.scnsoft.eldermark.dao.marketplace.PrimaryFocusDao.ORDER_BY_DISPLAY_NAME;

@Service
@Transactional
public class CareTypeService extends BaseDisplayableNamedKeyEntityService<CareTypeInfoDto, PrimaryFocus> {

    @Autowired
    private PrimaryFocusDao primaryFocusDao;

    public List<CareTypeInfoDto> listCareTypes() {
        List<PrimaryFocus> primaryFocuses = primaryFocusDao.findAll(new Sort(ORDER_BY_DISPLAY_NAME));
        return transform(primaryFocuses);
    }

    @Override
    protected CareTypeInfoDto createNewDto() {
        return new CareTypeInfoDto();
    }

}
