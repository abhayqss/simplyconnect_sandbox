package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.serviceplan.ProgramSubTypeDto;
import com.scnsoft.eldermark.entity.serviceplan.ProgramSubType;
import org.springframework.stereotype.Component;

@Component
public class ProgramSubTypeDtoConverter implements ListAndItemConverter<ProgramSubType, ProgramSubTypeDto> {
    @Override
    public ProgramSubTypeDto convert(ProgramSubType source) {
        ProgramSubTypeDto target = new ProgramSubTypeDto();
        target.setId(source.getId());
        target.setName(source.getCode());
        target.setTitle(source.getDisplayName());
        target.setProgramTypeId(source.getProgramType().getId());
        if (source.getzCode() != null) {
            target.setZcode(source.getzCode().getCode());
            target.setZcodeDesc(source.getzCode().getDescription());
        }
        return target;
    }
}
