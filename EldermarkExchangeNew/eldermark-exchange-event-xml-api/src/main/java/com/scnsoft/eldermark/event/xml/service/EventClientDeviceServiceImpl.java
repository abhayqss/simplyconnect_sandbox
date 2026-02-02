package com.scnsoft.eldermark.event.xml.service;

import com.scnsoft.eldermark.entity.client.ClientDevice;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.event.xml.dao.EventClientDeviceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventClientDeviceServiceImpl implements EventClientDeviceService {

    private final EventClientDeviceDao eventClientDeviceDao;

    @Autowired
    public EventClientDeviceServiceImpl(EventClientDeviceDao eventClientDeviceDao) {
        this.eventClientDeviceDao = eventClientDeviceDao;
    }

    @Override
    public ClientDevice findByDeviceIdAndCommunity(String deviceId, Community community) {
        return eventClientDeviceDao.findOneByDeviceIdAndClient_CommunityId(deviceId, community.getId());
    }
}
