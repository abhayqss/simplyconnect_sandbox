package com.scnsoft.eldermark.event.xml.facade;

import com.scnsoft.eldermark.event.xml.dto.DeviceEventProcessingResultDto;
import com.scnsoft.eldermark.event.xml.entity.EventsLog;
import com.scnsoft.eldermark.event.xml.schema.DeviceEvents;
import com.scnsoft.eldermark.event.xml.schema.Events;
import com.scnsoft.eldermark.event.xml.service.EventsLogService;
import com.scnsoft.eldermark.event.xml.service.EventsService;
import com.scnsoft.eldermark.service.SymmetricKeySqlServerService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.xml.transform.StringSource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

@Service
@Transactional
public class EventsFacadeImpl implements EventsFacade {

    private static final Logger logger = LoggerFactory.getLogger(EventsFacadeImpl.class);

    private final SymmetricKeySqlServerService symmetricKeySqlServerService;

    private final EventsLogService eventsLogService;

    private final Converter<HttpServletRequest, EventsLog> eventsLogConverter;

    private final Jaxb2Marshaller eventsMarshaller;

    private final Jaxb2Marshaller deviceEventsMarshaller;

    private final EventsService eventsService;

    @Autowired
    public EventsFacadeImpl(SymmetricKeySqlServerService symmetricKeySqlServerService, EventsLogService eventsLogService, Converter<HttpServletRequest, EventsLog> eventsLogConverter, @Qualifier("eventsUnmarshaler") Jaxb2Marshaller eventsMarshaller, @Qualifier("deviceEventsUnmarshaler") Jaxb2Marshaller deviceEventsMarshaller, EventsService eventsService) {
        this.symmetricKeySqlServerService = symmetricKeySqlServerService;
        this.eventsLogService = eventsLogService;
        this.eventsLogConverter = eventsLogConverter;
        this.eventsMarshaller = eventsMarshaller;
        this.deviceEventsMarshaller = deviceEventsMarshaller;
        this.eventsService = eventsService;
    }

    @Override
    public void processEvents(HttpServletRequest request) throws IOException {
        var now = System.currentTimeMillis();
        var body = IOUtils.toString(request.getInputStream(), Charset.defaultCharset());

        logger.info("New Events start processing: \n" + body + "\n Current Thread : " + Thread.currentThread().getId());

        saveLog(request, body);
        var events = unmarshalEvents(body);
        eventsService.processEvents(events);

        logger.info("Finish processing XML, takes " + (System.currentTimeMillis() - now) + "ms.");
    }

    @Override
    public DeviceEventProcessingResultDto processDeviceEvents(HttpServletRequest request) throws IOException {
        var now = System.currentTimeMillis();
        var body = IOUtils.toString(request.getInputStream(), Charset.defaultCharset());

        logger.info("New Device Events start processing: \n" + body + "\n Current Thread : " + Thread.currentThread().getId());

        saveLog(request, body);
        var deviceEvents = unmarshalDeviceEvents(body);
        var result = eventsService.processDeviceEvents(deviceEvents);

        logger.info("Finish processing XML, takes " + (System.currentTimeMillis() - now) + "ms.");

        return result;
    }

    private void saveLog(HttpServletRequest request, String body) {
        var eventsLog = eventsLogConverter.convert(request);
        Objects.requireNonNull(eventsLog).setMessage(body);
        eventsLogService.saveLog(eventsLog);
    }

    private Events unmarshalEvents(String xml) {
        synchronized (eventsMarshaller) {
            return (Events) eventsMarshaller.unmarshal(new StringSource(xml));
        }
    }

    private DeviceEvents unmarshalDeviceEvents(String xml) {
        synchronized (deviceEventsMarshaller) {
            return (DeviceEvents) deviceEventsMarshaller.unmarshal(new StringSource(xml));
        }
    }
}
