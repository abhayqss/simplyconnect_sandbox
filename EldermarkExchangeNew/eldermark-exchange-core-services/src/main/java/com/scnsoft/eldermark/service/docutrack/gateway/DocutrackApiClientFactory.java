package com.scnsoft.eldermark.service.docutrack.gateway;

import com.scnsoft.eldermark.entity.community.Community;

public interface DocutrackApiClientFactory {

    DocutrackApiClient createDocutrackApiClient(Community community);

}
