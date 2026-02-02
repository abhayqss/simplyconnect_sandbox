package com.scnsoft.eldermark.event.xml.service;

import com.scnsoft.eldermark.event.xml.dto.DeviceEventProcessingResultDto;
import com.scnsoft.eldermark.event.xml.schema.*;
import com.scnsoft.eldermark.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventsServiceImpl implements EventsService {

    private final EventOrganizationService eventOrganizationService;

    private final EventCommunityService eventCommunityService;

    private final EventClientService eventClientService;

    private final EventClientDeviceService eventClientDeviceService;

    private final Converter<Event, com.scnsoft.eldermark.entity.event.Event> eventEntityConverter;

    private final Converter<DeviceEvent, com.scnsoft.eldermark.entity.event.Event> deviceEventEntityConverter;

    private final EventService eventService;

    @Autowired
    public EventsServiceImpl(EventOrganizationService eventOrganizationService, EventCommunityService eventCommunityService, EventClientService eventClientService, EventClientDeviceService eventClientDeviceService, Converter<Event, com.scnsoft.eldermark.entity.event.Event> eventEntityConverter, Converter<DeviceEvent, com.scnsoft.eldermark.entity.event.Event> deviceEventEntityConverter, EventService eventService) {
        this.eventOrganizationService = eventOrganizationService;
        this.eventCommunityService = eventCommunityService;
        this.eventClientService = eventClientService;
        this.eventClientDeviceService = eventClientDeviceService;
        this.eventEntityConverter = eventEntityConverter;
        this.deviceEventEntityConverter = deviceEventEntityConverter;
        this.eventService = eventService;
    }

    @Override
    public void processEvents(Events events) {
        events.getEvent().forEach(this::processEvent);
    }

    private void processEvent(Event event) {
        var community = getOrCreateOrganizationAndCommunity(event.getOrganization(), event.getCommunity());
        var clients = eventClientService.getOrCreateClient(community, event.getPatient());

        for (var client : clients) {
            var eventEntity = eventEntityConverter.convert(event);
            if (eventEntity != null) {
                eventEntity.setClient(client);
                eventService.save(eventEntity);
            }
        }
    }

    @Override
    public DeviceEventProcessingResultDto processDeviceEvents(DeviceEvents deviceEvents) {
        DeviceEventProcessingResultDto result = new DeviceEventProcessingResultDto();
        for (DeviceEvent event : deviceEvents.getEvent()) {
            boolean processed = processDeviceEvent(event);
            if (processed) {
                result.addProcessed(event.getPatient().getDeviceID());
            } else {
                result.addFailed(event.getPatient().getDeviceID());
            }
        }
        return result;
    }

    private boolean processDeviceEvent(DeviceEvent deviceEvent) {
        var community = getOrCreateOrganizationAndCommunity(deviceEvent.getOrganization(), deviceEvent.getCommunity());
        var residentDevice = eventClientDeviceService.findByDeviceIdAndCommunity(deviceEvent.getPatient().getDeviceID(), community);

        if (residentDevice != null) {
            var eventEntity = deviceEventEntityConverter.convert(deviceEvent);
            if (eventEntity != null) {
                eventEntity.setClient(residentDevice.getClient());
                eventService.save(eventEntity);
            }
            return true;
        } else {
            return false;
        }
    }

    private com.scnsoft.eldermark.entity.community.Community getOrCreateOrganizationAndCommunity(Organization organization, Community community) {
        var org = eventOrganizationService.getOrCreateOrganizationFromSchema(organization);
        return eventCommunityService.getOrCreateCommunityFromSchema(org, community);
    }
}
