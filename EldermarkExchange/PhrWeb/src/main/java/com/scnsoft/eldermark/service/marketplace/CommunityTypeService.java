package com.scnsoft.eldermark.service.marketplace;

import com.scnsoft.eldermark.dao.marketplace.CommunityTypeDao;
import com.scnsoft.eldermark.entity.marketplace.CommunityType;
import com.scnsoft.eldermark.web.entity.CommunityTypeInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.scnsoft.eldermark.dao.marketplace.CommunityTypeDao.ORDER_BY_DISPLAY_NAME;

@Service
@Transactional
public class CommunityTypeService extends BaseDisplayableNamedKeyEntityService<CommunityTypeInfoDto, CommunityType> {

    @Autowired
    private CommunityTypeDao communityTypeDao;

    public List<CommunityTypeInfoDto> listCommunityTypes() {
        List<CommunityType> communityTypes = communityTypeDao.findAll(new Sort(ORDER_BY_DISPLAY_NAME));
        return transform(communityTypes);
    }

    @Override
    protected CommunityTypeInfoDto createNewDto() {
        return new CommunityTypeInfoDto();
    }
}
