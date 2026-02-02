package com.scnsoft.eldermark.mapper.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.Zone;
import com.scnsoft.eldermark.mapper.ZoneDto;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;

public class NotifyZoneMapper extends GenericMapper<Zone, ZoneDto> {

    @Override
    protected Class<Zone> getEntityClass() {
        return Zone.class;
    }

    @Override
    protected Class<ZoneDto> getDtoClass() {
        return ZoneDto.class;
    }

}
