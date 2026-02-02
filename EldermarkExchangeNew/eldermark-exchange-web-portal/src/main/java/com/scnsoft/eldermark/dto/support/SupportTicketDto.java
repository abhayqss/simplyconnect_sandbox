package com.scnsoft.eldermark.dto.support;

import com.scnsoft.eldermark.validation.ValidationRegExpConstants;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public class SupportTicketDto {

    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    @NotEmpty
    private String phone;

    @NotNull
    private Long typeId;

    @NotNull
    @Size(max = 5000)
    private String messageText;

    @Size(max = 10)
    private List<MultipartFile> attachmentFiles;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public List<MultipartFile> getAttachmentFiles() {
        return attachmentFiles;
    }

    public void setAttachmentFiles(List<MultipartFile> attachmentFiles) {
        this.attachmentFiles = attachmentFiles;
    }
}
