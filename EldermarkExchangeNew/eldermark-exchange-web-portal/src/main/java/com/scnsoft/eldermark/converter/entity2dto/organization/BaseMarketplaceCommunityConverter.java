package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.google.maps.model.LatLng;
import com.scnsoft.eldermark.dto.BaseMarketplaceCommunityDetailsDto;
import com.scnsoft.eldermark.dto.BaseMarketplaceCommunityLocationDto;
import com.scnsoft.eldermark.dto.KeyValueDto;
import com.scnsoft.eldermark.dto.LocationWithDistanceDto;
import com.scnsoft.eldermark.entity.Marketplace;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.basic.DisplayableNamedKeyEntity;
import com.scnsoft.eldermark.entity.community.CommunityAddress;
import com.scnsoft.eldermark.service.MapsService;
import com.scnsoft.eldermark.service.MarketplaceRatingService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.service.MarketplaceRatingServiceImpl.RATING_SERVICE_TYPE_KEY;

public abstract class BaseMarketplaceCommunityConverter {

    @Autowired
    private MapsService mapsService;

    @Autowired
    private MarketplaceRatingService marketplaceRatingService;

    @Autowired
    private Converter<Address, String> displayAddressConverter;

    protected void fillId(Marketplace source, BaseMarketplaceCommunityLocationDto target) {
        if (source.getCommunity() != null) {
            target.setCommunityId(source.getCommunityId());
        }
    }

    protected void fillLocationData(Marketplace source, BaseMarketplaceCommunityLocationDto target) {
        if (source.getCommunity() != null) {
            if (CollectionUtils.isNotEmpty(source.getCommunity().getAddresses())) {
                final CommunityAddress organizationAddress = source.getCommunity().getAddresses().get(0);
                final LocationWithDistanceDto locationDto = getLocationDto(source.getUserLongitude(), source.getUserLatitude(),
                        organizationAddress.getLongitude(), organizationAddress.getLatitude());
                target.setLocation(locationDto);
            }
        }
    }

    protected void fillBaseDetailsData(Marketplace source, BaseMarketplaceCommunityDetailsDto target) {
        if (source.getCommunity() != null) {
            target.setCommunityName(source.getCommunity().getName());
            if (CollectionUtils.isNotEmpty(source.getCommunity().getAddresses())) {
                var organizationAddress = source.getCommunity().getAddresses().get(0);
                target.setAddress(displayAddressConverter.convert(organizationAddress));
            }
            target.setPhone(source.getCommunity().getPhone());
        }
        target.setOrganizationId(source.getOrganizationId());
        target.setOrganizationName(source.getOrganization() != null ? source.getOrganization().getName() : null);
        target.setServiceCategories(getDisplayNames(source.getServiceCategories()));
        target.setServices(getDisplayNames(source.getServiceTypes()));
        if (CollectionUtils.isNotEmpty(source.getServiceTypes())) {
            if (source.getServiceTypes().stream().anyMatch(type -> RATING_SERVICE_TYPE_KEY.equals(type.getKey()))) {
                target.setRating(marketplaceRatingService.getRatingByName(source.getCommunity() != null ? source.getCommunity().getName() : source.getOrganization().getName())
                        .orElse(0));
            }
        }
    }

    protected LocationWithDistanceDto getLocationDto(Double userLongitude, Double userLatitude, Double longitude, Double latitude) {
        final LocationWithDistanceDto locationDto = new LocationWithDistanceDto();
        locationDto.setLatitude(latitude);
        locationDto.setLongitude(longitude);
        if (userLongitude == null || userLatitude == null || longitude == null || latitude == null) {
            return locationDto;
        }
        final LatLng userLocation = new LatLng(userLatitude, userLongitude);
        final LatLng pointLocation = new LatLng(latitude, longitude);
        final Double distanceInMiles = mapsService.calculateDistanceMiles(pointLocation, userLocation);
        locationDto.setDistanceInMiles(distanceInMiles);
        DecimalFormat df = new DecimalFormat("#.##");
        locationDto.setDisplayDistanceMiles(df.format(distanceInMiles) + " mi");
        return locationDto;
    }

    protected <E extends DisplayableNamedKeyEntity> List<KeyValueDto> getDisplayNames(List<E> entities) {
        return CollectionUtils.emptyIfNull(entities).stream()
                .map(x -> new KeyValueDto<>(x.getId(), x.getDisplayName())).collect(Collectors.toList());
    }

}
