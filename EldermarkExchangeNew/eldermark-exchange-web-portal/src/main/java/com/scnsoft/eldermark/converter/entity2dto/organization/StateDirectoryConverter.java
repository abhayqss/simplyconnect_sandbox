package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.DirectoryStateListItemDto;
import com.scnsoft.eldermark.entity.State;
import org.springframework.stereotype.Component;

@Component
public class StateDirectoryConverter implements ListAndItemConverter<State, DirectoryStateListItemDto<Long>> {

    @Override
    public DirectoryStateListItemDto<Long> convert(State source) {
        return new DirectoryStateListItemDto<>(source.getId(), source.getName() + " (" + source.getAbbr() + ")", source.getHieConsentPolicy(), source.getHieConsentPolicy().getDisplayName());
    }

}
