package com.scnsoft.eldermark.service.mail;

import com.scnsoft.eldermark.service.DirectAccountDetails;
import com.scnsoft.eldermark.service.DirectAttachment;
import com.scnsoft.eldermark.service.WebServiceClientFactory;
import com.scnsoft.eldermark.services.direct.ws.mail.*;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DirectMessagesServiceImpl implements DirectMessagesService {

    private static final Logger logger = LoggerFactory.getLogger(DirectMessagesServiceImpl.class);

    @Autowired
    private WebServiceClientFactory wsFactory;

    @Override
    public void sendMessage(List<String> recipients, String subject, String bodyHTML,
                            List<DirectAttachment> attachments, DirectAccountDetails directAccountDetails) {
        ObjectFactory objectFactory = new ObjectFactory();

        SesMessage message = objectFactory.createSesMessage();

        SesAddress fromAddress = objectFactory.createSesAddress();
        fromAddress.setAddress(objectFactory.createSesAddressAddress(directAccountDetails.getSecureEmail()));
        message.setFromAddress(objectFactory.createSesMessageFromAddress(fromAddress));

        ArrayOfSesAddress toAddressArray = objectFactory.createArrayOfSesAddress();
        for (String recipient : recipients) {
            SesAddress toAddress = objectFactory.createSesAddress();
            toAddress.setAddress(objectFactory.createSesAddressAddress(recipient));
            toAddressArray.getSesAddress().add(toAddress);
        }
        message.setToAddress(objectFactory.createSesMessageToAddress(toAddressArray));

        message.setBodyHTML(objectFactory.createSesMessageBodyHTML(bodyHTML));

        message.setSecureSubject(objectFactory.createSesMessageSecureSubject(subject));

        String msgid = UUID.randomUUID().toString().replaceAll("-", "");
        message.setMessageId(objectFactory.createSesMessageMessageId(msgid));
        message.setSESMessageId(objectFactory.createSesMessageSESMessageId(msgid));

        if (CollectionUtils.isNotEmpty(attachments)) {
            ArrayOfSesAttachment sesAttachments = objectFactory.createArrayOfSesAttachment();
            for (DirectAttachment directAttachment : attachments) {
                SesAttachment sesAttachment = objectFactory.createSesAttachment();
                sesAttachment.setData(objectFactory.createSesAttachmentData(directAttachment.getData()));
                sesAttachment.setFileName(objectFactory.createSesAttachmentFileName(directAttachment.getFileName()));
                sesAttachments.getSesAttachment().add(sesAttachment);
            }
            message.setAttachments(objectFactory.createSesMessageAttachments(sesAttachments));
        }

        String company = directAccountDetails.getCompany();
        System.setProperty("javax.xml.soap.SAAJMetaFactory", "com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl");
        SecureIntegrationServiceImap port = wsFactory.createMailPort(company);
        if (port == null) {
            logger.error("Error: SecureIntegrationServiceImap Port is Null");
            return;
        }

        Long time = System.currentTimeMillis();
        port.sendMessage(message);
        logger.debug("WebService.sendMessage() : {} ms", System.currentTimeMillis() - time);
    }

}
