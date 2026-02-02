package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.entity.phr.RegistrationApplication;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.shared.ResidentFilterPhrAppDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author phomal
 * Created on 10/30/2017.
 */
@Service
public class AsyncHealthProviderService {
    private static final Logger logger = LoggerFactory.getLogger(AsyncHealthProviderService.class);

    @Autowired
    UserDao userDao;

    @Autowired
    private HealthProviderService healthProviderService;

    @Async
    @Transactional
    public void updateUserResidentRecords(Long userId) {
        logger.debug("CurrentThread : " + Thread.currentThread().getId());

        final User user = userDao.findOne(userId);

        if (user.getResident() == null) return;

        final ResidentFilterPhrAppDto filter = new ResidentFilterPhrAppDto();
        filter.setSsn(user.getSsn());
        filter.setPhone(user.getResidentPhoneLegacy());
        filter.setEmail(user.getResidentEmailLegacy());
        filter.setFirstName(user.getResidentFirstNameLegacy());
        filter.setLastName(user.getResidentLastNameLegacy());

        healthProviderService.updateUserResidentRecordsHeavy(user, filter);
    }

    @Async
    @Transactional
    void updateUserResidentRecords(RegistrationApplication application) {
        logger.debug("CurrentThread : " + Thread.currentThread().getId());

        final ResidentFilterPhrAppDto filter = new ResidentFilterPhrAppDto();
        filter.setSsn(application.getSsn());
        filter.setPhone(application.getPhone());
        filter.setEmail(application.getEmail());
        filter.setFirstName(application.getFirstName());
        filter.setLastName(application.getLastName());

        healthProviderService.updateUserResidentRecordsHeavy(application, filter);
    }

}
