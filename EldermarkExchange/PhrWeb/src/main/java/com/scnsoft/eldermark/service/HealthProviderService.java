package com.scnsoft.eldermark.service;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Striped;
import com.scnsoft.eldermark.dao.phr.ResidentLightDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.ResidentLight;
import com.scnsoft.eldermark.entity.phr.RegistrationApplication;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.entity.phr.UserResidentRecord;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.shared.ResidentFilterPhrAppDto;
import com.scnsoft.eldermark.web.entity.HealthProviderDto;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.locks.Lock;

/**
 * <h3>Health Data Providers service.</h3>
 * Routines for searching and storing User-Resident relations (see {@link UserResidentRecord}), and switching between records.
 *
 * @author averazub
 * @author phomal
 * Created by averazub on 1/12/2017.
 */
@Service
@Transactional
public class HealthProviderService extends BasePhrService {

    @Autowired
    ResidentService residentService;

    @Autowired
    ResidentLightDao residentLightDao;

    @Autowired
    UserResidentRecordsDao userResidentRecordsDao;

    private ConcurrentWorker worker = new ConcurrentWorker();

    public List<HealthProviderDto> getHealthProviders(Long userId) {

        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        User user = userDao.getOne(userId);
        if (user.getResident() == null) {
            return Collections.emptyList();
        }
        updateUserResidentRecords(user);
        //asyncHealthProviderService.updateUserResidentRecords(user);

        return getHealthProvidersWithoutUpdate(userId);
    }

    public void updateUserResidentRecords(Long userId) {
        User user = userDao.getOne(userId);
        updateUserResidentRecords(user);
    }

    void updateUserResidentRecords(User user) {
        // SCPAPP-210 Apply the results of “Matching patients’ records” mechanism to PHR mobile app
        if (user.getResident() == null) return;
        final Set<Long> residentIds = residentService.getDirectMergedResidentIds(user.getResident());
        residentIds.add(user.getResidentId());
        updateUserResidentRecords(user, residentIds, true);
    }

    /**
     * Search associated resident records across all organizations and persist new User-Resident relations (see {@link UserResidentRecord}).
     * This method is designed to be used during <b>Health Data Providers</b> update request. It takes a long time to execute,
     * so although you can call this method directly it's recommended to be called asynchronously via {@link AsyncHealthProviderService}.
     */
    void updateUserResidentRecordsHeavy(User user, ResidentFilterPhrAppDto filter) {
        // SCPAPP-327 Search for patient records by name/SSN (if specified)/phone/email across all organizations (in background) during Health Providers list update
        List<Resident> residents = residentService.getResidents(filter);

        Set<Long> residentIds = new HashSet<>();
        CollectionUtils.collect(residents, new BeanToPropertyValueTransformer("id"), residentIds);

        updateUserResidentRecords(user, residentIds, false);
    }

    /**
     * Search associated resident records across all organizations and persist User-Resident relations (see {@link UserResidentRecord}).
     * This method is designed to be used during <b>Sign Up as Consumer</b> path. It takes a long time to execute,
     * so although you can call this method directly it's recommended to be called asynchronously via {@link AsyncHealthProviderService}.
     */
    void updateUserResidentRecordsHeavy(RegistrationApplication application, ResidentFilterPhrAppDto filter) {
        // SCPAPP-327 Search for patient records by name/SSN (if specified)/phone/email across all organizations (in background) during sign up
        List<Resident> residents = residentService.getResidents(filter);

        Set<Long> residentIds = new HashSet<>();
        CollectionUtils.collect(residents, new BeanToPropertyValueTransformer("id"), residentIds);

        updateUserResidentRecords(application, residentIds);
    }

    Set<User> getUsersByResidentId(Long residentId) {
        final List<Long> userIds = userResidentRecordsDao.getAllUserIdsByResidentId(residentId);
        return userDao.getByIdIn(userIds);
    }

    private List<HealthProviderDto> getHealthProvidersWithoutUpdate(Long userId) {
        List<UserResidentRecord> records = userResidentRecordsDao.getByUserId(userId);
        List<HealthProviderDto> results = new ArrayList<>();
        for (UserResidentRecord record : records)
            results.add(transform(record));
        return results;
    }

    public List<HealthProviderDto> setCurrentHealthProvider(Long userId, Long residentId) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        setCurrentUserResidentRecord(userId, residentId);

        return getHealthProvidersWithoutUpdate(userId);
    }

    /**
     * This is a general method designed for persisting User-Resident relations (see {@link UserResidentRecord}) found by any of two search algorithms:
     * <ol>
     *     <li>Automatic and manual matching.</li>
     *     <li>Background search across all organizations.</li>
     * </ol>
     */
    private void updateUserResidentRecordsInternal(Long userId, Long residentId, Set<Long> updated, boolean foundByMatching) {
        // Set of updated records IDs = BLIST

        //Get current records = ALIST
        Set<Long> current = new HashSet<>();
        // Set of records found by different mechanism = CLIST
        Set<Long> toNotModify = new HashSet<>();
        List<UserResidentRecord> currentResidentList = userResidentRecordsDao.getByUserId(userId);
        for (UserResidentRecord currentResident : currentResidentList) {
            current.add(currentResident.getResidentId());
            if (foundByMatching != currentResident.getFoundByMatching()) {
                toNotModify.add(currentResident.getResidentId());
            }
        }

        //Calculate intersection
        Set<Long> toStay = Sets.intersection(updated, current);
        // + CLIST
        toStay = Sets.union(toStay, toNotModify);
        //Calculate AddedRecords !ALIST*BLIST
        Set<Long> toAdd = Sets.difference(updated, current);
        //Calculate RemovedRecords BLIST - ALIST
        Set<Long> toRemove = Sets.difference(current, updated);
        // - CLIST
        toRemove = Sets.difference(toRemove, toNotModify);

        //Do remove
        if (!toRemove.isEmpty()) {
            userResidentRecordsDao.deleteUnusedRecords(userId, toRemove);
        }

        boolean needResetCurrent = (residentId == null) || (toRemove.contains(residentId));
        if (needResetCurrent) {
            if (!toStay.isEmpty()) {
                Long anyResidentId = toStay.iterator().next();
                setCurrentUserResidentRecord(userId, anyResidentId);
            }
        }

        //Do insert
        if (CollectionUtils.isEmpty(updated)) {
            return;
        }
        List<UserResidentRecord> batch = new ArrayList<>();
        for (ResidentLight src : residentLightDao.findAllByIdIn(updated)) {
            if (!toAdd.contains(src.getId())) continue;
            UserResidentRecord dest = transform(src, userId, null, true, foundByMatching);
            if (needResetCurrent) {
                userDao.updateMainResident(userId, dest.getResidentId());
                needResetCurrent = false;
            }
            batch.add(dest);
        }

        if (CollectionUtils.isNotEmpty(batch)) {
            userResidentRecordsDao.save(batch);
            userResidentRecordsDao.setCurrentRecordsAll(userId);
            userResidentRecordsDao.flush();
        }
    }

    /**
     * This method is designed to be used only for persisting data found by background residents search during <b>Sign Up as Consumer</b> path
     * (for completely new mobile users).
     */
    @Transactional(propagation = Propagation.MANDATORY)
    void updateUserResidentRecords(RegistrationApplication application, Set<Long> updated) {
        if (CollectionUtils.isEmpty(updated)) {
            return;
        }

        // Do insert all
        List<UserResidentRecord> batch = new ArrayList<>();
        for (ResidentLight src : residentLightDao.findAllByIdIn(updated)) {
            UserResidentRecord dest = transform(src, null, application, false, false);
            batch.add(dest);
        }

        if (CollectionUtils.isNotEmpty(batch)) {
            userResidentRecordsDao.save(batch);
            userResidentRecordsDao.flush();
        }
    }

    /**
     * This method is designed to be used only during the last step of <b>Sign Up as Consumer</b> path
     * (for completely new mobile users).
     */
    @Transactional(propagation = Propagation.MANDATORY)
    void updateUserResidentRecords(RegistrationApplication application, User newUser) {
        userResidentRecordsDao.activateRecords(application, newUser.getId());
    }

    private boolean setCurrentUserResidentRecord(Long userId, Long residentId) throws PhrException {
        if (residentId == null || residentId == 0) {
            // special case : turn on Merged Data mode
            int rowsUpdated = userResidentRecordsDao.setCurrentRecordsAll(userId);
            if (rowsUpdated < 1)
                throw new PhrException(PhrExceptionType.NOT_FOUND_PATIENT_INFO);
            return true;
        }

        userResidentRecordsDao.dropCurrentRecordForUser(userId);
        int rowsUpdated = userResidentRecordsDao.setCurrentRecord(userId, residentId);
        if (rowsUpdated != 1)
            throw new PhrException(PhrExceptionType.RESIDENT_RECORD_NOT_ASSOCIATED);

        return true;
    }

    private UserResidentRecord transform(ResidentLight src, Long userId, RegistrationApplication application, boolean isCurrent, boolean foundByMatching) {
        UserResidentRecord dest = new UserResidentRecord();
        dest.setUserId(userId);
        dest.setUserRegistrationApplication(application);
        dest.setResidentId(src.getId());
        Organization facility = src.getFacility();
        if (facility != null && src.getDatabase() != null) {
            dest.setProviderId(facility.getId());
            dest.setProviderName(src.getDatabase().getName() + " : " + facility.getName());
        }
        dest.setCurrent(isCurrent);
        dest.setFoundByMatching(foundByMatching);
        return dest;
    }

    private HealthProviderDto transform(UserResidentRecord src) {
        HealthProviderDto dest = new HealthProviderDto();
        dest.setResidentId(src.getResidentId());
        dest.setCurrent(src.getCurrent());
        dest.setProviderName(src.getProviderName());
        dest.setProviderId(src.getProviderId());
        return dest;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    void updateUserResidentRecords(User user, Set<Long> updated, boolean foundByMatching) {
        worker.stripedConcurrentUpdate(user.getId(), user.getResidentId(), updated, foundByMatching);
    }

    private final class ConcurrentWorker {
        private Striped<Lock> lockStripes = Striped.lock(20);

        public void stripedConcurrentUpdate(Long userId, Long residentId, Set<Long> updated, boolean foundByMatching) {
            Lock lock = lockStripes.get(userId);
            lock.lock();
            try {
                updateUserResidentRecordsInternal(userId, residentId, updated, foundByMatching);
            } finally {
                lock.unlock();
            }
        }
    }

}
