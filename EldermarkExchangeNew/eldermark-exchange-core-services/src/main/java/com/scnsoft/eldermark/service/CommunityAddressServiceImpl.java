package com.scnsoft.eldermark.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.maps.model.LatLng;
import com.scnsoft.eldermark.dao.CommunityAddressDao;
import com.scnsoft.eldermark.entity.community.CommunityAddress;

@Service
@Transactional
public class CommunityAddressServiceImpl implements CommunityAddressService {

    private static final Logger logger = LoggerFactory.getLogger(CommunityAddressServiceImpl.class);

    @Autowired
    CommunityAddressDao communityAddressDao;

    @Autowired
    private MapsServiceImpl mapsService;

    @Override
    @Async
    public void populateAllLocationForOutdatedAddresses(Long organizationId) {

        List<CommunityAddress> addresses = communityAddressDao
                .findByLocationUpToDateIsNullOrLocationUpToDateIsFalseAndOrganizationId(organizationId);
        populateCoordinates(addresses);
        communityAddressDao.saveAll(addresses);
    }

    @Override
    @Async
    public void populateAllLocationForOutdatedAddresses() {
        List<CommunityAddress> addresses = communityAddressDao
                .findByLocationUpToDateIsNullOrLocationUpToDateIsFalse();
        if (CollectionUtils.isNotEmpty(addresses)) {
            logger.info("Updating coordinates for communities. Count is " + addresses.size());
            populateCoordinates(addresses);
            communityAddressDao.saveAll(addresses);
            logger.info("Updating coordinates completed");
        }
    }

    private void populateCoordinates(List<CommunityAddress> addresses) {
        addresses.forEach(x -> {
            String address = x.getDisplayAddress();
            if (StringUtils.isNotBlank(address)) {
                LatLng coordinatesByAddress = mapsService.getCoordinatesByAddress(x.getDisplayAddress());
                if (coordinatesByAddress != null) {
                    x.setLatitude(coordinatesByAddress.lat);
                    x.setLongitude(coordinatesByAddress.lng);
                }
            }
            x.setLocationUpToDate(Boolean.TRUE);
        });
    }
}
