package com.scnsoft.eldermark.services.direct;

import com.scnsoft.eldermark.shared.SesDirectoryAccountDto;
import com.scnsoft.eldermark.shared.DirectMessageType;
import com.scnsoft.eldermark.shared.MessageDto;

import java.util.List;

public interface DirectMessagesService {
    void sendMessage(List<String> recipients, String subject, String bodyHTML, List<DirectAttachment> attachments, DirectAccountDetails directAccountDetails);

    List<MessageDto> getInboxMessages(int offset, int limit, DirectMessageType state, DirectAccountDetails directAccountDetails);

    Integer getInboxMessagesCount(DirectMessageType state, DirectAccountDetails directAccountDetails);

    MessageDto getInboxMessage(String messageId, DirectAccountDetails directAccountDetails, boolean reply);

    boolean deleteMessage(String messageId, DirectAccountDetails directAccountDetails);

    void getInboxMessageAttachment(String messageId, int partNumber, DownloadAttachmentCallback callback,
                                   DirectAccountDetails directAccountDetails);

    Integer getUnreadMessagesCount(DirectAccountDetails directAccountDetails);

    void markMessageAsSeen(String messageId, DirectAccountDetails directAccountDetails);

    List<SesDirectoryAccountDto> exchangeDirectorySearch(String secureEmailFilter, DirectAccountDetails directAccountDetails);
    List<SesDirectoryAccountDto> publicDirectorySearch(String secureEmailFilter, DirectAccountDetails directAccountDetails);

    String registerDirectAccount(RegistrationRequestDto account, String companyCode);

    void deleteDirectAccount(DirectAccountDetails directAccountDetails, String reason);

    /**
     * Searches SES repo for employee's secure email.
     */
    boolean isSecureEmailRegistered(DirectAccountDetails directAccountDetails);
}