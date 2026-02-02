package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.ServiceCategoryAwareIdentifiedTitledDto;
import com.scnsoft.eldermark.entity.marketplace.ServiceType;
import com.scnsoft.eldermark.service.internal.EntityListUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseServiceTypeConverter<S extends ServiceType, T extends ServiceCategoryAwareIdentifiedTitledDto> implements Converter<List<S>, List<T>> {

    private static final Comparator<ServiceType> COMPARATOR = Comparator.comparing(ServiceType::getServiceCategoryId)
            .thenComparing(EntityListUtils.displayNameComparator("Other"));

    @Override
    public List<T> convert(List<S> source) {
        var itemConverter = getItemConverter();
        return Stream.ofNullable(source)
                .flatMap(Collection::stream)
                .sorted(COMPARATOR)
                .map(itemConverter::convert)
                .collect(Collectors.toList());
    }

    protected abstract Converter<S, T> getItemConverter();
}
