package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.carecoordination.EventTypeDao;
import com.scnsoft.eldermark.entity.EventGroup;
import com.scnsoft.eldermark.entity.EventType;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.shared.web.entity.EventTypeDto;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 6/16/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class EventTypeServiceTest {

    @Mock
    private EventTypeDao eventTypeDao;

    @InjectMocks
    private EventTypeService eventTypeService;

    // Shared test data
    private final String[] EVENT_TYPE_CODES = {"SI", "ME", "AS", "MERR", "ARM"};
    private final String[] EVENT_TYPE_DESCRIPTIONS = {"Serious injury", "Medical emergency", "Suspected abuse", "Medication Errors", "Adverse Reaction to Medication"};
    final String eventTypeCode1 = TestDataGenerator.randomObjectFromList(EVENT_TYPE_CODES);
    final String eventTypeCode2 = TestDataGenerator.randomObjectFromList(EVENT_TYPE_CODES);
    private final Long eventTypeId = (TestDataGenerator.randomId(EVENT_TYPE_CODES.length + 1) - 1);

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nEvent type ID: %d\nEvent type code (1): %s\nEvent type code (2): %s\n\n",
                eventTypeId, eventTypeCode1, eventTypeCode2);
    }

    @Before
    public void injectDozer() {
        final DozerBeanMapper dozer = new DozerBeanMapper();
        eventTypeService.setDozer(dozer);
    }

    @Test
    public void testGetSortedEventTypes() {
        // Expected objects
        //final List<EventType> eventTypes = prepareEventTypes();
        final List<EventType> sortedEventTypes = new ArrayList<>(prepareEventTypes());
        Collections.sort(sortedEventTypes, new Comparator<EventType>() {
            @Override
            public int compare(EventType o1, EventType o2) {
                return o1.getDescription().compareTo(o2.getDescription());
            }
        });

        final Set<String> enabledEventTypes = new HashSet<>(Arrays.asList(eventTypeCode1, eventTypeCode2));
        final List<EventTypeDto> expectedEventTypes = prepareEventTypeDtos(enabledEventTypes);
        Collections.sort(expectedEventTypes, new Comparator<EventTypeDto>() {
            @Override
            public int compare(EventTypeDto o1, EventTypeDto o2) {
                return o1.getDescription().compareTo(o2.getDescription());
            }
        });

        // Mockito expectations
        when(eventTypeDao.list("description")).thenReturn(sortedEventTypes);
        //when(eventTypeDao.list(null)).thenReturn(eventTypes);

        // Execute the method being tested
        List<EventTypeDto> result = eventTypeService.getSortedEventTypes(enabledEventTypes);

        // Validation
        assertThat(result, hasSize(EVENT_TYPE_CODES.length));
        assertThat(result, contains(expectedEventTypes.toArray()));
    }

    @Test
    public void testGetEventTypes() {
        // Expected objects
        final List<EventType> eventTypes = prepareEventTypes();
        final List<EventTypeDto> expectedEventTypes = prepareEventTypeDtos(null);

        // Mockito expectations
        when(eventTypeDao.list(null)).thenReturn(eventTypes);

        // Execute the method being tested
        List<EventTypeDto> result = eventTypeService.getEventTypes();

        // Validation
        assertThat(result, hasSize(EVENT_TYPE_CODES.length));
        assertThat(result, contains(expectedEventTypes.toArray()));
    }

    @Test
    public void testGetById() {
        // Expected objects
        final EventType eventType = prepareEventType(eventTypeId.intValue());

        // Mockito expectations
        when(eventTypeDao.get(eventTypeId)).thenReturn(eventType);

        // Execute the method being tested
        EventType result = eventTypeService.getById(eventTypeId);

        // Validation
        assertThat(result, sameBeanAs(eventType));
    }

    // Utility methods

    private List<EventType> prepareEventTypes() {
        List<EventType> eventTypes = new ArrayList<>();
        for (int i = EVENT_TYPE_CODES.length - 1; i >= 0; --i) {
            EventType eventType = prepareEventType(i);
            eventTypes.add(eventType);
        }
        return eventTypes;
    }

    private EventType prepareEventType(int i) {
        EventGroup eventGroup = new EventGroup();
        eventGroup.setId((long) i);
        eventGroup.setPriority(i);
        EventType eventType = new EventType();
        eventType.setId((long) i);
        eventType.setCode(EVENT_TYPE_CODES[i]);
        eventType.setDescription(EVENT_TYPE_DESCRIPTIONS[i]);
        eventType.setEventGroup(eventGroup);
        return eventType;
    }

    private List<EventTypeDto> prepareEventTypeDtos(Set<String> enabledEventTypes) {
        List<EventTypeDto> eventTypes = new ArrayList<>();
        for (int i = EVENT_TYPE_CODES.length - 1; i >= 0; --i) {
            EventTypeDto eventType = prepareEventTypeDto(i, enabledEventTypes);
            eventTypes.add(eventType);
        }
        return eventTypes;
    }

    private EventTypeDto prepareEventTypeDto(int i, Set<String> enabledEventTypes) {
        EventTypeDto eventType = new EventTypeDto();
        eventType.setId((long) i);
        eventType.setCode(EVENT_TYPE_CODES[i]);
        eventType.setDescription(EVENT_TYPE_DESCRIPTIONS[i]);
        if (enabledEventTypes != null) {
            eventType.setEnabled(enabledEventTypes.contains(EVENT_TYPE_CODES[i]));
        }
        eventType.setGroupId((long) i);
        return eventType;
    }

}
