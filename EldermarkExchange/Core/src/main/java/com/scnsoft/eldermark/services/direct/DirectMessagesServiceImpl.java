package com.scnsoft.eldermark.services.direct;

import com.scnsoft.eldermark.services.direct.ws.directory.*;
import com.scnsoft.eldermark.services.direct.ws.directory.StateLicense;
import com.scnsoft.eldermark.services.direct.ws.mail.*;
import com.scnsoft.eldermark.services.direct.ws.mail.ArrayOfSesAddress;
import com.scnsoft.eldermark.services.direct.ws.mail.SesAddress;
import com.scnsoft.eldermark.services.direct.ws.mail.State;
import com.scnsoft.eldermark.services.direct.ws.register.*;
import com.scnsoft.eldermark.services.direct.ws.register.Address;
import com.scnsoft.eldermark.services.direct.ws.register.RegistrationType;
import com.scnsoft.eldermark.shared.*;
import com.scnsoft.eldermark.shared.exceptions.DirectMessagingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.util.*;

@Component
public class DirectMessagesServiceImpl implements DirectMessagesService {
    private static int ELDERMARK_VENDOR_ID = 33;

    private static final Logger logger = LoggerFactory.getLogger(DirectMessagesServiceImpl.class);

    @Autowired
    private WebServiceClientFactory wsFactory;

    private @Value("${secure.email.uuid.prefix}") String uuidPrefix;

    @Override
    public void sendMessage(List<String> recipients, String subject, String bodyHTML, List<DirectAttachment> attachments, DirectAccountDetails directAccountDetails) {
        com.scnsoft.eldermark.services.direct.ws.mail.ObjectFactory objectFactory = new com.scnsoft.eldermark.services.direct.ws.mail.ObjectFactory();

        SesMessage message = objectFactory.createSesMessage();

        SesAddress fromAddress = objectFactory.createSesAddress();
        fromAddress.setAddress(objectFactory.createSesAddressAddress(directAccountDetails.getSecureEmail()));
        message.setFromAddress(objectFactory.createSesMessageFromAddress(fromAddress));

        ArrayOfSesAddress toAddressArray = objectFactory.createArrayOfSesAddress();
        for(String recipient : recipients) {
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

        ArrayOfSesAttachment sesAttachments = objectFactory.createArrayOfSesAttachment();
        for(DirectAttachment directAttachment : attachments) {
            SesAttachment sesAttachment = objectFactory.createSesAttachment();
            sesAttachment.setData(objectFactory.createSesAttachmentData(directAttachment.getData()));
            sesAttachment.setFileName(objectFactory.createSesAttachmentFileName(directAttachment.getFileName()));
            sesAttachments.getSesAttachment().add(sesAttachment);
        }
        message.setAttachments(objectFactory.createSesMessageAttachments(sesAttachments));

        String company = directAccountDetails.getCompany();
        SecureIntegrationServiceImap port = wsFactory.createMailPort(company);

        Long time = System.currentTimeMillis();
        port.sendMessage(message);
        logger.debug(String.format("WebService.sendMessage() : %sms", (System.currentTimeMillis() - time)));
    }

    @Override
    public List<MessageDto> getInboxMessages(int offset, int limit, DirectMessageType state, DirectAccountDetails directAccountDetails) {
        com.scnsoft.eldermark.services.direct.ws.mail.ObjectFactory objectFactory = new com.scnsoft.eldermark.services.direct.ws.mail.ObjectFactory();

        ReceiveRequest getMessageListRequest = objectFactory.createReceiveRequest();
        getMessageListRequest.setEmail(objectFactory.createReceiveRequestEmail(directAccountDetails.getSecureEmail()));
        if(state == DirectMessageType.SENT) {
            return Collections.emptyList();
        } else if (state == DirectMessageType.DELETED) {
            return Collections.emptyList();
        }

        String company = directAccountDetails.getCompany();
        SecureIntegrationServiceImap port = wsFactory.createMailPort(company);

        Long time = System.currentTimeMillis();
        ArrayOfMessageSummary getMessageListReturn = port.getMessageList(getMessageListRequest);
        logger.debug(String.format("WebService.getMessageList() : %sms", (System.currentTimeMillis() - time)));
        List<MessageSummary> summaryList = getMessageListReturn.getMessageSummary();

        ArrayOflong messageUids = objectFactory.createArrayOflong();
        for (int i = summaryList.size() - offset - 1; i >= 0 && messageUids.getLong().size() < limit; i--) {
            if (i >= summaryList.size() - offset - limit - 1) {
                messageUids.getLong().add(summaryList.get(i).getMessageUid());
            }
        }

        if (messageUids.getLong().isEmpty()) {
            return Collections.emptyList();
        }

        MessageHeaderListRequest headerListRequest = new MessageHeaderListRequest();
        headerListRequest.setEmail(objectFactory.createMessageHeaderListRequestEmail(directAccountDetails.getSecureEmail()));
        headerListRequest.setMessageUids(objectFactory.createMessageHeaderListRequestMessageUids(messageUids));

        List<MessageDto> messageDtoList = new ArrayList<MessageDto>(limit);
        time = System.currentTimeMillis();
        ArrayOfKeyValueOflongSesHeaderjnPsq3HB headerListResponse = port.getMessageHeaderList(headerListRequest);
        logger.debug(String.format("WebService.getMessageHeaderList() : %sms", (System.currentTimeMillis() - time)));
        for(ArrayOfKeyValueOflongSesHeaderjnPsq3HB.KeyValueOflongSesHeaderjnPsq3HB entry : headerListResponse.getKeyValueOflongSesHeaderjnPsq3HB()) {
            Long messageUid = entry.getKey();
            SesHeader sesHeader = entry.getValue();

            MessageDto messageDto = new MessageDto();
            messageDto.setMessageId(messageUid.toString());
            messageDto.setDate(sesHeader.getDateSent().toGregorianCalendar().getTime());
            messageDto.setSeen(State.READ.equals(sesHeader.getStateFlag()));
            messageDto.setFrom(sesHeader.getFromAddress().getValue().getAddress().getValue());

            List<String> to = new ArrayList<String>();
            for(SesAddress sesAddress : sesHeader.getToAddress().getValue().getSesAddress()) {
                to.add(sesAddress.getAddress().getValue());
            }
            messageDto.setTo(to);
            messageDto.setSubject(sesHeader.getPublicSubject().getValue());
            messageDto.setMessageType(state);

            messageDtoList.add(messageDto);
        }
        Collections.sort(messageDtoList);

        return messageDtoList;
    }

    public boolean deleteMessage(String messageId, DirectAccountDetails directAccountDetails) {
        com.scnsoft.eldermark.services.direct.ws.mail.ObjectFactory objectFactory = new com.scnsoft.eldermark.services.direct.ws.mail.ObjectFactory();

        ReceiveRequest request = objectFactory.createReceiveRequest();
        request.setEmail(objectFactory.createReceiveRequestEmail(directAccountDetails.getSecureEmail()));
        request.setMessageUid(new Long(messageId));

        String company = directAccountDetails.getCompany();
        SecureIntegrationServiceImap port = wsFactory.createMailPort(company);

        Long time = System.currentTimeMillis();
        Boolean deleted = port.deleteMessage(request);
        logger.debug(String.format("WebService.deleteMessage() : %sms", (System.currentTimeMillis() - time)));

        return deleted;
    }

    @Override
    public Integer getInboxMessagesCount(DirectMessageType state, DirectAccountDetails directAccountDetails) {
        com.scnsoft.eldermark.services.direct.ws.mail.ObjectFactory objectFactory = new com.scnsoft.eldermark.services.direct.ws.mail.ObjectFactory();

        ReceiveRequest getMessageListRequest = objectFactory.createReceiveRequest();
        getMessageListRequest.setEmail(objectFactory.createReceiveRequestEmail(directAccountDetails.getSecureEmail()));
        if(state == DirectMessageType.SENT) {
            return 0;
        } else if (state == DirectMessageType.DELETED) {
            return 0;
        }

        String company = directAccountDetails.getCompany();
        SecureIntegrationServiceImap port = wsFactory.createMailPort(company);
        Long time = System.currentTimeMillis();
        Integer result = port.getMessageNumber(getMessageListRequest);
        logger.debug(String.format("WebService.getMessageNumber() : %sms", (System.currentTimeMillis() - time)));

        return result;
    }

    @Override
    public MessageDto getInboxMessage(String messageId, DirectAccountDetails directAccountDetails, boolean reply) {
        com.scnsoft.eldermark.services.direct.ws.mail.ObjectFactory objectFactory = new com.scnsoft.eldermark.services.direct.ws.mail.ObjectFactory();

        ReceiveRequest getMessageRequest = objectFactory.createReceiveRequest();
        getMessageRequest.setEmail(objectFactory.createReceiveRequestEmail(directAccountDetails.getSecureEmail()));
        getMessageRequest.setMessageUid(new Long(messageId));

        String company = directAccountDetails.getCompany();

        SecureIntegrationServiceImap port = wsFactory.createMailPort(company);
        Long time = System.currentTimeMillis();
        SesMessage sesMessage = port.getMessage(getMessageRequest);
        logger.debug(String.format("WebService.getMessage() : %sms", (System.currentTimeMillis() - time)));

        MessageDto messageDto = new MessageDto();

        messageDto.setMessageId(messageId);
        messageDto.setDate(sesMessage.getDateSent().toGregorianCalendar().getTime());
        messageDto.setSeen(State.READ.equals(sesMessage.getStateFlag()));
        messageDto.setFrom(sesMessage.getFromAddress().getValue().getAddress().getValue());

        List<String> to = new ArrayList<String>();
        for(SesAddress sesAddress : sesMessage.getToAddress().getValue().getSesAddress()) {
            to.add(sesAddress.getAddress().getValue());
        }
        messageDto.setTo(to);

        messageDto.setSubject(sesMessage.getPublicSubject().getValue());

        messageDto.setBody(getBody(sesMessage,reply));

        List<MessageAttachmentDto> messageAttachmentDtoList = new ArrayList<MessageAttachmentDto>();
        List<SesAttachment> sesAttachments = sesMessage.getAttachments().getValue().getSesAttachment();
        for (int j = 0; j < sesAttachments.size(); j++) {
            SesAttachment sesAttachment = sesAttachments.get(j);
            MessageAttachmentDto messageAttachmentDto = new MessageAttachmentDto();
            messageAttachmentDto.setMessageId(messageId);
            messageAttachmentDto.setContentType(sesAttachment.getContentType().getValue());
            messageAttachmentDto.setName(sesAttachment.getFileName().getValue());
            messageAttachmentDto.setPartIndex(j);
            messageAttachmentDtoList.add(messageAttachmentDto);
        }
        messageDto.setAttachments(messageAttachmentDtoList);

        return messageDto;
    }

    private String getBody(SesMessage sesMessage, boolean reply) {
        String bodyHTML = sesMessage.getBodyHTML().getValue();

        if (StringUtils.isEmpty(bodyHTML) || (reply && bodyHTML.startsWith("<html>"))) {
            return sesMessage.getBodyText().getValue();
        }
        else  if (!reply && !bodyHTML.startsWith("<html>")) {
            return bodyHTML.replace("\r\n", "<br>");
        }
        return bodyHTML;
    }


    @Override
    public void getInboxMessageAttachment(String messageId, int partNumber, DownloadAttachmentCallback callback,
                                          DirectAccountDetails directAccountDetails) {
        com.scnsoft.eldermark.services.direct.ws.mail.ObjectFactory objectFactory = new com.scnsoft.eldermark.services.direct.ws.mail.ObjectFactory();

        ReceiveRequest getMessageRequest = objectFactory.createReceiveRequest();
        getMessageRequest.setEmail(objectFactory.createReceiveRequestEmail(directAccountDetails.getSecureEmail()));
        getMessageRequest.setMessageUid(new Long(messageId));

        String company = directAccountDetails.getCompany();
        SecureIntegrationServiceImap port = wsFactory.createMailPort(company);
        Long time = System.currentTimeMillis();
        SesMessage sesMessage = port.getMessage(getMessageRequest);
        logger.debug(String.format("WebService.getMessage() : %sms", (System.currentTimeMillis() - time)));

        List<SesAttachment> sesAttachments = sesMessage.getAttachments().getValue().getSesAttachment();

        SesAttachment attachmentToDownload = sesAttachments.get(partNumber);

        DirectAttachment directAttachment = new DirectAttachment();
        directAttachment.setContentType(attachmentToDownload.getContentType().getValue());
        directAttachment.setFileName(attachmentToDownload.getFileName().getValue());
        directAttachment.setData(attachmentToDownload.getData().getValue());

        try {
            callback.download(directAttachment);
        } catch (IOException e) {
            throw new DirectMessagingException(e);
        }
    }

    @Override
    public void markMessageAsSeen(String messageId, DirectAccountDetails directAccountDetails) {
        com.scnsoft.eldermark.services.direct.ws.mail.ObjectFactory objectFactory = new com.scnsoft.eldermark.services.direct.ws.mail.ObjectFactory();

        ReceiveRequest receiveRequest = objectFactory.createReceiveRequest();
        receiveRequest.setEmail(objectFactory.createReceiveRequestEmail(directAccountDetails.getSecureEmail()));
        receiveRequest.setMessageUid(new Long(messageId));
        receiveRequest.setStateFlag(State.READ);

        String company = directAccountDetails.getCompany();

        SecureIntegrationServiceImap port = wsFactory.createMailPort(company);
        Long time = System.currentTimeMillis();
        port.updateMessageStatus(receiveRequest);
        logger.debug(String.format("WebService.updateMessageStatus() : %sms", (System.currentTimeMillis() - time)));
    }

    @Override
    public Integer getUnreadMessagesCount(DirectAccountDetails directAccountDetails) {
        com.scnsoft.eldermark.services.direct.ws.mail.ObjectFactory objectFactory = new com.scnsoft.eldermark.services.direct.ws.mail.ObjectFactory();

        ReceiveRequest receiveRequest = objectFactory.createReceiveRequest();
        receiveRequest.setEmail(objectFactory.createReceiveRequestEmail(directAccountDetails.getSecureEmail()));

        String company = directAccountDetails.getCompany();
        SecureIntegrationServiceImap port = wsFactory.createMailPort(company);

        Long time = System.currentTimeMillis();
        int messageNumber = port.getMessageNumber(receiveRequest);
        logger.debug(String.format("WebService.getMessageNumber() : %sms", (System.currentTimeMillis() - time)));

        time = System.currentTimeMillis();
        int readMessageNumber = port.getReadMessageNumber(receiveRequest);
        logger.debug(String.format("WebService.getReadMessageNumber() : %sms", (System.currentTimeMillis() - time)));

        return messageNumber - readMessageNumber;
    }

    @Override
    public boolean isSecureEmailRegistered(DirectAccountDetails directAccountDetails) {
        com.scnsoft.eldermark.services.direct.ws.register.ObjectFactory objectFactory = new com.scnsoft.eldermark.services.direct.ws.register.ObjectFactory();

        SearchRequest request = objectFactory.createSearchRequest();

        JAXBElement<String> secureEmail = objectFactory.createRegistrationAPIModelSecureEmail(directAccountDetails.getSecureEmail());
        request.setSecureEmail(secureEmail);
        request.setVendorId(ELDERMARK_VENDOR_ID);

        RegistrationService port = wsFactory.createRegistrationPort(directAccountDetails.getCompany());

        Long time = System.currentTimeMillis();
        List<RegistrationAPIModel> matchesCriteria = port.searchRegistation(request).getRegistrationAPIModel();
        logger.debug(String.format("WebService.searchRegistation() : %sms", (System.currentTimeMillis() - time)));

        return matchesCriteria.size() == 1;
    }

    @Override
    public List<SesDirectoryAccountDto> publicDirectorySearch(String secureEmailFilter, DirectAccountDetails directAccountDetails) {
        com.scnsoft.eldermark.services.direct.ws.directory.ObjectFactory objectFactory = new com.scnsoft.eldermark.services.direct.ws.directory.ObjectFactory();

        PublicSearchRequest publicSearchRequest = objectFactory.createPublicSearchRequest();

        publicSearchRequest.setSecureEmail(objectFactory.createPublicSearchRequestSecureEmail(secureEmailFilter));
        publicSearchRequest.setIsProfessional(true);
        publicSearchRequest.setIsOrganization(true);
        publicSearchRequest.setIsIndividual(true);

        String company = directAccountDetails.getCompany();
        IDirectoryServices port = wsFactory.createDirectoryPort(company);

        ArrayOfPublicSearchResponse responsesArray = port.publicDirectorySearch(publicSearchRequest);
        List<SesDirectoryAccountDto> sesDirectoryAccountDtos = new ArrayList<SesDirectoryAccountDto>();

        for(PublicSearchResponse response : responsesArray.getPublicSearchResponse()) {
            if (response.getSecureEmail() != null && response.getSecureEmail().getValue() != null)
                sesDirectoryAccountDtos.add(mapToSesDirectoryAccount(response));
        }

        sortByName(sesDirectoryAccountDtos);

        return sesDirectoryAccountDtos;
    }

    @Override
    public List<SesDirectoryAccountDto> exchangeDirectorySearch(String secureEmailFilter, DirectAccountDetails directAccountDetails) {
        com.scnsoft.eldermark.services.direct.ws.register.ObjectFactory objectFactory = new com.scnsoft.eldermark.services.direct.ws.register.ObjectFactory();

        SearchRequest request = objectFactory.createSearchRequest();

        JAXBElement<String> secureEmail = objectFactory.createRegistrationAPIModelSecureEmail(secureEmailFilter);
        request.setSecureEmail(secureEmail);
        request.setVendorId(ELDERMARK_VENDOR_ID);

        String company = directAccountDetails.getCompany();
        RegistrationService port = wsFactory.createRegistrationPort(company);

        List<RegistrationAPIModel> registrationAPIModels = port.searchRegistation(request).getRegistrationAPIModel();

        List<SesDirectoryAccountDto> sesDirectoryAccountDtos = new ArrayList<SesDirectoryAccountDto>();

        for(RegistrationAPIModel response : registrationAPIModels) {
            if (response.getSecureEmail() != null && response.getSecureEmail().getValue() != null)
                sesDirectoryAccountDtos.add(mapToSesDirectoryAccount(response));
        }

        sortByName(sesDirectoryAccountDtos);

        return sesDirectoryAccountDtos;
    }

    @Override
    public String registerDirectAccount(RegistrationRequestDto account, String companyCode) {
        com.scnsoft.eldermark.services.direct.ws.register.ObjectFactory objectFactory = new com.scnsoft.eldermark.services.direct.ws.register.ObjectFactory();
        RegistrationAPIModel request = objectFactory.createRegistrationAPIModel();

        request.setSecureEmail(objectFactory.createRegistrationAPIModelSecureEmail(account.getSecureEmail()));

        request.setName(objectFactory.createRegistrationAPIModelName(account.getFullName()));
        request.setRegistrationType(RegistrationType.INDIVIDUAL);

        Address addressRequest = objectFactory.createAddress();
        addressRequest.setCity(objectFactory.createAddressCity(account.getCity()));
        addressRequest.setStreet(objectFactory.createAddressStreet(account.getStreet()));
        addressRequest.setState(objectFactory.createAddressState(account.getState()));
        addressRequest.setZIP(objectFactory.createAddressZIP(account.getZip()));
        addressRequest.setPhone(objectFactory.createAddressPhone(account.getPhone()));
        request.setContactEmail(objectFactory.createRegistrationAPIModelContactEmail(account.getContactEmail()));
        request.setPrimaryAddress(objectFactory.createRegistrationAPIModelPrimaryAddress(addressRequest));

        request.setVendorId(ELDERMARK_VENDOR_ID);

        String randomEmployeeId = String.format("%s_%s_%s", uuidPrefix, account.getEmployeeId(), UUID.randomUUID().toString());
        request.setExternalUID(objectFactory.createRegistrationAPIModelExternalUID(randomEmployeeId));

        RegistrationService port = wsFactory.createRegistrationPort(companyCode);
        RegistrationAPIResults results = port.register(request);

        return results.getSecureEmail().getValue();
    }

    @Override
    public void deleteDirectAccount(DirectAccountDetails directAccountDetails, String reason) {
        RegistrationService port = wsFactory.createRegistrationPort(directAccountDetails.getCompany());
        port.deleteRegisteredAccount(directAccountDetails.getSecureEmail(), ELDERMARK_VENDOR_ID, reason);
    }

    private SesDirectoryAccountDto mapToSesDirectoryAccount(PublicSearchResponse response) {
        SesDirectoryAccountDto sesDirectoryAccountDto = new SesDirectoryAccountDto();

        if(response.getSecureEmail() != null)
            sesDirectoryAccountDto.setEmail(response.getSecureEmail().getValue());

        String title = null;
        if(response.getTitle() != null)
            title = response.getTitle().getValue();
        String name = "";
        if(response.getName() != null)
            name = response.getName().getValue();
        String titleAndName = title != null ? title + " " + name : name;

        sesDirectoryAccountDto.setName(titleAndName);

        if(response.getSpecialty() != null)
            sesDirectoryAccountDto.setSpeciality(response.getSpecialty().getValue());

        if(response.getNPINumber() != null && response.getNPINumber().getValue() != null)
            sesDirectoryAccountDto.setNpiNumbers(response.getNPINumber().getValue().getString());

        List<String> stateLicenses = new ArrayList<String>();
        if(response.getStateLicenses() != null && response.getStateLicenses().getValue() != null) {
            for(StateLicense stateLicense : response.getStateLicenses().getValue().getStateLicense()) {
                if(!stateLicense.getState().isNil())
                    stateLicenses.add(stateLicense.getState().getValue());
            }
        }
        sesDirectoryAccountDto.setStateLicences(stateLicenses);

        if(response.getRegistrationType() != null)
            sesDirectoryAccountDto.setRegistrationType(response.getRegistrationType().value());

        return sesDirectoryAccountDto;
    }

    private SesDirectoryAccountDto mapToSesDirectoryAccount(RegistrationAPIModel response) {
        SesDirectoryAccountDto sesDirectoryAccountDto = new SesDirectoryAccountDto();

        if(response.getSecureEmail() != null)
            sesDirectoryAccountDto.setEmail(response.getSecureEmail().getValue());

        String title = null;
        if(response.getTitle() != null)
            title = response.getTitle().getValue();
        String name = "";
        if(response.getName() != null)
            name = response.getName().getValue();
        String titleAndName = title != null ? title + " " + name : name;

        sesDirectoryAccountDto.setName(titleAndName);

        if(response.getSpecialty() != null)
            sesDirectoryAccountDto.setSpeciality(response.getSpecialty().getValue());

        if(response.getNPINumber() != null && response.getNPINumber().getValue() != null)
            sesDirectoryAccountDto.setNpiNumbers(response.getNPINumber().getValue().getString());

        List<String> stateLicenses = new ArrayList<String>();
        for(com.scnsoft.eldermark.services.direct.ws.register.StateLicense stateLicense : response.getStateLicenses().getValue().getStateLicense()) {
            if(!stateLicense.getState().isNil())
                stateLicenses.add(stateLicense.getState().getValue());
        }
        sesDirectoryAccountDto.setStateLicences(stateLicenses);

        if(response.getRegistrationType() != null)
            sesDirectoryAccountDto.setRegistrationType(response.getRegistrationType().value());

        return sesDirectoryAccountDto;
    }

    private void sortByName(List<SesDirectoryAccountDto> listToSort) {
        Collections.sort(listToSort, new Comparator<SesDirectoryAccountDto>(){
            public int compare(SesDirectoryAccountDto o1, SesDirectoryAccountDto o2) {
                String name1 = o1.getName();
                String name2 = o2.getName();
                if (name1 != null && name2 != null)
                    return name1.compareToIgnoreCase(name2);
                else if (name1 != null && name2 == null)
                    return -1;
                else if (name1 == null && name2 != null)
                    return 1;
                else
                    return 0;
            }
        });
    }
}