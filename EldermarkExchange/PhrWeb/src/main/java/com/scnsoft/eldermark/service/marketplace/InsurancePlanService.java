package com.scnsoft.eldermark.service.marketplace;

import com.scnsoft.eldermark.dao.marketplace.InsurancePlanDao;
import com.scnsoft.eldermark.entity.marketplace.InsurancePlan;
import com.scnsoft.eldermark.web.entity.InsurancePlanInfoDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.scnsoft.eldermark.dao.marketplace.InsurancePlanDao.ORDER_BY_DISPLAY_NAME;
import static com.scnsoft.eldermark.shared.utils.PaginationUtils.buildPageable;

@Service
@Transactional
public class InsurancePlanService extends BaseDisplayableNamedKeyEntityService<InsurancePlanInfoDto, InsurancePlan> {

    @Autowired
    private InsurancePlanDao insurancePlanDao;

    public List<InsurancePlanInfoDto> listAllInsurancePlans (Long inNetworkInsuranceId) {
        List<InsurancePlan> insurancePlanList = insurancePlanDao.getAllByInNetworkInsuranceIdOrderByDisplayNameAsc(inNetworkInsuranceId);
        return transform(insurancePlanList);
    }

    public Page<InsurancePlanInfoDto> listInsurancePlans(Long inNetworkInsuranceId, String searchText, Integer pageSize, Integer page) {
        String likeFormatSearchStr;
        Page<InsurancePlan> insurancePlanList;
        final Pageable pageable = buildPageable(pageSize, page);
        if (StringUtils.isNotBlank(searchText)) {
            likeFormatSearchStr = String.format("%%%s%%", searchText);
            if (inNetworkInsuranceId != null) {
                insurancePlanList = insurancePlanDao.getAllByInNetworkInsuranceIdAndDisplayNameLikeOrderByDisplayNameAsc(inNetworkInsuranceId, likeFormatSearchStr, pageable);
            } else {
                insurancePlanList = insurancePlanDao.getAllByDisplayNameLikeOrderByDisplayNameAsc(likeFormatSearchStr, pageable);
            }
        } else {
            if (inNetworkInsuranceId != null) {
                insurancePlanList = insurancePlanDao.getAllByInNetworkInsuranceIdOrderByDisplayNameAsc(inNetworkInsuranceId, pageable);
            } else {
                Pageable sortingPageable = buildPageable(pageSize, page, new Sort(ORDER_BY_DISPLAY_NAME));
                insurancePlanList = insurancePlanDao.findAll(sortingPageable);
            }
        }
        return new PageImpl<>(transform(insurancePlanList.getContent()), pageable, insurancePlanList.getTotalElements());
    }

    public Integer getPageNumber(Long insurancePlanId, Long inNetworkInsuranceId, Integer pageSize) {
        Integer result = null;
        InsurancePlan searchInsurancePlan = insurancePlanDao.getOne(insurancePlanId);
        if (searchInsurancePlan == null) {
            return null;
        }
        List<InsurancePlan> insurancePlans;
        if (inNetworkInsuranceId != null) {
            insurancePlans = insurancePlanDao.getAllByInNetworkInsuranceIdOrderByDisplayNameAsc(inNetworkInsuranceId);
        } else {
            insurancePlans = insurancePlanDao.findAll(new Sort(ORDER_BY_DISPLAY_NAME));
        }
        if (!CollectionUtils.isEmpty(insurancePlans)) {
            result = findNumber(0, pageSize, insurancePlans.size()-1, searchInsurancePlan.getDisplayName(), insurancePlans);
        }
        return result != null ? result/pageSize : null;
    }

    @Override
    protected InsurancePlanInfoDto createNewDto() {
        return new InsurancePlanInfoDto();
    }

    @Override
    protected InsurancePlanInfoDto transformListItem(InsurancePlan source) {
        final InsurancePlanInfoDto dto = super.transformListItem(source);
        dto.setInsuranceName(source.getInNetworkInsurance().getDisplayName());
        return dto;
    }

}
