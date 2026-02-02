package com.scnsoft.eldermark.service.twilio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessage;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
class JsonServiceMessageEncoder implements ServiceMessageEncoder {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String encode(ServiceMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new InternalServerException(InternalServerExceptionType.TWILIO_ERROR, e);
        }
    }
}
