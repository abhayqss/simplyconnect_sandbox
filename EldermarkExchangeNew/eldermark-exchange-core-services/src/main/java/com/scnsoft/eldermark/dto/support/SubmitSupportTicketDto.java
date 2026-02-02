package com.scnsoft.eldermark.dto.support;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.SupportTicketType;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

public class SubmitSupportTicketDto {

    private Instant creationDate;
    private Employee author;
    private String authorPhoneNumber;
    private String message;
    private SupportTicketType type;

    private List<MultipartFile> attachmentFiles;

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Employee getAuthor() {
        return author;
    }

    public void setAuthor(Employee author) {
        this.author = author;
    }

    public String getAuthorPhoneNumber() {
        return authorPhoneNumber;
    }

    public void setAuthorPhoneNumber(String authorPhoneNumber) {
        this.authorPhoneNumber = authorPhoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MultipartFile> getAttachmentFiles() {
        return attachmentFiles;
    }

    public void setAttachmentFiles(List<MultipartFile> attachmentFiles) {
        this.attachmentFiles = attachmentFiles;
    }

    public SupportTicketType getType() {
        return type;
    }

    public void setType(SupportTicketType type) {
        this.type = type;
    }
}
