package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.community.CommunityName;
import com.scnsoft.eldermark.entity.referral.Referral;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomReferralDao {

    List<CommunityName> findCommunityNames(Specification<Referral> specification);
}
