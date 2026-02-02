package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.FeaturedServiceProviderFilter;
import com.scnsoft.eldermark.dto.FeaturedServiceProviderDto;

import java.util.List;

public interface FeaturedServiceProviderFacade {
    List<FeaturedServiceProviderDto> fetchServiceProviders(FeaturedServiceProviderFilter filter);
}