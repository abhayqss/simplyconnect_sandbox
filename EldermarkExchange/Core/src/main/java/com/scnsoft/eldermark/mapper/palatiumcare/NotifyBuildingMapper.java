package com.scnsoft.eldermark.mapper.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.Building;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.shared.palatiumcare.building.NotifyBuildingDto;

public class NotifyBuildingMapper extends GenericMapper<Building, NotifyBuildingDto> {

    @Override
    protected Class<Building> getEntityClass() {
        return Building.class;
    }

    @Override
    protected Class<NotifyBuildingDto> getDtoClass() {
        return NotifyBuildingDto.class;
    }

}
