package com.scnsoft.eldermark.mapper.palatiumcare.alert;

import com.scnsoft.eldermark.shared.palatiumcare.AlertDto;
import com.scnsoft.eldermark.entity.palatiumcare.Alert;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;

public class AlertMapper extends GenericMapper<Alert, AlertDto> {


    {
        getModelMapper().addMappings(new AlertToAlertDtoMap());
    }


    @Override
    protected Class<Alert> getEntityClass() {
        return Alert.class;
    }

    @Override
    protected Class<AlertDto> getDtoClass() {
        return AlertDto.class;
    }
}
