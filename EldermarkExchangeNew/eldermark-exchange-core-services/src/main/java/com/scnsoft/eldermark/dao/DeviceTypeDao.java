package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.community.DeviceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceTypeDao extends JpaRepository<DeviceType, Long> {
    Page<DeviceType> findAllByCommunity_Id(Long communityId, Pageable pageable);
}
