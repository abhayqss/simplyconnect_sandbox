package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.RoleDto;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CareTeamRoleDtoConverter implements Converter<CareTeamRole, RoleDto> {

    @Override
    public RoleDto convert(CareTeamRole careTeamRole) {
        return new RoleDto(careTeamRole.getId(), careTeamRole.getCode().getCode(), careTeamRole.getName());
    }

}
