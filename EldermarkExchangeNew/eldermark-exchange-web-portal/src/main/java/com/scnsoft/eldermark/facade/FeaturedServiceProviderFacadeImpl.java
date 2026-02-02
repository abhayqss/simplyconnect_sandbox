package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.FeaturedServiceProviderFilter;
import com.scnsoft.eldermark.dto.FeaturedServiceProviderDto;
import com.scnsoft.eldermark.entity.FeaturedServiceProvider;
import com.scnsoft.eldermark.service.FeaturedServiceProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeaturedServiceProviderFacadeImpl implements FeaturedServiceProviderFacade {

    @Autowired
    private Converter<FeaturedServiceProvider, FeaturedServiceProviderDto> featuredServiceProviderDtoConverter;

    @Autowired
    private FeaturedServiceProviderService featuredServiceProviderService;

    @Override
    public List<FeaturedServiceProviderDto> fetchServiceProviders(FeaturedServiceProviderFilter filter) {
        return featuredServiceProviderService.fetchServiceProvidersByCommunityId(filter.getCommunityId())
                .stream()
                .map(featuredServiceProviderDtoConverter::convert)
                .filter(Objects::nonNull)
                .filter(FeaturedServiceProviderDto::getConfirmVisibility)
                .collect(Collectors.toList());
    }
}