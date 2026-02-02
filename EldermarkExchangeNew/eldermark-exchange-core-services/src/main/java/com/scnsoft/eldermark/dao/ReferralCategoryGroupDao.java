package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.referral.ReferralCategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralCategoryGroupDao extends JpaRepository<ReferralCategoryGroup, Long> {
}
