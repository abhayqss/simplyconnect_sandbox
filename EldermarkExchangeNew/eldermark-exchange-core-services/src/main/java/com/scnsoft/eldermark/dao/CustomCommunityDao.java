package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

public interface CustomCommunityDao {
    Map<Long, HieConsentPolicyType> findCommunityStatePolicy(Specification<Community> specification);
}
