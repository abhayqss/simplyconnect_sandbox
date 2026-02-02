package com.scnsoft.eldermark.handler;

import org.springframework.oxm.UnmarshallingFailureException;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

/**
 * Created by pzhurba on 22-Sep-15.
 */

public class EventsJaxbValidationEventHandler implements ValidationEventHandler {
    @Override
    public boolean handleEvent(ValidationEvent event) {
        throw new UnmarshallingFailureException(event.getMessage(), event.getLinkedException());
    }
}
