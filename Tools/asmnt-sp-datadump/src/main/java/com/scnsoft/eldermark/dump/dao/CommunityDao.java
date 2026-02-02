package com.scnsoft.eldermark.dump.dao;

import com.scnsoft.eldermark.dump.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityDao extends JpaRepository<Community, Long> {

    List<Community> findAllByOrganizationId(Long organizationId);
    
}
