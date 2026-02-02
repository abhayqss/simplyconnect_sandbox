package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.ServiceCategoryAwareIdentifiedTitledDto;
import com.scnsoft.eldermark.entity.marketplace.ServiceType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ServiceTypeConverter extends BaseServiceTypeConverter<ServiceType, ServiceCategoryAwareIdentifiedTitledDto> {

    @Override
    protected Converter<ServiceType, ServiceCategoryAwareIdentifiedTitledDto> getItemConverter() {
        return source -> new ServiceCategoryAwareIdentifiedTitledDto(
                source.getId(),
                source.getDisplayName(),
                source.getServiceCategoryId(),
                source.getServiceCategory().getDisplayName()
        );
    }
}
