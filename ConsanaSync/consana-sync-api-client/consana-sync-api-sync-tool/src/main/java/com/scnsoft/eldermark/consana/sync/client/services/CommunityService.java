package com.scnsoft.eldermark.consana.sync.client.services;

import com.scnsoft.eldermark.consana.sync.client.entities.Organization;

import java.util.Collection;
import java.util.List;

public interface CommunityService {

    List<Long> getInitialSyncEnabledCommunityIds();

    List<Organization> findAllByIds(Collection<Long> ids);
}
