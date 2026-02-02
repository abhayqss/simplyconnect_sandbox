package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.twilio.TwilioConversationWebhookDto;
import com.scnsoft.eldermark.dto.twilio.TwilioRoomWebhookDto;

public interface TwilioWebhookFacade {

    void processConversationWebhook(TwilioConversationWebhookDto conversationWebhookDto);

    void processRoomWebhook(TwilioRoomWebhookDto roomWebhookDto);
}
