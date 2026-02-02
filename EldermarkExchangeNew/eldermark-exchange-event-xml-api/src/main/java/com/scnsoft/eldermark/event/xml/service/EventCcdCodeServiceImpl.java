package com.scnsoft.eldermark.event.xml.service;

import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.event.xml.dao.EventCcdCodeDao;
import com.scnsoft.eldermark.event.xml.entity.emuns.Gender;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class EventCcdCodeServiceImpl implements EventCcdCodeService {

    private static final String GENDER_VALUE_SET_NAME = "AdministrativeGender";
    private static final String MARITAL_STATUS_VALUE_SET_NAME = "MaritalStatus";

    private final EventCcdCodeDao eventCcdCodeDao;

    @Autowired
    public EventCcdCodeServiceImpl(EventCcdCodeDao eventCcdCodeDao) {
        this.eventCcdCodeDao = eventCcdCodeDao;
    }

    @Override
    public CcdCode getGenderCcdCode(Gender gender) {
        return eventCcdCodeDao.getByCodeAndValueSetName(gender.getAdministrativeGenderCode(), GENDER_VALUE_SET_NAME);
    }

    @Override
    public CcdCode getMaritalStatus(String maritalStatus) {
        var codes = eventCcdCodeDao.findByValueSetNameAndDisplayNameIn(MARITAL_STATUS_VALUE_SET_NAME, Set.of(maritalStatus));
        if (CollectionUtils.isNotEmpty(codes)) {
            return codes.get(0);
        }
        return null;
    }
}
