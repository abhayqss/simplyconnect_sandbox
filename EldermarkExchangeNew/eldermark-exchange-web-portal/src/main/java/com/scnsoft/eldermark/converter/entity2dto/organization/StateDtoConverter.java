package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.StateDto;
import com.scnsoft.eldermark.entity.State;
import org.springframework.stereotype.Component;

@Component
public class StateDtoConverter implements ListAndItemConverter<State, StateDto> {

    @Override
    public StateDto convert(State state) {
        StateDto stateDto = new StateDto();
        stateDto.setId(state.getId());
        stateDto.setName(state.getName());
        stateDto.setAbbr(state.getAbbr());
        return stateDto;
    }

}
