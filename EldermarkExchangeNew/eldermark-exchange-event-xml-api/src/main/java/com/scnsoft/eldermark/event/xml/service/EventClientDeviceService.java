package com.scnsoft.eldermark.event.xml.service;

import com.scnsoft.eldermark.entity.client.ClientDevice;
import com.scnsoft.eldermark.entity.community.Community;

public interface EventClientDeviceService {

    ClientDevice findByDeviceIdAndCommunity(String deviceId, Community community);
}
