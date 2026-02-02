package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.IdProjectionRepository;
import com.scnsoft.eldermark.entity.community.CommunityPicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityPictureDao extends JpaRepository<CommunityPicture, Long>, IdProjectionRepository<Long> {
    List<CommunityPicture> findAllByCommunityId(Long communityId);
}
