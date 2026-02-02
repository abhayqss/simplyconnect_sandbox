package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.Guardian;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.facades.ccd.CcdFacade;
import com.scnsoft.eldermark.shared.ccd.CcdHeaderPatientDto;
import com.scnsoft.eldermark.shared.ccd.GuardianDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 7/4/2017.
 */
public class PhrServiceTest extends BaseServiceTest {

    @Mock
    private CcdFacade ccdFacade;
    @Mock
    private ResidentDao residentDao;
    @Mock
    private CareTeamSecurityUtils careTeamSecurityUtils;
    @Mock
    private UserDao userDao;
    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;

    @InjectMocks
    private PhrService phrService;

    @Before
    public void injectDozer() {
        final DozerBeanMapper dozer = new DozerBeanMapper();
        phrService.setDozer(dozer);
    }

    private User setUpMockitoExpectations(Long consumerUserId, boolean isMerged) {
        final User user = super.createConsumer(consumerUserId);

        if (isMerged) {
            when(userResidentRecordsDao.getActiveResidentIdsByUserId(consumerUserId)).thenReturn(allResidentIds);
        } else {
            when(userResidentRecordsDao.getActiveResidentIdsByUserId(consumerUserId)).thenReturn(activeResidentIds);
        }
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerUserId)).thenReturn(allResidentIds);
        when(userDao.findOne(consumerUserId)).thenReturn(user);
        when(userDao.getOne(consumerUserId)).thenReturn(user);
        if (consumerUserId.equals(userId)) {
            when(careTeamSecurityUtils.getCurrentUser()).thenReturn(user);
        }

        return user;
    }

    @Test
    public void testGetUserDemographics() {
        final CcdHeaderPatientDto ccdHeaderPatientDto = new CcdHeaderPatientDto();
        ccdHeaderPatientDto.setGuardians(Collections.<GuardianDto>emptyList());
        final CcdHeaderPatientDto expectedCcdHeaderPatientDto = new CcdHeaderPatientDto();
        expectedCcdHeaderPatientDto.setGuardians(null);

        // Mockito expectations
        setUpMockitoExpectations(userId, false);
        when(ccdFacade.getCcdHeaderPatient(eq(residentId), anyBoolean())).thenReturn(ccdHeaderPatientDto);

        // Execute the method being tested
        CcdHeaderPatientDto result = phrService.getUserDemographics(userId, false);

        // Validation
        assertThat(result, sameBeanAs(expectedCcdHeaderPatientDto));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId);
    }

    @Test
    public void testGetUserDemographicsV2() {
        final CcdHeaderPatientDto ccdHeaderPatientDto = new CcdHeaderPatientDto();
        ccdHeaderPatientDto.setGuardians(Collections.<GuardianDto>emptyList());
        final CcdHeaderPatientDto expectedCcdHeaderPatientDto = new CcdHeaderPatientDto();
        expectedCcdHeaderPatientDto.setGuardians(Collections.<GuardianDto>emptyList());

        // Mockito expectations
        setUpMockitoExpectations(userId, false);
        when(ccdFacade.getCcdHeaderPatient(eq(residentId), anyBoolean())).thenReturn(ccdHeaderPatientDto);

        // Execute the method being tested
        CcdHeaderPatientDto result = phrService.getUserDemographics(userId, true);

        // Validation
        assertThat(result, sameBeanAs(expectedCcdHeaderPatientDto));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId);
    }

    @Test
    public void testGetUserGuardiansInfo() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId, false);

        final Guardian guardian = new Guardian();
        guardian.setResident(user.getResident());
        user.getResident().setGuardians(Arrays.asList(guardian));

        final GuardianDto guardianDto = new GuardianDto();
        final CcdHeaderPatientDto expectedCcdHeaderPatientDto = new CcdHeaderPatientDto();
        expectedCcdHeaderPatientDto.setGuardians(Arrays.asList(guardianDto));

        // Mockito expectations
        when(ccdFacade.getCcdHeaderPatient(eq(residentId), anyBoolean())).thenReturn(expectedCcdHeaderPatientDto);
        when(residentDao.get(residentId)).thenReturn(user.getResident());

        // Execute the method being tested
        List<GuardianDto> result = phrService.getUserGuardiansInfo(userId);

        assertThat(result, sameBeanAs(Arrays.asList(guardianDto)));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId);
    }

    @Test
    public void testGetUserGuardiansInfoMerged() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId, true);

        final Guardian guardian = new Guardian();
        guardian.setResident(user.getResident());
        user.getResident().setGuardians(Arrays.asList(guardian));

        final GuardianDto guardianDto = new GuardianDto();
        final CcdHeaderPatientDto expectedCcdHeaderPatientDto = new CcdHeaderPatientDto();
        expectedCcdHeaderPatientDto.setGuardians(Arrays.asList(guardianDto));

        // Mockito expectations
        when(ccdFacade.getCcdHeaderPatient(eq(residentId), anyBoolean())).thenReturn(expectedCcdHeaderPatientDto);
        when(residentDao.get(residentId)).thenReturn(user.getResident());

        // Execute the method being tested
        List<GuardianDto> result = phrService.getUserGuardiansInfo(userId);

        assertThat(result, sameBeanAs(Arrays.asList(guardianDto)));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId);
    }

    @Test(expected = PhrException.class)
    public void testGetUserGuardiansInfoThrowsNotFoundPatientInfo() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId, false);
        user.setResidentId(null);
        user.setResident(null);

        // Mockito expectations
        when(userResidentRecordsDao.getActiveResidentIdsByUserId(userId)).thenReturn(Collections.<Long>emptyList());
        when(userResidentRecordsDao.getAllResidentIdsByUserId(userId)).thenReturn(Collections.<Long>emptyList());

        // Execute the method being tested
        phrService.getUserGuardiansInfo(userId);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme