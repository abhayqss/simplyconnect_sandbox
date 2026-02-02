package com.scnsoft.eldermark.service.task;

import com.scnsoft.eldermark.dao.VitalSignDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@ConditionalOnProperty(value = "vitalSigns.scheduled.archive.enabled", havingValue = "true")
public class ScheduledVitalSignArchiveServiceImpl implements ScheduledVitalSignArchiveService {

    private final Logger logger = LoggerFactory.getLogger(ScheduledVitalSignArchiveServiceImpl.class);

    @Value("${vitalSigns.scheduled.archive.expiration.minutes}")
    private Long expirationTimeInMinutes;

    @Autowired
    private VitalSignDao vitalSignDao;

    @Override
    @Scheduled(cron = "${vitalSigns.scheduled.archive.cron}", zone = "${vitalSigns.scheduled.archive.timezone}")
    public void archive() {

        logger.info("Start vital signs archiving");

        var fromDate = Instant.EPOCH;
        var toDate = Instant.now().minus(expirationTimeInMinutes, ChronoUnit.MINUTES);

        vitalSignDao.archive(fromDate, toDate);

        logger.info("Vital signs archiving completed");
    }
}
