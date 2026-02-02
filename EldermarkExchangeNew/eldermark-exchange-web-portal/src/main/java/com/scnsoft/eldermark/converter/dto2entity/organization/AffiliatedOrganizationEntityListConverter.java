package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.OrganizationDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import com.scnsoft.eldermark.entity.AffiliatedOrganization;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AffiliatedOrganizationEntityListConverter implements Converter<OrganizationDto, List<AffiliatedOrganization>> {

    @Override
    public List<AffiliatedOrganization> convert(OrganizationDto source) {
        if (source == null || CollectionUtils.isEmpty(source.getAffiliatedRelationships())) {
            return null;
        }

        var target = new ArrayList<AffiliatedOrganization>();

        for (var itemDto : source.getAffiliatedRelationships()) {
            var communityIds = Optional.ofNullable(itemDto.getPrimaryCommunities()).orElse(new ArrayList<>()).stream().map(IdentifiedTitledEntityDto::getId).collect(Collectors.toList());
            var affCommunityIds = Optional.ofNullable(itemDto.getAffiliatedCommunities()).orElse(new ArrayList<>()).stream().map(IdentifiedTitledEntityDto::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(communityIds)) {
                target.addAll(createForAllAffCommunities(source.getId(), null, itemDto.getAffiliatedOrganization().getId(),affCommunityIds));
            } else {
                target.addAll(communityIds.stream()
                        .map(id -> createForAllAffCommunities(source.getId(), id, itemDto.getAffiliatedOrganization().getId(), affCommunityIds))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()));
            }
        }

        return target;
    }

    private List<AffiliatedOrganization> createForAllAffCommunities(Long primaryOrganizationId, Long primaryCommunityId, Long affOrganizationId, List<Long> affCommunityIds) {
        if (CollectionUtils.isEmpty(affCommunityIds)) {
            return List.of(createForAffCommunity(primaryOrganizationId, primaryCommunityId, affOrganizationId, null));
        }
        return affCommunityIds.stream()
                .map(id -> createForAffCommunity(primaryOrganizationId, primaryCommunityId, affOrganizationId, id))
                .collect(Collectors.toList());
    }

    private AffiliatedOrganization createForAffCommunity(Long primaryOrganizationId, Long primaryCommunityId, Long affOrganizationId, Long affCommunityId) {
        var target = new AffiliatedOrganization();
        target.setPrimaryOrganizationId(primaryOrganizationId);
        target.setPrimaryCommunityId(primaryCommunityId);
        target.setAffiliatedOrganizationId(affOrganizationId);
        target.setAffiliatedCommunityId(affCommunityId);
        return target;
    }
}
