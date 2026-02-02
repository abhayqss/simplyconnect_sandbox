package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.IdProjectionRepository;
import com.scnsoft.eldermark.entity.referral.ReferralAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralAttachmentDao extends JpaRepository<ReferralAttachment, Long>, IdProjectionRepository<Long> {
}
