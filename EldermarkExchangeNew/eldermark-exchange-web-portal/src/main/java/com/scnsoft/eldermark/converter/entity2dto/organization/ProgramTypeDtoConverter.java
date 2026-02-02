package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.serviceplan.ProgramTypeDto;
import com.scnsoft.eldermark.entity.serviceplan.ProgramType;
import org.springframework.stereotype.Component;

@Component
public class ProgramTypeDtoConverter implements ListAndItemConverter<ProgramType, ProgramTypeDto> {
    @Override
    public ProgramTypeDto convert(ProgramType source) {
        ProgramTypeDto target = new ProgramTypeDto();
        target.setId(source.getId());
        target.setDomainId(source.getDomain().getDomainNumber());
        target.setName(source.getCode());
        target.setTitle(source.getDisplayName());
        return target;
    }
}
