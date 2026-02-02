package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.AllergyDto;
import com.scnsoft.eldermark.entity.document.ccd.ClientAllergy;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AllergyDtoConverter implements Converter<ClientAllergy, AllergyDto>, AllergyConverter {

    @Override
    public AllergyDto convert(ClientAllergy source) {
        AllergyDto target = new AllergyDto();
        fill(source, target);
        target.setType(source.getTypeText());
        target.setSeverity(source.getSeverityText());
        target.setStatusName(source.getStatus().name());
        target.setStatusTitle(source.getStatus().getDisplayName());
        target.setStoppedDate(DateTimeUtils.toEpochMilli(source.getEffectiveTimeHigh()));
        target.setDataSource(getDataSource(source));
        return target;
    }

    private String getDataSource(ClientAllergy source) {
        return source.getClient().getOrganization().getName() + ", " +
                source.getClient().getCommunity().getName();
    }
}