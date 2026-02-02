package com.scnsoft.eldermark.event.xml.service;

import com.scnsoft.eldermark.event.xml.schema.Community;

public interface EventCommunityService {

    com.scnsoft.eldermark.entity.community.Community getOrCreateCommunityFromSchema(com.scnsoft.eldermark.entity.Organization organization, Community community);
}
