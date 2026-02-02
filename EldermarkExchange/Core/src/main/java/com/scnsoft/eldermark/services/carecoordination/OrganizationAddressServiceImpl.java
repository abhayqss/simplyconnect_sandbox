package com.scnsoft.eldermark.services.carecoordination;

import com.google.maps.model.LatLng;
import com.scnsoft.eldermark.dao.carecoordination.OrganizationAddressDao;
import com.scnsoft.eldermark.entity.OrganizationAddress;
import com.scnsoft.eldermark.services.marketplace.MapsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationAddressServiceImpl implements OrganizationAddressService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationAddressServiceImpl.class);

    @Autowired
    private MapsService mapsService;

    @Autowired
    private OrganizationAddressDao organizationAddressDao;

    @Override
    @Async
    public void populateAllLocationForOutdatedAddresses() {
        LOGGER.info("Populate outdated organization addresses with location");
        final List<OrganizationAddress> addresses = getOrganizationAddressDao().findWithNotUptodateLocation();
        for (final OrganizationAddress address : addresses) {
            final LatLng coordinatesByAddress = getMapsService().getCoordinatesByAddress(address.getDisplayAddress());
            address.setLocationUpToDate(true);
            if (coordinatesByAddress != null) {
                address.setLatitude(coordinatesByAddress.lat);
                address.setLongitude(coordinatesByAddress.lng);
                organizationAddressDao.save(address);
            }

        }
    }

    public MapsService getMapsService() {
        return mapsService;
    }

    public void setMapsService(MapsService mapsService) {
        this.mapsService = mapsService;
    }

    public OrganizationAddressDao getOrganizationAddressDao() {
        return organizationAddressDao;
    }

    public void setOrganizationAddressDao(OrganizationAddressDao organizationAddressDao) {
        this.organizationAddressDao = organizationAddressDao;
    }
}
