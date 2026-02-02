package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.dto.twilio.TwilioConversationWebhookDto;
import com.scnsoft.eldermark.dto.twilio.TwilioRoomWebhookDto;
import com.scnsoft.eldermark.facade.TwilioWebhookFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/twilio/webhook")
public class TwilioWebhookController {

    @Autowired
    private TwilioWebhookFacade twilioWebhookFacade;

    @PostMapping(value = "/conversation", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Response<Void> conversationWebhook(@ModelAttribute TwilioConversationWebhookDto conversationWebhookDto) {
        twilioWebhookFacade.processConversationWebhook(conversationWebhookDto);
        return Response.successResponse();
    }

    @PostMapping(value = "/room", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Response<Void> roomWebhook(@ModelAttribute TwilioRoomWebhookDto roomWebhookDto) {
        twilioWebhookFacade.processRoomWebhook(roomWebhookDto);
        return Response.successResponse();
    }
}
