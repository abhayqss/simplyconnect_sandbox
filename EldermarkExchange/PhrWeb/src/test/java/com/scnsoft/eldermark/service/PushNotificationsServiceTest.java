package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.PushNotificationRegistrationDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.entity.phr.PushNotificationRegistration;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import org.junit.After;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 6/13/2017.
 */
public class PushNotificationsServiceTest extends BaseServiceTest {

    @Mock
    private PushNotificationRegistrationDao pushNotificationRegistrationDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private PushNotificationsService pushNotificationsService;

    // Shared validation
    @After
    public void validate() {
        verify(authentication, atLeastOnce()).getDetails();
        verify(userDao).getOne(userId);
        verify(pushNotificationRegistrationDao).save(any(PushNotificationRegistration.class));
    }

    @Test
    public void testRegisterGcm() {
        // Expected objects
        final String regIdGcm = TestDataGenerator.randomGcmToken();
        final User persistedUser = User.Builder.anUser()
                .withId(userId)
                .build();

        // Mockito expectations
        when(userDao.getOne(userId)).thenReturn(persistedUser);

        // Execute the method being tested
        Boolean result = pushNotificationsService.register(userId, regIdGcm, PushNotificationRegistration.ServiceProvider.GCM);

        // Validation
        assertTrue(result);
    }

    @Test
    public void testRegisterApns() {
        // Expected objects
        final String regIdApns = TestDataGenerator.randomApnsToken();
        final User persistedUser = User.Builder.anUser()
                .withId(userId)
                .build();

        // Mockito expectations
        when(userDao.getOne(userId)).thenReturn(persistedUser);

        // Execute the method being tested
        Boolean result = pushNotificationsService.register(userId, regIdApns, PushNotificationRegistration.ServiceProvider.APNS_UN);

        // Validation
        assertTrue(result);
    }

    @Test
    public void testRegisterFcm() {
        // Expected objects
        final String regIdFcm = TestDataGenerator.randomGcmToken();
        final User persistedUser = User.Builder.anUser()
                .withId(userId)
                .build();

        // Mockito expectations
        when(userDao.getOne(userId)).thenReturn(persistedUser);

        // Execute the method being tested
        Boolean result = pushNotificationsService.register(userId, regIdFcm, PushNotificationRegistration.ServiceProvider.FCM);

        // Validation
        assertTrue(result);
    }

    @Test
    public void testRegisterDuplicate() {
        // Expected objects
        final String regId = TestDataGenerator.randomGcmToken();
        final User persistedUser = User.Builder.anUser()
                .withId(userId)
                .build();

        // Mockito expectations
        when(userDao.getOne(userId)).thenReturn(persistedUser);
        when(pushNotificationRegistrationDao.save(any(PushNotificationRegistration.class))).thenThrow(DataIntegrityViolationException.class);

        // Execute the method being tested
        Boolean result = pushNotificationsService.register(userId, regId, PushNotificationRegistration.ServiceProvider.FCM);

        // Validation
        assertTrue(result);
    }

}