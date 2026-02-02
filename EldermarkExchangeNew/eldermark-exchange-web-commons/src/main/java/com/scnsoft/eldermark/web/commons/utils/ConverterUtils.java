package com.scnsoft.eldermark.web.commons.utils;

import com.scnsoft.eldermark.entity.basic.DisplayableNamedKeyEntity;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public final class ConverterUtils {

    public static <E extends DisplayableNamedKeyEntity> List<IdentifiedTitledEntityDto> getDisplayNames(List<E> entities) {
        return CollectionUtils.emptyIfNull(entities).stream()
                .map(x -> new IdentifiedTitledEntityDto(x.getId(), x.getDisplayName())).collect(Collectors.toList());
    }
}
