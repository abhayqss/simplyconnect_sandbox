package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.ReferralIdAware;
import com.scnsoft.eldermark.dao.ReferralAttachmentDao;
import com.scnsoft.eldermark.entity.referral.ReferralAttachment;
import com.scnsoft.eldermark.service.storage.ReferralAttachmentFileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReferralAttachmentServiceImpl extends BaseAttachmentServiceImpl<ReferralAttachment, Long> implements ReferralAttachmentService {

    private final ReferralAttachmentDao referralAttachmentDao;

    public ReferralAttachmentServiceImpl(
        ReferralAttachmentFileStorage fileStorage,
        ReferralAttachmentDao referralAttachmentDao
    ) {
        super(fileStorage);
        this.referralAttachmentDao = referralAttachmentDao;
    }

    @Override
    protected JpaRepository<ReferralAttachment, Long> getAttachmentDao() {
        return referralAttachmentDao;
    }

    @Override
    @Transactional(readOnly = true)
    public ReferralIdAware findReferralIdAwareById(Long id) {
        return referralAttachmentDao.findById(id, ReferralIdAware.class).orElseThrow();
    }
}
