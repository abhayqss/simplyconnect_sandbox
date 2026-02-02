package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.DirectErrorCode;
import com.scnsoft.eldermark.facades.DirectConfigurationFacade;
import com.scnsoft.eldermark.facades.DirectMessagesFacade;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.services.direct.DirectAccountDetails;
import com.scnsoft.eldermark.services.direct.DirectAttachment;
import com.scnsoft.eldermark.services.direct.DownloadAttachmentCallback;
import com.scnsoft.eldermark.shared.AddressBookSource;
import com.scnsoft.eldermark.shared.DirectMessageType;
import com.scnsoft.eldermark.shared.MessageDto;
import com.scnsoft.eldermark.shared.exceptions.DirectAccountNotRegisteredException;
import com.scnsoft.eldermark.shared.exceptions.FileIOException;
import com.scnsoft.eldermark.shared.form.AddressBookFilter;
import com.scnsoft.eldermark.shared.form.ComposeMessageForm;
import com.scnsoft.eldermark.shared.form.MessageFilter;
import com.scnsoft.eldermark.web.resolvers.MessagingAccount;
import com.scnsoft.eldermark.web.session.SecureMessagingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//@Controller
//@RequestMapping(value = "/secure-messaging")
//@PreAuthorize(SecurityExpressions.IS_EXCHANGE_USER)
public class MessagingController {

    @Autowired
    private DirectMessagesFacade directMessagesFacade;

    @Autowired
    private DirectConfigurationFacade directConfigurationFacade;

    @Autowired
    private SecureMessagingConfig messagingConfig;

    @Autowired
    private EmployeeService employeeService;

    private static String REPLY_TO_PREFIX = "RE: ";

    @ModelAttribute("messageTypeValues")
    public DirectMessageType[] getMessageTypes() {
        DirectMessageType[] values = {DirectMessageType.INBOX};
        return values;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String initView(@AuthenticationPrincipal ExchangeUserDetails userDetails, Model model) {
        if (!messagingConfig.isAccountRegistered(userDetails)) {
            return "forward:/secure-messaging/config-warning";
        }

        MessageFilter messageFilter = new MessageFilter();
        messageFilter.setMessageType(DirectMessageType.INBOX);
        model.addAttribute("msgFilter", messageFilter);

        return "messaging.view";
    }

    @RequestMapping(value = "/unread-inbox-count", method = RequestMethod.GET)
    @ResponseBody
    public Integer getUnreadInboxCount(@AuthenticationPrincipal ExchangeUserDetails userDetails,
                                       @MessagingAccount DirectAccountDetails messagingAccount) {
        if (!messagingConfig.isAccountRegistered(userDetails)) {
            return 0;
        }

        try {
            return directMessagesFacade.getUnreadMessagesCount(messagingAccount);
        } catch (Exception e) {
            return 0;
        }
    }

    @RequestMapping(value = "/employee-secure-email", method = RequestMethod.GET)
    @ResponseBody
    public String getEmployeeSecureEmail(@AuthenticationPrincipal ExchangeUserDetails userDetails,
                                         @MessagingAccount DirectAccountDetails messagingAccount) {
        if (!messagingConfig.isAccountRegistered(userDetails)) {
            return "Secure Messaging is not setup";
        }

        try {
            return messagingAccount.getSecureEmail();
        } catch (Exception e) {
            return "Secure Messaging is not setup";
        }
    }

    @RequestMapping(value = "/employee-secure-email-activated", method = RequestMethod.GET)
    @ResponseBody
    public Boolean getEmployeeSecureEmailActivated(@AuthenticationPrincipal ExchangeUserDetails userDetails,
                                         @MessagingAccount DirectAccountDetails messagingAccount) {
        return messagingConfig.isAccountRegistered(userDetails);
    }

    @RequestMapping(value = "/results", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<MessageDto> mailbox(@ModelAttribute("msgFilter") MessageFilter filter,
                                    Pageable pageRequest,
                                    @MessagingAccount DirectAccountDetails messagingAccount) {

        List<MessageDto> result = directMessagesFacade.getInboxMessages(filter.getMessageType(), pageRequest, messagingAccount);
        long totalCount = directMessagesFacade.getInboxMessagesCount(filter.getMessageType(), messagingAccount);

        return new PageImpl<MessageDto>(result, pageRequest, totalCount);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.GET)
    public String getMessageDetails(@PathVariable(value = "messageId") String messageId,
                                    Model model,
                                    @MessagingAccount DirectAccountDetails messagingAccount) {
        model.addAttribute("message", directMessagesFacade.getInboxMessage(messageId, messagingAccount, false));
        return "messaging.details.view";
    }

    @RequestMapping(value = "/{messageId}/seen", method = RequestMethod.GET)
    @ResponseBody
    public void getMessageDetails(@PathVariable(value = "messageId") String messageId,
                                  @MessagingAccount DirectAccountDetails messagingAccount) {
        directMessagesFacade.markMessageAsSeen(messageId, messagingAccount);
    }

    @RequestMapping(value = "/{messageId}/attachment", method = RequestMethod.GET)
    public void getMessageAttachment(@PathVariable(value = "messageId") String messageId,
                                     @RequestParam(value = "partIndex") Integer partIndex,
                                     @MessagingAccount DirectAccountDetails messagingAccount,
                                     final HttpServletResponse response) {

        directMessagesFacade.getInboxMessageAttachment(messageId, partIndex, new DownloadAttachmentCallback() {
            @Override
            public void download(DirectAttachment directAttachment) throws IOException {
                boolean isViewMode = false;
                String openType = isViewMode ? "inline" : "attachment";

                response.setContentType(directAttachment.getContentType());
                response.setHeader("Content-Disposition", openType + ";filename=\"" + directAttachment.getFileName() + "\"");
                try {
                    FileCopyUtils.copy(directAttachment.getData(), response.getOutputStream());
                } catch (IOException e) {
                    throw new FileIOException("Failed to save file " + directAttachment.getFileName(), e);
                }
            }
        }, messagingAccount);
    }

    @RequestMapping(value = "/{messageId}/delete", method = RequestMethod.GET)
    @ResponseBody
    public void deleteMessage(@PathVariable(value = "messageId") String messageId,
                              @MessagingAccount DirectAccountDetails messagingAccount) {

        directMessagesFacade.deleteMessage(messageId, messagingAccount);
    }

    @RequestMapping(value = "/{messageId}/reply-to", method = RequestMethod.GET)
    public String initReplyToView(@PathVariable(value = "messageId") String messageId,
                                  @MessagingAccount DirectAccountDetails messagingAccount,
                                  Model model) {

        MessageDto replyToMessage = directMessagesFacade.getInboxMessage(messageId, messagingAccount, true);
        MessageDto newMessage = new MessageDto();

        String newSubject = replyToMessage.getSubject();
        if (newSubject != null) {
            newMessage.setSubject(newSubject.startsWith(REPLY_TO_PREFIX) ? newSubject : REPLY_TO_PREFIX + newSubject);
        }

        List<String> to = new ArrayList<String>();
        to.add(replyToMessage.getFrom());
        newMessage.setTo(to);

        newMessage.setBody("\n-----Original Message-----\n" + replyToMessage.getBody());

        model.addAttribute("msgDetails", newMessage);

        AddressBookFilter addressBookFilter = new AddressBookFilter();
        addressBookFilter.setAddressBookSource(AddressBookSource.ELDERMARK_EXCHANGE_DIRECTORY);
        model.addAttribute("addressBookFilter", addressBookFilter);

        model.addAttribute("sendMessageUrlTemplate", "/secure-messaging/send");

        return "secure.messaging.compose";
    }

    @RequestMapping(value = "/compose", method = RequestMethod.GET)
    public String initComposeNewView(Model model,
                                     @MessagingAccount DirectAccountDetails messagingAccount) {
        model.addAttribute("msgDetails", new ComposeMessageForm());

        AddressBookFilter addressBookFilter = new AddressBookFilter();
        addressBookFilter.setAddressBookSource(AddressBookSource.ELDERMARK_EXCHANGE_DIRECTORY);
        model.addAttribute("addressBookFilter", addressBookFilter);

        model.addAttribute("sendMessageUrlTemplate", "/secure-messaging/send");

        return "secure.messaging.compose";
    }

    @ResponseBody
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public List<String> send(@ModelAttribute(value = "msgDetails") ComposeMessageForm msgDetails,
                             @MessagingAccount DirectAccountDetails messagingAccount) {

        List<DirectAttachment> attachments = new ArrayList<DirectAttachment>();
        if(msgDetails.getFiles() != null){
            for(CommonsMultipartFile file: msgDetails.getFiles()){
                DirectAttachment attachment = new DirectAttachment();
                attachment.setContentType(file.getContentType());
                attachment.setData(file.getBytes());
                attachment.setFileName(file.getOriginalFilename());
                attachments.add(attachment);
            }
        }

        String allRecipients = msgDetails.getTo();
        String subject = msgDetails.getSubject();
        String body = msgDetails.getBody();
        List<String> errorList = new ArrayList<String>();

        try {
            directMessagesFacade.sendMessage(allRecipients, subject, body, attachments, messagingAccount);
        } catch (Exception e) {
            errorList.add(e.getLocalizedMessage());
        }
        return errorList;
    }

    @RequestMapping(value = "/config-warning", method = RequestMethod.GET)
    public String initMessagingNotConfiguredView(@AuthenticationPrincipal ExchangeUserDetails userDetails,
                                                 @MessagingAccount DirectAccountDetails messagingAccount, Model model) {
        model.addAttribute("secureEmail", messagingAccount.getSecureEmail());

        boolean readyForActivation;
        if (SecurityUtils.hasAnyRole(CareTeamRoleCode.ALL_ROLES)) {
            // check if all mandatory fields are filled
            readyForActivation = directMessagesFacade.isValidForRegistration(userDetails.getEmployeeId());
            // deactivate when not
            if (!readyForActivation) {
                directMessagesFacade.deactivateSecureMessaging(userDetails.getEmployeeId(), DirectErrorCode.EMPLOYEE_NOT_VALID);
            }
        } else {
            readyForActivation = true;
        }

        model.addAttribute("isReadyForActivation", readyForActivation);

        return "config-warning.view";
    }

    @RequestMapping(value = "/activate", method = RequestMethod.POST)
    @ResponseBody
    public void activate(@AuthenticationPrincipal ExchangeUserDetails userDetails,
                         @MessagingAccount DirectAccountDetails messagingAccount) {
        Long employeeId = userDetails.getEmployeeId();

        // 1. check Secure Messaging is configured for organization
        if (!directConfigurationFacade.isConfigured(userDetails.getCompanyCode())) {
            directMessagesFacade.deactivateSecureMessaging(employeeId, DirectErrorCode.CERTIFICATE_ERROR);
            throwNotRegisteredException(messagingAccount);
        }

        if (directMessagesFacade.isDirectAccountRegistered(messagingAccount)) {
            directMessagesFacade.activateSecureMessaging(employeeId);
        } else {
            directMessagesFacade.deactivateSecureMessaging(employeeId, DirectErrorCode.ACCOUNT_NOT_REGISTERED);
            throwNotRegisteredException(messagingAccount);
        }

        messagingConfig.resetCache();
    }

    private void throwNotRegisteredException(DirectAccountDetails messagingAccount) {
        String message = String.format("Secure Messaging account %s does not exist.\n Please contact your manager for details.",
                messagingAccount.getSecureEmail());
        throw new DirectAccountNotRegisteredException(message);
    }

    @ModelAttribute("addressBookSourceValues")
    public AddressBookSource[] getAddressBookSources() {
        return AddressBookSource.values();
    }
}
