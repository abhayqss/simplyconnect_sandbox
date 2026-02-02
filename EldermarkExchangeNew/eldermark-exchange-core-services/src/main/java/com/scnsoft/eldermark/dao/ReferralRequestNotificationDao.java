package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.referral.ReferralRequestNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralRequestNotificationDao extends JpaRepository<ReferralRequestNotification, Long> {
}
