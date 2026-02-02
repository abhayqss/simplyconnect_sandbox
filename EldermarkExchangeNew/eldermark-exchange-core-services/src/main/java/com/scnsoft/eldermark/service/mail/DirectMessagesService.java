package com.scnsoft.eldermark.service.mail;

import java.util.List;

import com.scnsoft.eldermark.service.DirectAccountDetails;
import com.scnsoft.eldermark.service.DirectAttachment;

public interface DirectMessagesService {
    void sendMessage(List<String> recipients, String subject, String bodyHTML, List<DirectAttachment> attachments,
            DirectAccountDetails directAccountDetails);

}