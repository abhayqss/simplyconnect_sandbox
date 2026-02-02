package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.EventAuthorDto;
import com.scnsoft.eldermark.api.external.web.dto.EventCreateDto;
import com.scnsoft.eldermark.api.shared.dto.EventDetailsDto;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import com.scnsoft.eldermark.api.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.api.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.service.EventService;
import org.dozer.DozerBeanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventsServiceTest {
    @Mock
    private ResidentsService residentsService;
    @Mock
    private EventService eventService;

    @InjectMocks
    private EventsServiceImpl eventsService;

    @BeforeEach
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
    protected final Client resident = new Client(residentId);

    @Test
    @Disabled("Implement EventCreateDto->Event conversion first")
    public void testCreate() {
        final Long expectedEventId = TestDataGenerator.randomId();
        final Event persisted = new Event();
        persisted.setId(expectedEventId);
        final EventCreateDto newEventDto = prepareEventCreateDto();

        // Mockito expectations
        when(eventService.save(any(Event.class))).thenReturn(persisted);

        // Execute the method being tested
        final Long actualEventId = eventsService.create(residentId, newEventDto);

        // Validation
        assertEquals(expectedEventId, actualEventId);
        verify(residentsService).checkAccessOrThrow(residentId);
    }

    @Test
    public void testCreateThrowsAccessForbidden() {
        final Event persisted = new Event();
        persisted.setId(1L);
        final EventCreateDto newEventDto = prepareEventCreateDto();

        // Mockito expectations
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN)).when(residentsService).checkAccessOrThrow(residentId);

        // Execute the method being tested
        assertThrows(PhrException.class, () -> eventsService.create(residentId, newEventDto));

        verifyNoInteractions(eventService);
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
