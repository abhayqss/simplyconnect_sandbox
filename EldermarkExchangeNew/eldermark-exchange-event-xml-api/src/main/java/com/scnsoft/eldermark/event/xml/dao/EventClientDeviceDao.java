package com.scnsoft.eldermark.event.xml.dao;

import com.scnsoft.eldermark.entity.client.ClientDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventClientDeviceDao extends JpaRepository<ClientDevice, Long> {

    ClientDevice findOneByDeviceIdAndClient_CommunityId(String deviceId, long communityId);
}
