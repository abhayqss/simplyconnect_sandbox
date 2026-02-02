package com.scnsoft.eldermark.service.twilio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.beans.twilio.attributes.ConversationAttributes;
import com.scnsoft.eldermark.beans.twilio.attributes.MessageAttributes;
import com.scnsoft.eldermark.beans.twilio.attributes.ParticipantAttributes;
import com.scnsoft.eldermark.beans.twilio.attributes.UserAttributes;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.twilio.rest.conversations.v1.service.Conversation;
import com.twilio.rest.conversations.v1.service.User;
import com.twilio.rest.conversations.v1.service.conversation.Message;
import com.twilio.rest.conversations.v1.service.conversation.Participant;
import com.twilio.rest.conversations.v1.service.user.UserConversation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TwilioAttributeServiceImpl implements TwilioAttributeService {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String build(Object attributeDto) {
        try {
            return objectMapper.writeValueAsString(attributeDto);
        } catch (JsonProcessingException e) {
            throw new InternalServerException(InternalServerExceptionType.TWILIO_ERROR, e);
        }
    }

    @Override
    public ConversationAttributes parse(Conversation conversation) {
        return this.parse(conversation.getAttributes(), ConversationAttributes.class)
                .orElseGet(ConversationAttributes::new);
    }

    @Override
    public ConversationAttributes parse(UserConversation conversation) {
        return this.parse(conversation.getAttributes(), ConversationAttributes.class)
                .orElseGet(ConversationAttributes::new);
    }

    @Override
    public ParticipantAttributes parse(Participant participant) {
        return this.parse(participant.getAttributes(), ParticipantAttributes.class)
                .orElseGet(ParticipantAttributes::new);
    }

    @Override
    public MessageAttributes parse(Message message) {
        return this.parse(message.getAttributes(), MessageAttributes.class)
                .orElseGet(MessageAttributes::new);
    }

    @Override
    public UserAttributes parse(User user) {
        return this.parse(user.getAttributes(), UserAttributes.class)
                .orElseGet(UserAttributes::new);
    }

    @Override
    public <T> Optional<T> parse(String attributeStr, Class<T> clazz) {
        if (StringUtils.isBlank(attributeStr)) return Optional.empty();
        try {
            return Optional.ofNullable(objectMapper.readValue(attributeStr, clazz));
        } catch (JsonProcessingException e) {
            throw new InternalServerException(InternalServerExceptionType.TWILIO_ERROR, e);
        }
    }
}
