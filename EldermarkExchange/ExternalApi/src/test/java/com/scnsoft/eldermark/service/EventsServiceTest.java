package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.carecoordination.EventService;
import com.scnsoft.eldermark.shared.carecoordination.EventDetailsDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.EventAuthorDto;
import com.scnsoft.eldermark.web.entity.EventCreateDto;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 4/14/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class EventsServiceTest {
    @Mock
    private ResidentsService residentsService;
    @Mock
    private EventService eventService;

    @InjectMocks
    private EventsService eventsService;

    @Before
    public void injectDozer() {
        final DozerBeanMapper dozer = new DozerBeanMapper();
        eventsService.setDozer(dozer);
    }

    /**
     * Current user ID
     */
    protected final Long userId = TestDataGenerator.randomId();
    /**
     * Current user access token
     */
    protected final Long residentId = TestDataGenerator.randomId();
    protected final Resident resident = new Resident(residentId);

    @Test
    public void testCreate() throws Exception {
        final Long expectedEventId = TestDataGenerator.randomId();
        final Event persisted = new Event();
        persisted.setId(expectedEventId);
        final EventCreateDto newEventDto = prepareEventCreateDto();

        // Mockito expectations
        when(eventService.processManualEvent(any(EventDto.class))).thenReturn(persisted);

        // Execute the method being tested
        final Long actualEventId = eventsService.create(residentId, newEventDto);

        // Validation
        assertEquals(expectedEventId, actualEventId);
        verify(residentsService).checkAccessOrThrow(residentId);
    }

    @Test(expected = PhrException.class)
    public void testCreateThrowsAccessForbidden() throws Exception {
        final Event persisted = new Event();
        persisted.setId(1L);
        final EventCreateDto newEventDto = prepareEventCreateDto();

        // Mockito expectations
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN)).when(residentsService).checkAccessOrThrow(residentId);
        when(eventService.processManualEvent(any(EventDto.class))).thenReturn(persisted);

        // Execute the method being tested
        eventsService.create(residentId, newEventDto);
    }

    private static EventCreateDto prepareEventCreateDto() {
        final EventAuthorDto authorDto = new EventAuthorDto();
        authorDto.setFirstName(TestDataGenerator.randomName());
        authorDto.setLastName(TestDataGenerator.randomName());
        authorDto.setRoleId(TestDataGenerator.randomId(10));
        final EventDetailsDto detailsDto = new EventDetailsDto();
        detailsDto.setEmergencyVisit(false);
        detailsDto.setOvernightPatient(false);
        detailsDto.setEventDatetime(TestDataGenerator.randomDate());
        detailsDto.setEventTypeId(TestDataGenerator.randomId(20));
        final EventCreateDto newEventDto = new EventCreateDto();
        newEventDto.setAuthor(authorDto);
        newEventDto.setEventDetails(detailsDto);
        return newEventDto;
    }


}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme