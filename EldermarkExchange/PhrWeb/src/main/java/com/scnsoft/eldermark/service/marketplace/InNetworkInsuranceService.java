package com.scnsoft.eldermark.service.marketplace;

import com.scnsoft.eldermark.dao.marketplace.InsurancePlanDao;
import com.scnsoft.eldermark.dao.phr.InNetworkInsuranceDao;
import com.scnsoft.eldermark.entity.phr.InNetworkInsurance;
import com.scnsoft.eldermark.web.entity.InNetworkInsuranceInfoDto;
import com.scnsoft.eldermark.web.entity.InNetworkInsurancePageInfoDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.scnsoft.eldermark.dao.phr.InNetworkInsuranceDao.ORDER_BY_DISPLAY_NAME;
import static com.scnsoft.eldermark.shared.utils.PaginationUtils.buildPageable;

@Service
@Transactional
public class InNetworkInsuranceService extends BaseDisplayableNamedKeyEntityService<InNetworkInsuranceInfoDto, InNetworkInsurance> {

    @Autowired
    private InNetworkInsuranceDao inNetworkInsuranceDao;

    @Autowired
    private InsurancePlanDao insurancePlanDao;

    public Page<InNetworkInsuranceInfoDto> listInNetworkInsurances(String searchText, Integer pageSize, Integer page) {
        String likeFormatSearchStr;
        Page<InNetworkInsurance> networkInsuranceList;
        Pageable pageable;
        if (StringUtils.isNotBlank(searchText)) {
            likeFormatSearchStr = String.format("%%%s%%", searchText);
            pageable = buildPageable(pageSize, page);
            networkInsuranceList = inNetworkInsuranceDao.getAllByDisplayNameLikeOrderByDisplayNameAsc(likeFormatSearchStr, pageable);
        } else {
            pageable = buildPageable(pageSize, page, new Sort(ORDER_BY_DISPLAY_NAME));
            if (pageable != null) {
                networkInsuranceList = inNetworkInsuranceDao.findAll(pageable);
            } else {
                List<InNetworkInsurance> list = inNetworkInsuranceDao.findAll(new Sort(ORDER_BY_DISPLAY_NAME));
                networkInsuranceList = new PageImpl<>(list, pageable, list.size());
            }

        }
        return new PageImpl<>(transform(networkInsuranceList.getContent()), pageable, networkInsuranceList.getTotalElements());
    }

    public InNetworkInsurancePageInfoDto getPageInfo(Long inNetworkInsuranceId, Long insurancePlanId, Integer pageSize) {
        InNetworkInsurancePageInfoDto result = new InNetworkInsurancePageInfoDto();
        Integer number = null;
        if (inNetworkInsuranceId == null) {
            inNetworkInsuranceId = insurancePlanDao.getInNetworkInsuranceIdById(insurancePlanId);
        }
        InNetworkInsurance searchInNetworkInsurance = inNetworkInsuranceDao.getOne(inNetworkInsuranceId);
        if (searchInNetworkInsurance == null) {
            return null;
        }
        List<InNetworkInsurance> insurances = inNetworkInsuranceDao.findAll(new Sort(ORDER_BY_DISPLAY_NAME));
        if (!CollectionUtils.isEmpty(insurances)) {
            number = findNumber(0, pageSize, insurances.size()-1, searchInNetworkInsurance.getDisplayName(), insurances);
        }
        result.setInsuranceId(inNetworkInsuranceId);
        result.setPage(number != null ? number/pageSize : null);
        return result;
    }

    @Override
    protected InNetworkInsuranceInfoDto createNewDto() {
        return new InNetworkInsuranceInfoDto();
    }

    @Override
    protected InNetworkInsuranceInfoDto transformListItem(InNetworkInsurance inNetworkInsurance) {
        InNetworkInsuranceInfoDto result =  super.transformListItem(inNetworkInsurance);
        result.setHasPlans(!CollectionUtils.isEmpty(inNetworkInsurance.getInsurancePlans()));
        return result;
    }
}
