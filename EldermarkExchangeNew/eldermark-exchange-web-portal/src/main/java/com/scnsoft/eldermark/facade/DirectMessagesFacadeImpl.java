package com.scnsoft.eldermark.facade;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.service.DirectAccountDetails;
import com.scnsoft.eldermark.service.DirectAttachment;
import com.scnsoft.eldermark.service.mail.DirectMessagesService;

@Component
public class DirectMessagesFacadeImpl implements DirectMessagesFacade {

    @Autowired
    private DirectMessagesService directMessagesService;

    @Override
    public void sendMessage(List<String> recipients, String subject, String bodyHTML,
            List<DirectAttachment> attachments, DirectAccountDetails directAccountDetails) {
        try {
            directMessagesService.sendMessage(recipients, subject, bodyHTML, attachments, directAccountDetails);
        } catch (Exception e) {
            String errorDetail = e.getLocalizedMessage();
            // Should be removed after SES will fix error message
            if ("Error Sending message Error Sending message: No valid To or Cc email addresses".equals(errorDetail))
                errorDetail = "Error Sending message: To email address is invalid.";
            else if ("Error Sending message Can't send a blank message with no attachments or secure subject"
                    .equals(errorDetail))
                errorDetail = "Error Sending message: Can't send a blank message with no attachments or subject.";
            // TODO manage exception throw new SendMessageException(errorDetail);
        }
    }

}
