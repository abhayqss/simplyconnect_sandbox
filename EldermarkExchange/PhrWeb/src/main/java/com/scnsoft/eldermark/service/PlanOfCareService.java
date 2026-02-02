package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.PlanOfCareActivityDao;
import com.scnsoft.eldermark.entity.PlanOfCareActivity;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional(readOnly = true)
public class PlanOfCareService extends BasePhrService {

    @Autowired
    private PlanOfCareActivityDao planOfCareActivityDao;

    public Page<PlanOfCareActivity> getPlansOfCare(Collection<Long> authorityIds, Pageable pageable) {
        final Sort.Order sortOrder = new Sort.Order(Sort.Direction.DESC,"effectiveTime");
        return planOfCareActivityDao.listResidentPlanOfCareActivityWithoutDuplicates(authorityIds, PaginationUtils.setSort(pageable, sortOrder));
    }

    public PlanOfCareActivity getPlanOfCareActivity(Long activityId) {
        return planOfCareActivityDao.getOne(activityId);
    }
}
