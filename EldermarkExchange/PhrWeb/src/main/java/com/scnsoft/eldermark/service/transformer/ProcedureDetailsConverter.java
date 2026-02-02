package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.ProcedureActivity;
import com.scnsoft.eldermark.service.BasePhrService;
import com.scnsoft.eldermark.service.DataSourceService;
import com.scnsoft.eldermark.web.entity.ProcedureDetailsDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProcedureDetailsConverter extends BasePhrService implements Converter<ProcedureActivity, ProcedureDetailsDto> {

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    @Override
    public ProcedureDetailsDto convert(ProcedureActivity procedureActivity) {
        final ProcedureDetailsDto result = new ProcedureDetailsDto();
        result.setId(procedureActivity.getId());
        result.setName(procedureActivity.getProcedureTypeText());
        result.setIdentifiedDate(procedureActivity.getProcedureStarted());
        result.setStoppedDate(procedureActivity.getProcedureStopped());
        result.setDataSource(DataSourceService.transform(procedureActivity.getDatabase(), procedureActivity.getProcedure() != null ? procedureActivity.getProcedure().getResident().getId() : null));
        return result;
    }
}