package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.ProcedureActivity;
import com.scnsoft.eldermark.web.entity.ProcedureListItemDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProcedureListConverter implements Converter<ProcedureActivity, ProcedureListItemDto> {

    @Override
    public ProcedureListItemDto convert(ProcedureActivity procedureActivity) {
        final ProcedureListItemDto listItemDto = new ProcedureListItemDto();
        listItemDto.setId(procedureActivity.getId());
        listItemDto.setName(procedureActivity.getProcedureTypeText());
        listItemDto.setIdentifiedDate(procedureActivity.getProcedureStarted());
        return listItemDto;
    }
}