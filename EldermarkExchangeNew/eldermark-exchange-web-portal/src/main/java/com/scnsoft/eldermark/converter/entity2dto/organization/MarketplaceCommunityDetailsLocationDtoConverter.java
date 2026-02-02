package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.MarketplaceCommunityLocationDetailsDto;
import com.scnsoft.eldermark.entity.Marketplace;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class MarketplaceCommunityDetailsLocationDtoConverter extends BaseMarketplaceCommunityConverter
        implements Converter<Marketplace, MarketplaceCommunityLocationDetailsDto> {

    @Override
    public MarketplaceCommunityLocationDetailsDto convert(Marketplace source) {
        var target = new MarketplaceCommunityLocationDetailsDto();
        fillId(source, target);
        fillLocationData(source, target);
        fillBaseDetailsData(source, target);
        return target;
    }

}
