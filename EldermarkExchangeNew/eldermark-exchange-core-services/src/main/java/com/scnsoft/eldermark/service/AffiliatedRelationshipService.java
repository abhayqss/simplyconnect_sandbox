package com.scnsoft.eldermark.service;

import java.util.Collection;

public interface AffiliatedRelationshipService {

    boolean existsByPrimaryCommunityIdIn(Collection<Long> communityIds);
}
