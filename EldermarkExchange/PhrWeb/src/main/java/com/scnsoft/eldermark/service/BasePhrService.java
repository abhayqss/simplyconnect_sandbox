package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

/**
 * Base abstract service for PHR services that require user info (nine tenth of all PHR services).
 *
 * @author averazub
 * @author phomal
 * Created by averazub on 1/10/2017.
 */
public abstract class BasePhrService {

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected UserResidentRecordsDao userResidentRecordsDao;

    @Autowired
    private ResidentCareTeamMemberDao residentCareTeamMemberDao;

    protected static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public Long getResidentIdByReceiverId(Long receiverId) {
        return residentCareTeamMemberDao.getOne(receiverId).getResidentId();
    }

    protected Long getResidentIdOrThrow(Long userId) {
        Long residentId;
        List<Long> residentIds = userResidentRecordsDao.getActiveResidentIdsByUserId(userId);
        if (residentIds.size() == 1) {
            residentId = residentIds.get(0);
        } else {
            residentId = userDao.findOne(userId).getResidentId();
        }
        if (residentId == null) {
            throw new PhrException(PhrExceptionType.NOT_FOUND_PATIENT_INFO);
        }
        return residentId;
    }

    protected Long getEmployeeId(Long userId) {
        Long employeeId = userDao.findOne(userId).getEmployeeId();
        return employeeId;
    }

    protected User getUser(Employee employee) {
        return userDao.getFirstByEmployee(employee);
    }

    protected Long getUserId(Employee employee) {
        return userDao.getFirstByEmployee(employee).getId();
    }

    protected Long getEmployeeIdOrThrow(Long userId) {
        Long employeeId = getEmployeeId(userId);
        if (employeeId == null) {
            throw new PhrException(PhrExceptionType.NOT_FOUND_EMPLOYEE_INFO);
        }
        return employeeId;
    }

    protected static Employee getEmployeeOrThrow(User user) {
        Employee employee = user.getEmployee();
        if (employee == null) {
            throw new PhrException(PhrExceptionType.NOT_FOUND_EMPLOYEE_INFO);
        }
        return employee;
    }

    /**
     * @param userId User ID
     * @return For patient: A collection of resident IDs from his selected health provider(s);<br/>
     *         For Care Team Member: A collection of resident IDs from all patient's health providers.
     */
    public Collection<Long> getResidentIds(Long userId) {
        Collection<Long> residentIds;
        if (PhrSecurityUtils.checkAccessToUserInfo(userId)) {
            residentIds = userResidentRecordsDao.getActiveResidentIdsByUserId(userId);
        } else {
            residentIds = userResidentRecordsDao.getAllResidentIdsByUserId(userId);
        }
        return residentIds;
    }

    /**
     * @param userId User ID
     * @return For patient: A collection of provider IDs of his selected health provider(s);<br/>
     *         For Care Team Member: A collection of provider IDs from all patient's health providers.
     */
    protected Collection<Long> getHealthProviderProviderIds(Long userId) {
        if (PhrSecurityUtils.checkAccessToUserInfo(userId)) {
            return userResidentRecordsDao.getActiveProviderIdsByUserId(userId);
        } else {
            return userResidentRecordsDao.getAllProviderIdsByUserId(userId);
        }
    }

    /**
     * @param userId User ID
     * @return For patient: A collection of resident IDs from his selected health provider(s);<br/>
     *         For Care Team Member: A collection of resident IDs from all patient's health providers.
     */
    protected Collection<Long> getResidentIdsOrThrow(Long userId) {
        Collection<Long> residentIds = getResidentIds(userId);
        if (CollectionUtils.isEmpty(residentIds)) {
            throw new PhrException(PhrExceptionType.NOT_FOUND_PATIENT_INFO);
        }
        return residentIds;
    }

    /**
     * Check there's no existing user with the specified email
     * @param email Email address
     */
    protected void validateEmailNotExistsOrThrow(Database database, String email, PhrExceptionType phrExceptionType) throws PhrException {
        User existingUser = userDao.findUserByDatabaseAndEmailNormalizedAndAutocreatedIsFalse(database, Normalizer.normalizeEmail(email));
        if (existingUser != null) {
            throw new PhrException(phrExceptionType);
        }
    }

    /**
     * Search for existing user with the specified data
     * @param ssn Social Security Number. May be null
     * @param phone Phone number
     * @param email Email address
     * @return User or null
     */
    protected User getExistingUser(String ssn, String phone, String email) {
        List<User> existingUsers;
        if (StringUtils.isBlank(ssn)) {
            existingUsers = userDao.findUsersByEmailAndPhoneNormalized(Normalizer.normalizeEmail(email), Normalizer.normalizePhone(phone));
        } else {
            existingUsers = userDao.findUsersBySsnAndEmailAndPhoneNormalized(ssn, Normalizer.normalizeEmail(email), Normalizer.normalizePhone(phone));
        }
        if (CollectionUtils.isEmpty(existingUsers))
            return null;
        User user = existingUsers.get(0);
        return user;
    }

    protected User getExistingUser(Long databaseId, String phone, String email) {
        List<User> existingUsers = userDao.findUsersByDatabaseIdAndEmailNormalizedAndPhoneNormalized(
                databaseId, Normalizer.normalizeEmail(email), Normalizer.normalizePhone(phone));
        if (CollectionUtils.isEmpty(existingUsers))
            return null;
        User user = existingUsers.get(0);
        return user;
    }

    /**
     * Search for existing user with the specified data and with no SSN specified
     * @param phone Phone number
     * @param email Email address
     * @return User or null
     */
    protected User getExistingUserWithoutSsn(String phone, String email) {
        List<User> existingUsers;
        existingUsers = userDao.findUsersByEmailAndPhoneNormalizedAndSsnIsNull(Normalizer.normalizeEmail(email), Normalizer.normalizePhone(phone));
        if (CollectionUtils.isEmpty(existingUsers))
            return null;
        User user = existingUsers.get(0);
        return user;
    }

}
