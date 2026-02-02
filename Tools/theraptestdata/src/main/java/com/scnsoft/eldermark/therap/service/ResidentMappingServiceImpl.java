package com.scnsoft.eldermark.therap.service;

import com.scnsoft.eldermark.therap.bean.TherapRecord;
import com.scnsoft.eldermark.therap.dao.ResidentMappingDao;
import com.scnsoft.eldermark.therap.entity.ResidentMapping;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

@Service
public class ResidentMappingServiceImpl implements ResidentMappingService {

    private final ResidentMappingDao residentMappingDao;

    public ResidentMappingServiceImpl(ResidentMappingDao residentMappingDao) {
        this.residentMappingDao = residentMappingDao;
    }


    @Override
    public ResidentMapping generateAndCreateNewMapping(TherapRecord therapRecord) {
        var mapping = new ResidentMapping();
        mapping.setSourcePatientId(therapRecord.getPatientId());
        mapping.setSourceFirstName(therapRecord.getFirstName());
        mapping.setSourceLastName(therapRecord.getLastName());
        mapping.setSourceSsn(therapRecord.getSSN());
        mapping.setSourceDateOfBirth(therapRecord.getDateOfBirth());

        mapping = residentMappingDao.save(mapping);

        mapping.setNewPatientId("Resident" + mapping.getId());
        mapping.setNewFirstName("FN_resident " + mapping.getId());
        mapping.setNewLastName("LN_resident" + mapping.getId());
        mapping.setNewSsn(randomSSN());
        mapping.setNewDateOfBirth(randomDOB());

        mapping.setClassName(therapRecord.getClass().getName());
        mapping.setFilename(therapRecord.getFilename());

        return residentMappingDao.save(mapping);
    }

    private String randomDOB() {
        final Date startDate = new GregorianCalendar(1917, Calendar.JUNE, 1).getTime();
        final Date endDate = new GregorianCalendar(2017, Calendar.JUNE, 1).getTime();
        long ms = ThreadLocalRandom.current().nextLong(startDate.getTime(), endDate.getTime());
        return new SimpleDateFormat("MM/dd/yyyy").format(new Date(ms));
    }

    private String randomSSN() {
        String ssn;
        do {
            ssn = RandomStringUtils.randomNumeric(9);
        } while (!isValidSsn(ssn));
        return ssn;
    }

    private boolean isValidSsn(String ssn) {
        Pattern pattern = Pattern.compile("^(?!(000|666|9))\\d{3}(?!00)\\d{2}(?!0000)\\d{4}$");
        return pattern.matcher(ssn).matches();
    }

    @Override
    public ResidentMapping findAndUpdateMapping(TherapRecord therapRecord) {
        final List<ResidentMapping> residentMappingList = residentMappingDao.findAllBySourceFirstNameAndSourceLastName(
                therapRecord.getFirstName(),
                therapRecord.getLastName());

        if (residentMappingList.isEmpty()) {
            return null;
        }
        ResidentMapping residentMapping;

        if (residentMappingList.size() == 1) {
            residentMapping = residentMappingList.get(0);
        } else {
            //todo perhaps it's ok
            throw new RuntimeException("Multiple records found");
        }

        validateMatches(therapRecord, residentMapping);

        return updateMapping(therapRecord, residentMapping);
    }


    private void validateMatches(TherapRecord therapRecord, ResidentMapping residentMapping) {
        if (StringUtils.isNoneEmpty(therapRecord.getPatientId(), residentMapping.getSourcePatientId())
                && !therapRecord.getPatientId().equalsIgnoreCase(residentMapping.getSourcePatientId())) {
            throw new IllegalArgumentException("Patient ids don't match " + therapRecord.getPatientId() + ", " + residentMapping.getSourcePatientId()
                    + ". File is " + therapRecord.getFilename() + " patient is " + therapRecord.getFirstName() + " " + therapRecord.getLastName());
        }

        if (StringUtils.isNoneEmpty(therapRecord.getSSN(), residentMapping.getSourceSsn())
                && !therapRecord.getSSN().equalsIgnoreCase(residentMapping.getSourceSsn())) {
            throw new IllegalArgumentException("Ssns don't match " + therapRecord.getSSN() + ", " + residentMapping.getSourceSsn()
                    + ". File is " + therapRecord.getFilename() + " patient is " + therapRecord.getFirstName() + " " + therapRecord.getLastName());
        }

//        if (StringUtils.isNoneEmpty(therapRecord.getDateOfBirth(), residentMapping.getSourceDateOfBirth())
//                && !therapRecord.getDateOfBirth().equalsIgnoreCase(residentMapping.getSourceDateOfBirth())) {
//            throw new IllegalArgumentException("DOB don't match " + therapRecord.getDateOfBirth() + ", " + residentMapping.getSourceDateOfBirth()
//                    + ". File is " + therapRecord.getFilename() + " patient is " + therapRecord.getFirstName() + " " + therapRecord.getLastName());
//        }
    }

    private ResidentMapping updateMapping(TherapRecord therapRecord, ResidentMapping residentMapping) {
        boolean update = false;
        if (StringUtils.isNotEmpty(therapRecord.getPatientId()) && StringUtils.isEmpty(residentMapping.getSourcePatientId())) {
            residentMapping.setSourcePatientId(therapRecord.getPatientId());
            update = true;
        }

        if (StringUtils.isNotEmpty(therapRecord.getSSN()) && StringUtils.isEmpty(residentMapping.getSourceSsn())) {
            residentMapping.setSourceSsn(therapRecord.getSSN());
            update = true;
        }

        if (StringUtils.isNotEmpty(therapRecord.getDateOfBirth()) && StringUtils.isEmpty(residentMapping.getSourceDateOfBirth())) {
            residentMapping.setNewDateOfBirth(therapRecord.getDateOfBirth());
            update = true;
        }

        if (update) {
            return residentMappingDao.save(residentMapping);
        }
        return residentMapping;
    }
}
