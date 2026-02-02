package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.ActivityDao;
import com.scnsoft.eldermark.dao.carecoordination.Responsibility;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.*;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.services.phr.EventActivityService;
import com.scnsoft.eldermark.services.phr.InvitationActivityService;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.ActivityDto;
import com.scnsoft.eldermark.web.entity.CallActivityDto;
import com.scnsoft.eldermark.web.entity.VideoActivityDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 6/13/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ActivityServiceTest {

    @Mock
    private ActivityDao activityDao;

    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;

    @InjectMocks
    private ActivityService activityService;

    @InjectMocks
    private EventActivityService eventActivityService;

    @InjectMocks
    private InvitationActivityService invitationActivityService;

    // Shared test data
    private final Long userId = TestDataGenerator.randomId();
    private final Long eventId = TestDataGenerator.randomId();
    private final Long eventTypeId = TestDataGenerator.randomId();
    private final Date now = new Date();
    private final Employee employee = new Employee();

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nEvent ID: %d\nEvent Type ID: %d\nNow: %s\n\n",
                userId, eventId, eventTypeId, now);
    }

    @Test
    public void testLogInvitationActivity() {
        // Expected objects
        final InvitationActivity.Status status = InvitationActivity.Status.SENT;

        final InvitationActivity expectedActivity = new InvitationActivity();
        expectedActivity.setEmployee(employee);
        expectedActivity.setStatus(status);
        expectedActivity.setPatientId(userId);

        // Mockito expectations
        when(activityDao.save(any(InvitationActivity.class))).then(returnsFirstArg());

        // Execute the method being tested
        InvitationActivity result = activityService.logInvitationActivity(userId, employee, status);

        // Validation
        assertNotNull(result);
        assertThat(result, sameBeanAs(expectedActivity)
                .ignoring("id").ignoring(Date.class).ignoring(User.class));
        verify(activityDao).save(result);
    }

    @Test
    public void testLogCallActivity() {
        // Expected objects
        final Integer duration = 300;
        final CallActivityDto activityDto = new CallActivityDto();
        activityDto.setCallType(ActivityDto.CallType.INCOMING);
        activityDto.setDuration(duration);
        activityDto.setDate(now.getTime());
        activityDto.setType(ActivityDto.Type.CALL);

        final CallActivity expectedActivity = new CallActivity();
        expectedActivity.setEmployee(employee);
        expectedActivity.setIncoming(Boolean.TRUE);
        expectedActivity.setDuration(duration);
        expectedActivity.setDate(now);
        expectedActivity.setPatientId(userId);

        // Mockito expectations
        when(activityDao.save(any(CallActivity.class))).then(returnsFirstArg());

        // Execute the method being tested
        Activity result = activityService.logCallActivity(userId, employee, activityDto);

        // Validation
        assertNotNull(result);
        assertThat(result, instanceOf(CallActivity.class));
        assertThat((CallActivity) result, sameBeanAs(expectedActivity)
                .ignoring("id").ignoring(User.class)
                .ignoring(Date.class));
        assertThat(result.getDate(), anyOf(greaterThan(now), equalTo(now)));
        verify(activityDao).save(result);
    }

    @Test
    public void testLogVideoActivity() {
        // Expected objects
        final Integer duration = 400;
        final VideoActivityDto activityDto = new VideoActivityDto();
        activityDto.setVideoType(ActivityDto.CallType.OUTGOING);
        activityDto.setDuration(duration);
        activityDto.setDate(now.getTime());
        activityDto.setType(ActivityDto.Type.VIDEO);

        final VideoActivity expectedActivity = new VideoActivity();
        expectedActivity.setEmployee(employee);
        expectedActivity.setIncoming(Boolean.FALSE);
        expectedActivity.setDuration(duration);
        expectedActivity.setDate(now);
        expectedActivity.setPatientId(userId);

        // Mockito expectations
        when(activityDao.save(any(VideoActivity.class))).then(returnsFirstArg());

        // Execute the method being tested
        Activity result = activityService.logCallActivity(userId, employee, activityDto);

        // Validation
        assertNotNull(result);
        assertThat(result, instanceOf(VideoActivity.class));
        assertThat((VideoActivity) result, sameBeanAs(expectedActivity)
                .ignoring("id").ignoring(User.class)
                .ignoring(Date.class));
        assertThat(result.getDate(), anyOf(greaterThan(now), equalTo(now)));
        verify(activityDao).save(result);
    }

    @Test
    public void testLogEventActivity() {
        // Expected objects
        final Long residentId = TestDataGenerator.randomId();
        final CareCoordinationResident resident = new CareCoordinationResident();
        resident.setId(residentId);
        final EventType eventType = new EventType();
        eventType.setId(eventTypeId);
        eventType.setCode("TE");
        eventType.setDescription("Test event type");
        final Event event = Event.Builder.anEvent()
                .withId(eventId)
                .withEventType(eventType)
                .withResident(resident)
                .build();
        final Responsibility responsibility = Responsibility.R;
        final EventNotification eventNotification = new EventNotification();
        eventNotification.setEvent(event);
        eventNotification.setEmployee(employee);
        eventNotification.setResponsibility(responsibility);

        final EventActivity expectedActivity = new EventActivity();
        expectedActivity.setEmployee(employee);
        expectedActivity.setEventId(eventId);
        expectedActivity.setEventTypeId(eventTypeId);
        expectedActivity.setEventType(eventType);
        expectedActivity.setPatientId(userId);
        expectedActivity.setResponsibility(responsibility);

        // Mockito expectations
        when(activityDao.save(any(EventActivity.class))).then(returnsFirstArg());
        List<Long> userIds = Collections.singletonList(userId);
        when(userResidentRecordsDao.getAllUserIdsByResidentId(residentId)).thenReturn(userIds);

        // Execute the method being tested
        List<EventActivity> eventActivities = eventActivityService.logEventActivity(eventNotification);

        // Validation
        assertThat(eventActivities.get(0), sameBeanAs(expectedActivity)
                .ignoring("id").ignoring(Date.class).ignoring(User.class));
        verify(activityDao).save(eventActivities.get(0));
        verify(activityDao, times(userIds.size())).save(any(EventActivity.class));
        verify(userResidentRecordsDao).getAllUserIdsByResidentId(residentId);
    }

    @Test
    public void testLogInvitationAcceptedActivity() {
        // Expected objects
        final InvitationActivity existingActivity = new InvitationActivity();
        existingActivity.setEmployee(employee);
        existingActivity.setStatus(InvitationActivity.Status.SENT);
        existingActivity.setPatientId(userId);

        final InvitationActivity expectedActivity = new InvitationActivity();
        expectedActivity.setEmployee(employee);
        expectedActivity.setStatus(InvitationActivity.Status.ACCEPTED);
        expectedActivity.setPatientId(userId);

        // Mockito expectations
        when(activityDao.save(any(InvitationActivity.class))).then(returnsFirstArg());
        List<InvitationActivity> existingActivities = Collections.singletonList(existingActivity);
        when(activityDao.findInvitationActivitiesByEmployeeAndStatus(employee, InvitationActivity.Status.SENT)).thenReturn(existingActivities);

        // Execute the method being tested
        List<InvitationActivity> invitationActivities = invitationActivityService.logInvitationAcceptedActivity(employee);

        // Validation
        assertThat(invitationActivities.get(0), sameBeanAs(expectedActivity)
                .ignoring("id").ignoring(Date.class).ignoring(User.class));
        verify(activityDao).save(invitationActivities.get(0));
        verify(activityDao, times(existingActivities.size())).save(any(InvitationActivity.class));
        verify(activityDao).findInvitationActivitiesByEmployeeAndStatus(employee, InvitationActivity.Status.SENT);
    }

    @Test
    public void testLogInvitationRejectedActivity() {
        // Expected objects
        final InvitationActivity existingActivity = new InvitationActivity();
        existingActivity.setEmployee(employee);
        existingActivity.setStatus(InvitationActivity.Status.SENT);
        existingActivity.setPatientId(userId);

        final InvitationActivity expectedActivity = new InvitationActivity();
        expectedActivity.setEmployee(employee);
        expectedActivity.setStatus(InvitationActivity.Status.REJECTED);
        expectedActivity.setPatientId(userId);

        // Mockito expectations
        when(activityDao.save(any(InvitationActivity.class))).then(returnsFirstArg());
        List<InvitationActivity> existingActivities = Collections.singletonList(existingActivity);
        when(activityDao.findInvitationActivitiesByEmployeeAndStatus(employee, InvitationActivity.Status.SENT)).thenReturn(existingActivities);

        // Execute the method being tested
        List<InvitationActivity> invitationActivities = invitationActivityService.logInvitationRejectedActivity(employee);

        // Validation
        assertThat(invitationActivities.get(0), sameBeanAs(expectedActivity)
                .ignoring("id").ignoring(Date.class).ignoring(User.class));
        verify(activityDao).save(invitationActivities.get(0));
        verify(activityDao, times(existingActivities.size())).save(any(InvitationActivity.class));
        verify(activityDao).findInvitationActivitiesByEmployeeAndStatus(employee, InvitationActivity.Status.SENT);
    }

    @Test
    public void testCountRecentActivity() {
        // Mockito expectations
        when(activityDao.countByPatientIdAndEmployee(userId, employee)).thenReturn(4L);

        // Execute the method being tested
        final Long result = activityService.countRecentActivity(userId, employee);

        // Validation
        assertEquals((Long) 4L, result);
    }

    @Test
    public void testGetRecentActivity() {
        final Activity activity = new InvitationActivity();

        // Mockito expectations
        when(activityDao.findByPatientIdAndEmployee(eq(userId), eq(employee), any(Pageable.class))).thenReturn(Collections.singletonList(activity));

        // Execute the method being tested
        final List<Activity> recentActivity = activityService.getRecentActivity(userId, employee, new PageRequest(0, 5));

        // Validation
        assertThat(recentActivity, hasSize(1));
        assertEquals(recentActivity.get(0), activity);
    }

}
