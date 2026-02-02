package com.scnsoft.eldermark.event.xml.converter;

import com.scnsoft.eldermark.entity.event.EventAddress;
import com.scnsoft.eldermark.event.xml.schema.*;
import com.scnsoft.eldermark.service.EventTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.xml.transform.StringResult;

import java.util.Optional;

@Component
public class EventEntityConverter extends AbstractEventEntityConverter implements Converter<Event, com.scnsoft.eldermark.entity.event.Event> {

    private static final Logger logger = LoggerFactory.getLogger(EventEntityConverter.class);

    private final Jaxb2Marshaller eventsMarshaller;

    @Autowired
    public EventEntityConverter(EventTypeService eventTypeService, Converter<Address, EventAddress> eventAddressEntityConverter, @Qualifier("eventsUnmarshaler") Jaxb2Marshaller eventsMarshaller) {
        super(eventTypeService, eventAddressEntityConverter);
        this.eventsMarshaller = eventsMarshaller;
    }

    @Override
    public com.scnsoft.eldermark.entity.event.Event convert(Event source) {
        var target = createAndFillEvent(source.getEventDetails());
        target.setOrganization(Optional.ofNullable(source.getOrganization()).map(Organization::getName).orElse(null));
        target.setCommunity(Optional.ofNullable(source.getCommunity()).map(Community::getName).orElse(null));
        target.setEventManager(Optional.ofNullable(source.getManager()).map(this::convertEventManager).orElse(null));
        target.setEventAuthor(Optional.ofNullable(source.getFormAuthor()).map(this::convertEventAuthor).orElse(null));
        target.setEventRn(Optional.ofNullable(source.getRN()).map(this::convertEventRN).orElse(null));
        pushContent(source, target);
        return target;
    }

    @Override
    protected void pushContent(Object event, com.scnsoft.eldermark.entity.event.Event eventEntity) {
        try {
            String xml = marshal(createEvents((Event) event));
            eventEntity.setEventContent(xml);
        } catch (UnmarshallingFailureException e) {
            eventEntity.setEventContent("undefined");
            logger.error("Error marshaling event", e);
        }
    }

    private Events createEvents(Event event) {
        var events = new Events();
        events.getEvent().add(event);
        return events;
    }

    private String marshal(Object event) {
        var result = new StringResult();
        synchronized (eventsMarshaller) {
            eventsMarshaller.marshal(event, result);
        }
        return result.toString();
    }
}
