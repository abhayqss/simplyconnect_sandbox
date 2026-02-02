package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.entity.DirectErrorCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.services.direct.*;
import com.scnsoft.eldermark.shared.AddressBookSource;
import com.scnsoft.eldermark.shared.DirectMessageType;
import com.scnsoft.eldermark.shared.MessageDto;
import com.scnsoft.eldermark.shared.SesDirectoryAccountDto;
import com.scnsoft.eldermark.shared.exceptions.AccessToDirectMailboxDeniedException;
import com.scnsoft.eldermark.shared.exceptions.SendMessageException;
import com.scnsoft.eldermark.shared.exceptions.ToAddressValidationException;
import com.scnsoft.eldermark.shared.form.AddressBookFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.ws.soap.SOAPFaultException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class DirectMessagesFacadeImpl implements DirectMessagesFacade {
    private static final Logger logger = LoggerFactory.getLogger(DirectMessagesFacadeImpl.class);

    @Autowired
    private DirectMessagesService directMessagesService;

    @Autowired
    private EmployeeDao employeeDao;

    private static String EMAIL_REGEX = "\\b[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@" +
                                        "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\b";

    @Override
    public void sendMessage(List<String> recipients, String subject, String bodyHTML, List<DirectAttachment> attachments, DirectAccountDetails directAccountDetails) {
        try {
            directMessagesService.sendMessage(recipients, subject, bodyHTML, attachments, directAccountDetails);
        } catch (Exception e) {
            logger.warn("Exception during sending direct message", e);
            String errorDetail = e.getLocalizedMessage();
            // Should be removed after SES will fix error message
            if ("Error Sending message Error Sending message: No valid To or Cc email addresses".equals(errorDetail))
                errorDetail = "Error Sending message: To email address is invalid.";
            else if ("Error Sending message Can't send a blank message with no attachments or secure subject".equals(errorDetail))
                errorDetail = "Error Sending message: Can't send a blank message with no attachments or subject.";
            throw new SendMessageException(errorDetail);
        }
    }

    public void sendMessage(String allRecipients, String subject, String bodyHTML, List<DirectAttachment> attachments, DirectAccountDetails directAccountDetails) {
        List<String> recipients = new ArrayList<String>();
        for(String candidate : Arrays.asList(allRecipients.replaceAll(";", " ").trim().toLowerCase().split("\\s+"))) {
            if (!candidate.matches(EMAIL_REGEX)) {
                throw new ToAddressValidationException(candidate);
            }
            recipients.add(candidate);
        }

        sendMessage(recipients, subject, bodyHTML, attachments, directAccountDetails);
    }

    @Override
    public List<MessageDto> getInboxMessages(int offset, int limit, DirectMessageType state, DirectAccountDetails directAccountDetails) {
        try {
            return directMessagesService.getInboxMessages(offset, limit, state, directAccountDetails);
        } catch (RuntimeException e) {
            String errorDetail = e.getLocalizedMessage();
            if (errorDetail != null && errorDetail.startsWith("Error GetMessageList Request not Authorized for target")) {
                errorDetail = "Your secure mail box is not accessible via SimplyConnect, please check your messages via your secure messaging provider portal.";
            }
            throw new AccessToDirectMailboxDeniedException(errorDetail);
        }
    }

    @Override
    public List<MessageDto> getInboxMessages(DirectMessageType state, Pageable pageable, DirectAccountDetails directAccountDetails) {
        return getInboxMessages(pageable.getOffset(), pageable.getPageSize(), state, directAccountDetails);
    }

    @Override
    public MessageDto getInboxMessage(String messageId, DirectAccountDetails directAccountDetails, boolean reply) {
        return directMessagesService.getInboxMessage(messageId, directAccountDetails, reply);
    }

    @Override
    public Boolean deleteMessage(String messageId, DirectAccountDetails directAccountDetails) {
        return directMessagesService.deleteMessage(messageId, directAccountDetails);
    }

    @Override
    public Integer getInboxMessagesCount(DirectMessageType state, DirectAccountDetails directAccountDetails) {
        return directMessagesService.getInboxMessagesCount(state, directAccountDetails);
    }

    @Override
    public void getInboxMessageAttachment(String messageId, int partNumber, DownloadAttachmentCallback callback,
                                          DirectAccountDetails directAccountDetails) {
        directMessagesService.getInboxMessageAttachment(messageId, partNumber, callback, directAccountDetails);
    }

    @Override
    public Integer getUnreadMessagesCount(DirectAccountDetails directAccountDetails) {
        return directMessagesService.getUnreadMessagesCount(directAccountDetails);
    }

    @Override
    public void markMessageAsSeen(String messageId, DirectAccountDetails directAccountDetails) {
        directMessagesService.markMessageAsSeen(messageId, directAccountDetails);
    }

    @Override
    public Page<SesDirectoryAccountDto> directorySearch(AddressBookFilter filter, Pageable pageable, DirectAccountDetails directAccountDetails) {
        List<SesDirectoryAccountDto> directoryAccounts = Collections.EMPTY_LIST;

        if (filter.getAddressBookSource() == AddressBookSource.ELDERMARK_EXCHANGE_DIRECTORY) {
            directoryAccounts = directMessagesService.exchangeDirectorySearch(filter.getSecureEmail(), directAccountDetails);
        } else {
            try {
                directoryAccounts = directMessagesService.publicDirectorySearch(filter.getSecureEmail(), directAccountDetails);
            } catch (SOAPFaultException e) {
                if (!"There are zero search results for your search criteria, please search again with some other data!".equals(e.getMessage()))
                    throw e;
            }
        }

        List<SesDirectoryAccountDto> searchResult = directoryAccounts;

        int fromIndex = pageable.getOffset();
        int toIndex = pageable.getOffset() + pageable.getPageSize();
        toIndex = toIndex > directoryAccounts.size() ? directoryAccounts.size() : toIndex;

        if (fromIndex >= 0 && toIndex <= directoryAccounts.size() && fromIndex <= toIndex) {
            searchResult = directoryAccounts.subList(fromIndex, toIndex);
        }

        return new PageImpl<SesDirectoryAccountDto>(searchResult, pageable, directoryAccounts.size());
    }

    @Override
    @Transactional
    public boolean isSecureMessagingActive(Long employeeId) {
        Employee employee = employeeDao.get(employeeId);

        return employee.isSecureMessagingActive();
    }

    @Override
    @Transactional
    public void activateSecureMessaging(Long employeeId) {
        Employee employee = employeeDao.get(employeeId);

        employee.setSecureMessagingActive(true);
        employee.setSecureMessagingError(null);

        employeeDao.merge(employee);
    }

    @Override
    @Transactional
    public void deactivateSecureMessaging(Long employeeId, DirectErrorCode errorCode) {
        Employee employee = employeeDao.get(employeeId);

        employee.setSecureMessagingActive(false);
        employee.setSecureMessagingError(errorCode);

        employeeDao.merge(employee);
    }

    @Override
    @Transactional
    public void deactivateSecureMessaging(Long employeeId, DirectErrorCode errorCode, String message) {
        Employee employee = employeeDao.get(employeeId);

        employee.setSecureMessagingActive(false);
        employee.setSecureMessagingError(errorCode, message);

        employeeDao.merge(employee);
    }

    @Override
    public boolean isDirectAccountRegistered(DirectAccountDetails directAccountDetails) {
        return directMessagesService.isSecureEmailRegistered(directAccountDetails);
    }

    @Override
    @Transactional
    public void registerDirectAccount(Long employeeId, String companyCode) {
        Employee employee = employeeDao.get(employeeId);
        RegistrationRequestDto account = RegistrationRequestDto.createFromEmployee(employee);

        String actualSecureEmail = directMessagesService.registerDirectAccount(account, companyCode);

        employee.setSecureMessaging(actualSecureEmail);
        employee.setSecureMessagingActive(true);

        employeeDao.merge(employee);
        employeeDao.flush();
    }

    @Override
    @Transactional
    public boolean isValidForRegistration(Long employeeId) {
        Employee employee = employeeDao.get(employeeId);
        RegistrationRequestDto account = RegistrationRequestDto.createFromEmployee(employee);

        return account.isValid();
    }

    @Override
    @Async
    public void deleteDirectAccount(DirectAccountDetails directAccountDetails, String reason) {
        directMessagesService.deleteDirectAccount(directAccountDetails, reason);
    }
}
