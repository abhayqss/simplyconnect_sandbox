package com.scnsoft.eldermark.service.impl;

import com.scnsoft.eldermark.dao.healthdata.EncounterDao;
import com.scnsoft.eldermark.entity.Encounter;
import com.scnsoft.eldermark.entity.ProcedureActivity;
import com.scnsoft.eldermark.service.BasePhrService;
import com.scnsoft.eldermark.service.EncounterService;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.web.entity.EncounterInfoDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;


/**
 * @author ggavrysh
 * service for working with encounters section in mobile backend
 */
@Service
@Transactional(readOnly = true)
public class EncounterServiceImpl extends BasePhrService implements EncounterService {

    @Autowired
    private EncounterDao encounterDao;

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    @Autowired
    private Converter<Encounter, EncounterInfoDto> encounterTransformer;

    @Autowired
    private MPIService mpiService;

    @Override
    public Page<Encounter> getEncountersForResidents(Collection<Long> residentIds, Pageable pageable) {

        Sort.Order ORDER_BY_EFFECTIVE_DATE_DESC = new Sort.Order(Sort.Direction.DESC, "effectiveTime");
        final Pageable pageableWithSort = PaginationUtils.setSort(pageable, ORDER_BY_EFFECTIVE_DATE_DESC);
        final Page<Encounter> page = encounterDao.listResidentEncounters(residentIds, pageableWithSort);
        return page;
    }

    @Override
    public Encounter getEncounter(Long encounterId) {
        return encounterDao.getOne(encounterId);
    }

    public Converter<Encounter, EncounterInfoDto> getEncounterTransformer() {
        return encounterTransformer;
    }

    public void setEncounterTransformer(Converter<Encounter, EncounterInfoDto> encounterTransformer) {
        this.encounterTransformer = encounterTransformer;
    }
}
