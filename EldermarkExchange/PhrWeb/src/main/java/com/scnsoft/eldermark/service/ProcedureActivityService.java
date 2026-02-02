package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.ProcedureActivityDao;
import com.scnsoft.eldermark.entity.ProcedureActivity;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * @author sparuchnik
 */
@Service
@Transactional(readOnly = true)
public class ProcedureActivityService extends BasePhrService {

    @Autowired
    private ProcedureActivityDao procedureActivityDao;

    public Page<ProcedureActivity> getProcedureActivities(Collection<Long> residentIds, Pageable pageable) {
        final Sort.Order sortOrder = new Sort.Order(Sort.Direction.DESC,"procedureStarted");
        final Page<ProcedureActivity> activities = procedureActivityDao.listResidentProcedureActivitiesWithoutDuplicates(residentIds,
                PaginationUtils.setSort(pageable, sortOrder));
        return activities;
    }

    public ProcedureActivity getProcedureActivity(Long procedureActivityId) {
        return procedureActivityDao.getOne(procedureActivityId);
    }
}
