package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.projection.NameAware;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dto.AffiliatedRelationshipItemDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import com.scnsoft.eldermark.entity.AffiliatedOrganization;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class AffiliatedRelationshipDtoListConverter implements Converter<List<AffiliatedOrganization>, List<AffiliatedRelationshipItemDto>> {

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private CommunityDao communityDao;

    @Override
    public List<AffiliatedRelationshipItemDto> convert(List<AffiliatedOrganization> source) {
        if (CollectionUtils.isEmpty(source)) {
            return null;
        }

        var affOrganizationsByIds = source.stream()
                .collect(Collectors.groupingBy(AffiliatedOrganization::getAffiliatedOrganizationId));

        return affOrganizationsByIds.entrySet().stream()
                .map(v -> {
                    var primaryCommunityIds = v.getValue().stream()
                            .map(AffiliatedOrganization::getPrimaryCommunityId)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList());
                    var affCommunityIds = v.getValue().stream()
                            .map(AffiliatedOrganization::getAffiliatedCommunityId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    var target = new AffiliatedRelationshipItemDto();
                    if (CollectionUtils.isNotEmpty(primaryCommunityIds)) {
                        target.setPrimaryCommunities(communityDao.findByIdIn(primaryCommunityIds, IdNameAware.class).stream()
                                .map(idNameAware -> new IdentifiedTitledEntityDto(idNameAware.getId(), idNameAware.getName())).collect(Collectors.toList()));
                    }
                    target.setAffiliatedOrganization(new IdentifiedTitledEntityDto(v.getKey(), organizationDao.findById(v.getKey(), NameAware.class).orElseThrow().getName()));
                    if (CollectionUtils.isNotEmpty(affCommunityIds)) {
                        target.setAffiliatedCommunities((communityDao.findByIdIn(affCommunityIds, IdNameAware.class).stream()
                                .map(idNameAware -> new IdentifiedTitledEntityDto(idNameAware.getId(), idNameAware.getName())).collect(Collectors.toList())));
                    }
                    return target;
                })
                .collect(Collectors.toList());
    }
}
