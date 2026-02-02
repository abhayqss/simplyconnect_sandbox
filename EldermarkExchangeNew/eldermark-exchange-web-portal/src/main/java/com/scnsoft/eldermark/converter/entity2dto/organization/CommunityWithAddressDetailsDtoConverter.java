package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.CommunityWithAddressDetailsDto;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.service.StateService;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CommunityWithAddressDetailsDtoConverter implements Converter<Community, CommunityWithAddressDetailsDto> {

    @Autowired
    private CommunityDtoConverter communityDtoConverter;

    @Autowired
    private StateService stateService;

    @Override
    public CommunityWithAddressDetailsDto convert(Community source) {
        var target = new CommunityWithAddressDetailsDto();
        communityDtoConverter.fillData(source, target);
        stateService.findById(target.getStateId())
            .ifPresent(state -> {
                target.setStateAbbr(state.getAbbr());
                target.setStateName(state.getName());
            });
        return target;
    }
}
