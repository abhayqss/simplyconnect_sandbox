package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.entity.community.CommunityPicture;

import java.util.List;

public interface CommunityPictureService extends BaseAttachmentService<CommunityPicture, Long> {

    List<CommunityPicture> findAllByCommunityId(Long communityId);

    CommunityIdAware findCommunityIdAwareById(Long id);
}
