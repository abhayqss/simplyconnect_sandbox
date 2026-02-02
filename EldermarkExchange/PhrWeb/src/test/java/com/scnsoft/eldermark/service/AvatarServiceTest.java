package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.UserAvatarDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.entity.phr.UserAvatar;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 6/28/2017.
 */
public class AvatarServiceTest extends BaseServiceTest {

    @Mock
    private UserAvatarDao userAvatarDao;
    @Mock
    private UserDao userDao;
    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;

    @InjectMocks
    private AvatarService avatarService;

    private User setUpMockitoExpectations(Long consumerUserId) {
        final User user = super.createConsumer(consumerUserId);

        when(userResidentRecordsDao.getActiveResidentIdsByUserId(consumerUserId)).thenReturn(activeResidentIds);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerUserId)).thenReturn(allResidentIds);
        when(userDao.findOne(consumerUserId)).thenReturn(user);
        when(userDao.getOne(consumerUserId)).thenReturn(user);

        return user;
    }

    @Test
    public void testGetPhotoUrl() {
        // Expected objects
        String expected = "/phr/" + userId + "/profile/avatar";

        final UserAvatar avatar = new UserAvatar();
        avatar.setId(TestDataGenerator.randomId());
        avatar.setUserId(userId);
        avatar.setContentType("application/png");
        avatar.setFile("Hello world".getBytes());

        // Mockito expectations
        when(userAvatarDao.getByUserId(userId)).thenReturn(avatar);

        // Execute the method being tested
        String result = avatarService.getPhotoUrl(userId);

        // Validation
        assertEquals(expected, result);
    }

    @Test
    public void testGetPhotoUrlNull() {
        // Mockito expectations
        when(userAvatarDao.getByUserId(userId)).thenReturn(null);

        // Execute the method being tested
        String result = avatarService.getPhotoUrl(userId);

        // Validation
        assertEquals(null, result);
    }

    @Test
    public void testGetPhotoUrlNull2() {
        // Mockito expectations
        when(userAvatarDao.getByUserId(null)).thenThrow(NullPointerException.class);

        // Execute the method being tested
        String result = avatarService.getPhotoUrl(null);

        // Validation
        assertEquals(null, result);
    }

    @Test
    public void testDeleteAvatar() {
        // Mockito expectations
        setUpMockitoExpectations(userId);
        when(userAvatarDao.deleteByUserId(userId)).thenReturn(1);

        // Execute the method being tested
        Boolean result = avatarService.deleteAvatar(userId);

        // Validation
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testDeleteAvatar2() {
        // Mockito expectations
        setUpMockitoExpectations(userId);
        when(userAvatarDao.deleteByUserId(userId)).thenReturn(0);

        // Execute the method being tested
        Boolean result = avatarService.deleteAvatar(userId);

        // Validation
        assertEquals(Boolean.FALSE, result);
    }

    @Test
    public void testDownloadAvatar() {
        final User user = setUpMockitoExpectations(userId);

        // Expected objects
        final UserAvatar avatar = new UserAvatar();
        avatar.setId(TestDataGenerator.randomId());
        avatar.setUser(user);
        avatar.setUserId(userId);
        avatar.setContentType("application/png");
        avatar.setFile("Hello world".getBytes());

        final MockHttpServletResponse response = new MockHttpServletResponse();

        // Mockito expectations
        when(userAvatarDao.getByUserId(userId)).thenReturn(avatar);

        // Execute the method being tested
        avatarService.downloadAvatar(userId, response);

        // Validation
        assertEquals("application/png", response.getContentType());
    }

    @Test(expected = PhrException.class)
    public void testDownloadAvatarThrowsNotFound() {
        // Expected object
        final MockHttpServletResponse response = new MockHttpServletResponse();

        // Mockito expectations
        when(userAvatarDao.getByUserId(userId)).thenReturn(null);

        // Execute the method being tested
        avatarService.downloadAvatar(userId, response);
    }

    @Test
    public void testGetAvatarAsBase64() {
        final User user = setUpMockitoExpectations(userId);

        // Expected objects
        final UserAvatar avatar = new UserAvatar();
        avatar.setId(TestDataGenerator.randomId());
        avatar.setUser(user);
        avatar.setUserId(userId);
        avatar.setContentType("application/png");
        avatar.setFile("Hello, world!".getBytes());

        final String expectedPhotoBase64 = Base64.encodeBase64String(avatar.getFile());

        // Mockito expectations
        when(userAvatarDao.getByUserId(userId)).thenReturn(avatar);

        // Execute the method being tested
        String result = avatarService.getAvatarAsBase64(userId);

        // Validation
        assertEquals(expectedPhotoBase64, result);
    }

    @Test(expected = PhrException.class)
    public void testGetAvatarAsBase64ThrowsNotFound() {
        // Mockito expectations
        when(userAvatarDao.getByUserId(userId)).thenReturn(null);

        // Execute the method being tested
        avatarService.getAvatarAsBase64(userId);
    }

    @Test
    public void testSetAvatar() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId);

        final byte[] content = "Hello world!".getBytes();
        final MockMultipartFile file = new MockMultipartFile("filename", "original filename", "application/png", content);

        final UserAvatar expectedAvatar = new UserAvatar();
        expectedAvatar.setUser(user);
        expectedAvatar.setUserId(userId);
        expectedAvatar.setContentType("application/png");
        expectedAvatar.setFile(content);

        // Mockito expectations
        when(userAvatarDao.save(any(UserAvatar.class))).then(returnsFirstArg());

        // Execute the method being tested
        UserAvatar result = avatarService.setAvatar(userId, file);

        // Validation
        assertThat(result, sameBeanAs(expectedAvatar));
    }

    @Test
    public void testSetAvatarWithReplace() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId);

        final byte[] content = "Hello world!".getBytes();
        final MockMultipartFile file = new MockMultipartFile("filename", "original filename", "application/png", content);

        final Long avatarId = TestDataGenerator.randomId();
        final UserAvatar existingAvatar = new UserAvatar();
        existingAvatar.setId(avatarId);
        existingAvatar.setUser(user);
        existingAvatar.setUserId(userId);
        existingAvatar.setContentType("application/png");
        existingAvatar.setFile("dsfsdfsdfsdfds".getBytes());

        final UserAvatar expectedAvatar = new UserAvatar();
        expectedAvatar.setId(avatarId);
        expectedAvatar.setUser(user);
        expectedAvatar.setUserId(userId);
        expectedAvatar.setContentType("application/png");
        expectedAvatar.setFile(content);

        // Mockito expectations
        when(userAvatarDao.save(any(UserAvatar.class))).then(returnsFirstArg());
        when(userAvatarDao.getByUserId(userId)).thenReturn(existingAvatar);

        // Execute the method being tested
        UserAvatar result = avatarService.setAvatar(userId, file);

        // Validation
        assertThat(result, sameBeanAs(expectedAvatar));
    }

    @Test
    public void testSetAvatarAsBase64() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId);

        final UserAvatar expectedAvatar = new UserAvatar();
        expectedAvatar.setUser(user);
        expectedAvatar.setUserId(userId);
        expectedAvatar.setContentType(null);
        expectedAvatar.setFile("Hello, world!".getBytes());

        final String photoBase64 = Base64.encodeBase64String(expectedAvatar.getFile());

        // Mockito expectations
        when(userAvatarDao.save(any(UserAvatar.class))).then(returnsFirstArg());

        // Execute the method being tested
        UserAvatar result = avatarService.setAvatar(userId, photoBase64);

        // Validation
        assertThat(result, sameBeanAs(expectedAvatar));
    }

    @Test
    public void testSetAvatarAsBase64WithReplace() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId);

        final Long avatarId = TestDataGenerator.randomId();
        final UserAvatar existingAvatar = new UserAvatar();
        existingAvatar.setId(avatarId);
        existingAvatar.setUser(user);
        existingAvatar.setUserId(userId);
        existingAvatar.setContentType("application/png");
        existingAvatar.setFile("dsfsdfsdfsdfds".getBytes());

        final UserAvatar expectedAvatar = new UserAvatar();
        expectedAvatar.setId(avatarId);
        expectedAvatar.setUser(user);
        expectedAvatar.setUserId(userId);
        expectedAvatar.setContentType(null);
        expectedAvatar.setFile("Hello, world!".getBytes());

        final String photoBase64 = Base64.encodeBase64String(expectedAvatar.getFile());

        // Mockito expectations
        when(userAvatarDao.save(any(UserAvatar.class))).then(returnsFirstArg());
        when(userAvatarDao.getByUserId(userId)).thenReturn(existingAvatar);

        // Execute the method being tested
        UserAvatar result = avatarService.setAvatar(userId, photoBase64);

        // Validation
        assertThat(result, sameBeanAs(expectedAvatar));
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme