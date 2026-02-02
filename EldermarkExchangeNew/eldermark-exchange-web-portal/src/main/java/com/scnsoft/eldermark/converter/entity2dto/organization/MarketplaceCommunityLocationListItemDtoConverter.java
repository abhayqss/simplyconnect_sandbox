package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.MarketplaceCommunityLocationListItemDto;
import com.scnsoft.eldermark.entity.Marketplace;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class MarketplaceCommunityLocationListItemDtoConverter extends BaseMarketplaceCommunityConverter
        implements Converter<Marketplace, MarketplaceCommunityLocationListItemDto> {

    @Override
    public MarketplaceCommunityLocationListItemDto convert(Marketplace source) {
        var target = new MarketplaceCommunityLocationListItemDto();
        fillId(source, target);
        fillLocationData(source, target);
        return target;
    }
}
