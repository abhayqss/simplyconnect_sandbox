package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.community.Community;

import java.util.List;

public interface ClientPharmacyService {

    List<Community> findPharmaciesAsCommunitiesByClientId(Long clientId);
}
