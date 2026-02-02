package com.scnsoft.eldermark.service.twilio;

import com.scnsoft.eldermark.beans.twilio.attributes.ConversationAttributes;
import com.scnsoft.eldermark.beans.twilio.attributes.MessageAttributes;
import com.scnsoft.eldermark.beans.twilio.attributes.ParticipantAttributes;
import com.scnsoft.eldermark.beans.twilio.attributes.UserAttributes;
import com.twilio.rest.conversations.v1.service.Conversation;
import com.twilio.rest.conversations.v1.service.User;
import com.twilio.rest.conversations.v1.service.conversation.Message;
import com.twilio.rest.conversations.v1.service.conversation.Participant;
import com.twilio.rest.conversations.v1.service.user.UserConversation;

import java.util.Optional;

public interface TwilioAttributeService {

    String build(Object attributeDto);

    ConversationAttributes parse(Conversation conversation);

    ConversationAttributes parse(UserConversation conversation);

    ParticipantAttributes parse(Participant participant);

    MessageAttributes parse(Message message);

    UserAttributes parse(User user);

    <T> Optional<T> parse(String attributes, Class<T> attributesClass);
}
