package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.AllergyListItemDto;
import com.scnsoft.eldermark.entity.document.ccd.ClientAllergy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AllergyListItemDtoConverter implements Converter<ClientAllergy, AllergyListItemDto>, AllergyConverter {

    @Override
    public AllergyListItemDto convert(ClientAllergy source) {
        AllergyListItemDto target = new AllergyListItemDto();
        fill(source, target);
        return target;
    }
}
