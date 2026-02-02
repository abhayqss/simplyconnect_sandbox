package com.scnsoft.eldermark.event.xml.dao;

import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.CustomCommunityDao;
import com.scnsoft.eldermark.entity.community.Community;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventCommunityDao extends CommunityDao, CustomCommunityDao {

    List<Community> findByOidAndOrganizationId(String oid, Long organizationId);
}
