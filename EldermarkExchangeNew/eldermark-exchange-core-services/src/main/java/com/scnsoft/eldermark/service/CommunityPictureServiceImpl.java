package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.dao.CommunityPictureDao;
import com.scnsoft.eldermark.entity.community.CommunityPicture;
import com.scnsoft.eldermark.service.storage.CommunityPictureFileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommunityPictureServiceImpl extends BaseAttachmentServiceImpl<CommunityPicture, Long> implements CommunityPictureService {

    private final CommunityPictureDao communityPictureDao;

    public CommunityPictureServiceImpl(
        CommunityPictureFileStorage fileStorage,
        CommunityPictureDao communityPictureDao
    ) {
        super(fileStorage);
        this.communityPictureDao = communityPictureDao;
    }

    @Override
    protected JpaRepository<CommunityPicture, Long> getAttachmentDao() {
        return communityPictureDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityPicture> findAllByCommunityId(Long communityId) {
        return communityPictureDao.findAllByCommunityId(communityId);
    }

    @Override
    @Transactional(readOnly = true)
    public CommunityIdAware findCommunityIdAwareById(Long id) {
        return communityPictureDao.findById(id, CommunityIdAware.class).orElseThrow();
    }
}
