package com.scnsoft.eldermark.mapper.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.Handset;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.shared.palatiumcare.HandsetDto;

public class NotifyHandsetMapper extends GenericMapper<Handset, HandsetDto> {

    @Override
    protected Class<Handset> getEntityClass() {
        return Handset.class;
    }

    @Override
    protected Class<HandsetDto> getDtoClass() {
        return HandsetDto.class;
    }
}
