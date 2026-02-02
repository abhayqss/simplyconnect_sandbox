package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dto.AffiliationInfoItemDto;
import com.scnsoft.eldermark.dto.OrganizationAffiliationInfoItemDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedViewableEntityDto;
import com.scnsoft.eldermark.entity.AffiliatedOrganization;
import com.scnsoft.eldermark.service.security.CommunitySecurityService;
import com.scnsoft.eldermark.service.security.OrganizationSecurityService;
import com.scnsoft.eldermark.util.StreamUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AffiliatedInfoConverterImpl implements AffiliatedInfoConverter {

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private CommunitySecurityService communitySecurityService;

    @Autowired
    private OrganizationSecurityService organizationSecurityService;

    @Override
    public <T extends AffiliationInfoItemDto> List<T> convertAffiliationInfo(List<AffiliatedOrganization> input,
                                                                             Function<AffiliatedOrganization, Long> organizationIdExtractor,
                                                                             Supplier<T> itemConstructor,
                                                                             BiConsumer<T, Map.Entry<Long, List<AffiliatedOrganization>>> filler) {
        if (CollectionUtils.isEmpty(input)) {
            return Collections.emptyList();
        }

        Map<Long, List<AffiliatedOrganization>> groupedByTargetOrgId = input.stream()
                .collect(Collectors.groupingBy(organizationIdExtractor,
                        Collectors.toCollection(ArrayList::new) //explicit support for null values in list
                ));

        return groupedByTargetOrgId.entrySet().stream()
                .map(entry -> {
                    var result = itemConstructor.get();
                    filler.accept(result, entry);
                    return result;
                }).collect(Collectors.toList());
    }

    @Override
    public <T extends AffiliationInfoItemDto> BiConsumer<T, Map.Entry<Long, List<AffiliatedOrganization>>> baseInfoItemFiller(
            List<AffiliatedOrganization> input,
            Function<AffiliatedOrganization, Long> organizationIdExtractor,
            Function<AffiliatedOrganization, Long> communityIdExtractor
    ) {
        var orgIds = input.stream().map(organizationIdExtractor).collect(Collectors.toSet());
        var organizationsCache = organizationDao.findByIdIn(orgIds, IdNameAware.class).stream().collect(StreamUtils.toMapOfUniqueKeys(IdAware::getId));

        var commIds = input.stream().map(communityIdExtractor).filter(Objects::nonNull).collect(Collectors.toSet());
        var communitiesCache = communityDao.findByIdIn(commIds, IdNameAware.class).stream().collect(StreamUtils.toMapOfUniqueKeys(IdAware::getId));

        return (result, entry) -> {
            var organization = organizationsCache.get(entry.getKey());
            result.setOrganization(convertOrganization(organization));
            var communityIds = entry.getValue().stream()
                    .map(communityIdExtractor)
                    .collect(Collectors.toCollection(HashSet::new));

            if (communityIds.contains(null)) {
                //'all' mode
                result.setCommunities(Collections.emptyList());
            } else {
                result.setCommunities(communityIds.stream()
                        .map(communitiesCache::get)
                        .map(this::convertCommunity)
                        .collect(Collectors.toList()));
            }
        };
    }

    @Override
    public BiConsumer<OrganizationAffiliationInfoItemDto, Map.Entry<Long, List<AffiliatedOrganization>>> organizationInfoItemFiller(
            List<AffiliatedOrganization> input,
            Function<AffiliatedOrganization, Long> ownCommunityIdExtractor) {

        var ownCommIds = input.stream().map(ownCommunityIdExtractor).filter(Objects::nonNull).collect(Collectors.toSet());
        var ownCommunitiesCache = communityDao.findByIdIn(ownCommIds, IdNameAware.class).stream().collect(StreamUtils.toMapOfUniqueKeys(IdAware::getId));

        return (result, entry) -> {
            var ownCommunityIds = entry.getValue().stream()
                    .map(ownCommunityIdExtractor)
                    .collect(Collectors.toCollection(HashSet::new));

            if (ownCommunityIds.contains(null)) {
                //'all' mode
                result.setOwnCommunities(Collections.emptyList());
            } else {
                result.setOwnCommunities(ownCommunityIds.stream()
                        .map(ownCommunitiesCache::get)
                        .map(this::convertCommunity)
                        .collect(Collectors.toList()));
            }
        };
    }

    private IdentifiedNamedViewableEntityDto convertOrganization(IdNameAware organization) {
        return new IdentifiedNamedViewableEntityDto(
                organization.getId(),
                organization.getName(),
                organizationSecurityService.canView(organization.getId()));
    }

    private IdentifiedNamedViewableEntityDto convertCommunity(IdNameAware community) {
        return new IdentifiedNamedViewableEntityDto(
                community.getId(),
                community.getName(),
                communitySecurityService.canView(community.getId()));
    }

}
