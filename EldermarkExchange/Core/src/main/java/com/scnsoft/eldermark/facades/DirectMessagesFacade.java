package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.entity.DirectErrorCode;
import com.scnsoft.eldermark.services.direct.DirectAccountDetails;
import com.scnsoft.eldermark.services.direct.DirectAttachment;
import com.scnsoft.eldermark.services.direct.DownloadAttachmentCallback;
import com.scnsoft.eldermark.shared.DirectMessageType;
import com.scnsoft.eldermark.shared.MessageDto;
import com.scnsoft.eldermark.shared.SesDirectoryAccountDto;
import com.scnsoft.eldermark.shared.form.AddressBookFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DirectMessagesFacade {
    void sendMessage(List<String> recipients, String subject, String bodyHTML, List<DirectAttachment> attachments, DirectAccountDetails directAccountDetails);

    void sendMessage(String allRecipients, String subject, String bodyHTML, List<DirectAttachment> attachments, DirectAccountDetails directAccountDetails);

    List<MessageDto> getInboxMessages(int offset, int limit, DirectMessageType state, DirectAccountDetails directAccountDetails);

    List<MessageDto> getInboxMessages(DirectMessageType state, Pageable pageable, DirectAccountDetails directAccountDetails);

    MessageDto getInboxMessage(String messageId, DirectAccountDetails directAccountDetails, boolean reply);

    Boolean deleteMessage(String messageId, DirectAccountDetails directAccountDetails);

    Integer getInboxMessagesCount(DirectMessageType state, DirectAccountDetails directAccountDetails);

    void getInboxMessageAttachment(String messageId, int partNumber, DownloadAttachmentCallback callback, DirectAccountDetails directAccountDetails);

    Integer getUnreadMessagesCount(DirectAccountDetails directAccountDetails);

    void markMessageAsSeen(String messageId, DirectAccountDetails directAccountDetails);

    Page<SesDirectoryAccountDto> directorySearch(AddressBookFilter filter, Pageable pageable, DirectAccountDetails directAccountDetails);

    boolean isDirectAccountRegistered(DirectAccountDetails directAccountDetails);

    void deleteDirectAccount(DirectAccountDetails directAccountDetails, String reason);

    void registerDirectAccount(Long employeeId, String companyCode);

    boolean isValidForRegistration(Long employeeId);

    boolean isSecureMessagingActive(Long employeeId);

    void activateSecureMessaging(Long employeeId);

    void deactivateSecureMessaging(Long employeeId, DirectErrorCode error);
    void deactivateSecureMessaging(Long employeeId, DirectErrorCode error, String message);
}
