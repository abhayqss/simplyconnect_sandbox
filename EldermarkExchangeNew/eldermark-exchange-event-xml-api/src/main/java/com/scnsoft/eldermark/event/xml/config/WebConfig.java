package com.scnsoft.eldermark.event.xml.config;

import com.scnsoft.eldermark.event.xml.schema.DeviceEvents;
import com.scnsoft.eldermark.event.xml.schema.Events;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.xml.bind.ValidationEventHandler;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${events.xsd.file}")
    private Resource eventsResource;

    @Value("${device.events.xsd.file}")
    private Resource deviceEventsResource;

    @Bean(name = "eventsUnmarshaler")
    public Jaxb2Marshaller eventsUnmarshaler() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setSchemas(eventsResource);
        marshaller.setValidationEventHandler(validationEventHandler());
        marshaller.setClassesToBeBound(Events.class);
        return marshaller;
    }

    @Bean(name = "deviceEventsUnmarshaler")
    public Jaxb2Marshaller deviceEventsUnmarshaler() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setSchemas(deviceEventsResource);
        marshaller.setValidationEventHandler(validationEventHandler());
        marshaller.setClassesToBeBound(DeviceEvents.class);
        return marshaller;
    }

    @Bean
    public ValidationEventHandler validationEventHandler() {
        return event -> {
            throw new UnmarshallingFailureException(event.getMessage(), event.getLinkedException());
        };
    }

    @Bean
    public HttpMessageConverter<Object> xmlHttpMessageConverter() {
        var xmlConverter = new MarshallingHttpMessageConverter();
        var marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("/com/scnsoft/eldermark/event/xml/response");
        xmlConverter.setMarshaller(marshaller);
        return xmlConverter;
    }
}
