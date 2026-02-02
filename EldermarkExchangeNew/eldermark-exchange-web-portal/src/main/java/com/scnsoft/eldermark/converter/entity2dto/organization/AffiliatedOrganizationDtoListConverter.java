package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.AffiliatedOrganizationDto;
import com.scnsoft.eldermark.entity.AffiliatedOrganization;
import com.scnsoft.eldermark.service.CommunityService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AffiliatedOrganizationDtoListConverter implements Converter<List<AffiliatedOrganization>, List<AffiliatedOrganizationDto>> {

    @Autowired
    private CommunityService communityService;

    @Override
    public List<AffiliatedOrganizationDto> convert(List<AffiliatedOrganization> source) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        return source.stream()
                .flatMap(s -> {
                    var primaryCommunityIds = Optional.ofNullable(s.getPrimaryCommunityId())
                            .map(Collections::singletonList)
                            .orElseGet(() -> communityService.findCommunityIdsByOrgId(s.getPrimaryOrganizationId()));

                    var affiliatedCommunityIds = Optional.ofNullable(s.getAffiliatedCommunityId())
                            .map(Collections::singletonList)
                            .orElseGet(() -> communityService.findCommunityIdsByOrgId(s.getAffiliatedOrganizationId()));

                    return primaryCommunityIds.stream()
                            .flatMap(prId -> affiliatedCommunityIds.stream().map(affCId -> convert(s, prId, affCId)));
                })
                .collect(Collectors.toList());
    }

    private AffiliatedOrganizationDto convert(AffiliatedOrganization source, Long primaryCommunityId, Long affCommunityId) {
        var target = new AffiliatedOrganizationDto();
        target.setPrimaryOrganizationId(source.getPrimaryOrganizationId());
        target.setPrimaryCommunityId(primaryCommunityId);
        target.setAffiliatedOrganizationId(source.getAffiliatedOrganizationId());
        target.setAffiliatedCommunityId(affCommunityId);
        return target;
    }

}
