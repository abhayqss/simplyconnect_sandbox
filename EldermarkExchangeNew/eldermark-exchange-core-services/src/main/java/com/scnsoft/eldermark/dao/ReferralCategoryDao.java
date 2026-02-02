package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.referral.ReferralCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralCategoryDao extends JpaRepository<ReferralCategory, Long> {
}
