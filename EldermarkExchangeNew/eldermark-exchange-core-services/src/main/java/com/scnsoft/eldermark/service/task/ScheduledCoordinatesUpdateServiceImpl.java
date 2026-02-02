package com.scnsoft.eldermark.service.task;

import com.scnsoft.eldermark.service.CommunityAddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "coordinates.scheduled.update.enabled", havingValue = "true")
public class ScheduledCoordinatesUpdateServiceImpl implements ScheduledCoordinatesUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledCoordinatesUpdateServiceImpl.class);

    @Autowired
    private CommunityAddressService communityAddressService;

    @Override
    @Scheduled(cron = "0 0 1 * * *")
    public void populateCoordinatesForOutdatedAddresses() {
        logger.info("Updating coordinates of communities");
        communityAddressService.populateAllLocationForOutdatedAddresses();
        logger.info("Finished updating coordinates of communities");
    }
}
