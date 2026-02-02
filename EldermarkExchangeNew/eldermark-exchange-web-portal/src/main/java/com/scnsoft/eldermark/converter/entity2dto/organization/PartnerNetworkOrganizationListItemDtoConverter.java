package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.PartnerNetworkOrganization;
import com.scnsoft.eldermark.dto.PartnerNetworkOrganizationListItemDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import com.scnsoft.eldermark.entity.basic.DisplayableNamedEntity;
import com.scnsoft.eldermark.util.StreamUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class PartnerNetworkOrganizationListItemDtoConverter implements Converter<PartnerNetworkOrganization, PartnerNetworkOrganizationListItemDto> {

    @Override
    public PartnerNetworkOrganizationListItemDto convert(PartnerNetworkOrganization source) {
        var target = new PartnerNetworkOrganizationListItemDto();
        target.setId(source.getId());
        target.setTitle(source.getDisplayName());
        target.setCommunities(convertCommunities(source.getCommunities()));
        return target;
    }

    private Set<IdentifiedTitledEntityDto> convertCommunities(List<DisplayableNamedEntity> communities) {
        if (communities == null) {
            return null;
        }
        return communities.stream()
                .map(c -> new IdentifiedTitledEntityDto(c.getId(), c.getDisplayName()))
                .collect(StreamUtils.toIdsComparingSet());
    }
}
