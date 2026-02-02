package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.OrganizationDto;
import com.scnsoft.eldermark.entity.SystemSetup;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class SystemSetupEntityConverter implements Converter<OrganizationDto, SystemSetup> {

    @Override
    public SystemSetup convert(OrganizationDto source) {
        var target = new SystemSetup();
        target.setLoginCompanyId(source.getCompanyId());
        return target;
    }
}
