package com.scnsoft.eldermark.service.impl;

import com.scnsoft.eldermark.dao.healthdata.AdvanceDirectiveDao;
import com.scnsoft.eldermark.dao.healthdata.EncounterDao;
import com.scnsoft.eldermark.entity.AdvanceDirective;
import com.scnsoft.eldermark.entity.Encounter;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.service.AdvanceDirectiveService;
import com.scnsoft.eldermark.service.validation.AccessibleResidentsProvider;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.web.entity.AdvanceDirectiveInfoDto;
import com.scnsoft.eldermark.web.entity.EncounterInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdvanceDirectiveServiceImpl implements AdvanceDirectiveService {

    @Autowired
    private AdvanceDirectiveDao advanceDirectiveDao;

    @Autowired
    private Converter<AdvanceDirective, AdvanceDirectiveInfoDto> advanceDirectiveTransformer;


    @Override
    public Page<AdvanceDirective> getAdvanceDirectivesForResidents(final Collection<Long> residentIds, final  Pageable pageable) {
        Sort.Order ORDER_BY_TIME_LOW_DESC = new Sort.Order(Sort.Direction.DESC, "timeLow");
        Sort.Order ORDER_BY_TIME_HUGH_DESC = new Sort.Order(Sort.Direction.DESC, "timeHigh");
        final Pageable pageableWithSort = PaginationUtils.setSort(pageable, ORDER_BY_TIME_LOW_DESC, ORDER_BY_TIME_HUGH_DESC);
        final Page<AdvanceDirective> page = getAdvanceDirectiveDao().getDirectivesByResidentIds(residentIds, pageableWithSort);
        return page;
    }

    @Override
    public AdvanceDirective getAdvanceDirective(Long advanceDirectiveId) {
        return advanceDirectiveDao.getOne(advanceDirectiveId);
    }


    public AdvanceDirectiveDao getAdvanceDirectiveDao() {
        return advanceDirectiveDao;
    }

    public void setAdvanceDirectiveDao(AdvanceDirectiveDao advanceDirectiveDao) {
        this.advanceDirectiveDao = advanceDirectiveDao;
    }

    public Converter<AdvanceDirective, AdvanceDirectiveInfoDto> getAdvanceDirectiveTransformer() {
        return advanceDirectiveTransformer;
    }

    public void setAdvanceDirectiveTransformer(Converter<AdvanceDirective, AdvanceDirectiveInfoDto> advanceDirectiveTransformer) {
        this.advanceDirectiveTransformer = advanceDirectiveTransformer;
    }
}
