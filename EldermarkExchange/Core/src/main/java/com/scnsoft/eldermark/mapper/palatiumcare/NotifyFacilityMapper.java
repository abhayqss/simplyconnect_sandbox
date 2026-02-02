package com.scnsoft.eldermark.mapper.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.Facility;
import com.scnsoft.eldermark.shared.palatiumcare.FacilityDto;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;

public class NotifyFacilityMapper extends GenericMapper<Facility, FacilityDto> {
    @Override
    protected Class<Facility> getEntityClass() {
        return Facility.class;
    }

    @Override
    protected Class<FacilityDto> getDtoClass() {
        return FacilityDto.class;
    }
}
