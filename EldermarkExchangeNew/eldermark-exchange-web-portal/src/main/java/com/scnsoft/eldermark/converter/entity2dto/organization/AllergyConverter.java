package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.AllergyListItemDto;
import com.scnsoft.eldermark.entity.document.ccd.ClientAllergy;
import com.scnsoft.eldermark.util.DateTimeUtils;

public interface AllergyConverter {

    default void fill(ClientAllergy source, AllergyListItemDto target) {
        target.setId(source.getId());
        target.setSubstance(source.getProductText());
        target.setReaction(source.getCombinedReactionTexts());
        target.setIdentifiedDate(DateTimeUtils.toEpochMilli(source.getEffectiveTimeLow()));
    }
}
