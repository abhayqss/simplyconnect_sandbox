package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.AffiliationInfoItemDto;
import com.scnsoft.eldermark.dto.OrganizationAffiliationInfoItemDto;
import com.scnsoft.eldermark.entity.AffiliatedOrganization;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface AffiliatedInfoConverter {

    <T extends AffiliationInfoItemDto> List<T> convertAffiliationInfo(List<AffiliatedOrganization> input,
                                                                      Function<AffiliatedOrganization, Long> organizationIdExtractor,
                                                                      Supplier<T> itemConstructor,
                                                                      BiConsumer<T, Map.Entry<Long, List<AffiliatedOrganization>>> filler);


    <T extends AffiliationInfoItemDto> BiConsumer<T, Map.Entry<Long, List<AffiliatedOrganization>>> baseInfoItemFiller(
            List<AffiliatedOrganization> input,
            Function<AffiliatedOrganization, Long> organizationIdExtractor,
            Function<AffiliatedOrganization, Long> communityIdExtractor
    );

    BiConsumer<OrganizationAffiliationInfoItemDto, Map.Entry<Long, List<AffiliatedOrganization>>> organizationInfoItemFiller(
            List<AffiliatedOrganization> input,
            Function<AffiliatedOrganization, Long> ownCommunityIdExtractor);

}
