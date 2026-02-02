package com.scnsoft.eldermark.mapper.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.Location;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.shared.palatiumcare.location.NotifyLocationDto;


public class NotifyLocationMapper extends GenericMapper<Location, NotifyLocationDto> {

    @Override
    protected Class<Location> getEntityClass() {
        return Location.class;
    }

    @Override
    protected Class<NotifyLocationDto> getDtoClass() {
        return NotifyLocationDto.class;
    }

}
