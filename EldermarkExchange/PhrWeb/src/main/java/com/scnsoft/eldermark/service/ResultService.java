package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.ResultObservationDao;
import com.scnsoft.eldermark.entity.ResultObservation;
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
public class ResultService extends BasePhrService {

    @Autowired
    private ResultObservationDao resultObservationDao;

    public Page<ResultObservation> getResults(final Collection<Long> residentIds, final Pageable pageable) {
        Sort.Order ORDER_BY_EFFECTIVE_DATE_DESC = new Sort.Order(Sort.Direction.DESC, "effectiveTime");
        Sort.Order ORDER_BY_TYPE = new Sort.Order(Sort.Direction.ASC, "resultTypeCode.displayName");
        final Pageable pageableWithSort = PaginationUtils.setSort(pageable, ORDER_BY_EFFECTIVE_DATE_DESC, ORDER_BY_TYPE);
        final Page<ResultObservation> page = resultObservationDao.listResidentResultsWithoutDuplicates(residentIds, pageableWithSort);
        return page;
    }

    public ResultObservation getResult(final Long resultObservationId) {
        final ResultObservation resultObservation = resultObservationDao.getOne(resultObservationId);
        return resultObservation;
    }
}
