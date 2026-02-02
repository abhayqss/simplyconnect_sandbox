package com.scnsoft.eldermark.web.controller.helper;

import com.scnsoft.eldermark.shared.marketplace.BasicMarketplaceInfoDto;
import com.scnsoft.eldermark.shared.marketplace.MarketplaceFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class MarketPlaceControllerHelper {
    //todo add java doc
    public void clearFilter(final MarketplaceFilterDto marketplaceFilter) {
        final List<Long> primaryFocusIds = marketplaceFilter.getPrimaryFocusIds();
        if (primaryFocusIds != null) {
            primaryFocusIds.remove(0L);
            primaryFocusIds.remove(null);
        }
        final List<Long> communityTypeIds = marketplaceFilter.getCommunityTypeIds();
        if (communityTypeIds != null) {
            communityTypeIds.remove(null);
            communityTypeIds.remove(0L);
        }
        final List<Long> serviceIds = marketplaceFilter.getServiceIds();
        if (serviceIds != null) {
            serviceIds.remove(null);
            serviceIds.remove(0L);
        }
    }

    public void prepareMarketPlaceList(List<BasicMarketplaceInfoDto> dtos) {
        for (BasicMarketplaceInfoDto dto : dtos) {
            if (dto.getLocation() == null || dto.getLocation().getDistanceInMiles() == null) {
                dto.getLocation().setDisplayDistanceMiles("");
                continue;
            }
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
            dto.getLocation().setDisplayDistanceMiles(df.format(dto.getLocation().getDistanceInMiles()) + " mi.");
        }
    }

    /**
     *
     * @param pageDto
     * @param pageable
     * @return
     */
    public Page<BasicMarketplaceInfoDto> sortByDistance(Page<BasicMarketplaceInfoDto> pageDto, Pageable pageable) {
        final List<BasicMarketplaceInfoDto> dtos = new ArrayList<>(pageDto.getContent());
        Collections.sort(dtos, new Comparator<BasicMarketplaceInfoDto>() {
            @Override
            public int compare(BasicMarketplaceInfoDto o1, BasicMarketplaceInfoDto o2) {
                final boolean firstMarketDistanceNull = o1.getLocation() == null
                        || o1.getLocation().getDistanceInMiles() == null;
                final boolean secondMarketDistanceNull = o2.getLocation() == null
                        || o2.getLocation().getDistanceInMiles() == null;
                if (firstMarketDistanceNull && secondMarketDistanceNull) {
                    return 0;
                }
                if (!firstMarketDistanceNull && secondMarketDistanceNull) {
                    return -1;
                }
                if (firstMarketDistanceNull && !secondMarketDistanceNull) {
                    return 1;
                }
                return o1.getLocation().getDistanceInMiles().compareTo(o2.getLocation().getDistanceInMiles());
            }
        });
        return new PageImpl<BasicMarketplaceInfoDto>(dtos, pageable, dtos.size());
    }
}
