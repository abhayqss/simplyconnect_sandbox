package com.scnsoft.eldermark.service;

import java.util.List;

public interface DirectMessagesService {
    void sendMessage(List<String> recipients, String subject, String bodyHTML, List<DirectAttachment> attachments, DirectAccountDetails directAccountDetails);
}