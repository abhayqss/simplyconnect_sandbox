package com.scnsoft.eldermark.dto.notification;

import org.springframework.core.io.InputStreamSource;

import java.time.Instant;
import java.util.List;

public class SupportTicketSubmittedNotificationDto {

    private String receiverEmail;

    private Long ticketNumber;

    private Instant date;
    private String authorOrganizationName;
    private String authorName;
    private String authorPhoneNumber;
    private String howCanWeHelpYouMessage;
    private String message;

    private List<Attachment> attachments;

    public static class Attachment {

        private String fileName;
        private String mediaType;
        private InputStreamSource data;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getMediaType() {
            return mediaType;
        }

        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }

        public InputStreamSource getData() {
            return data;
        }

        public void setData(InputStreamSource data) {
            this.data = data;
        }
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public Long getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(Long ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getAuthorOrganizationName() {
        return authorOrganizationName;
    }

    public void setAuthorOrganizationName(String authorOrganizationName) {
        this.authorOrganizationName = authorOrganizationName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorPhoneNumber() {
        return authorPhoneNumber;
    }

    public void setAuthorPhoneNumber(String authorPhoneNumber) {
        this.authorPhoneNumber = authorPhoneNumber;
    }

    public String getHowCanWeHelpYouMessage() {
        return howCanWeHelpYouMessage;
    }

    public void setHowCanWeHelpYouMessage(String howCanWeHelpYouMessage) {
        this.howCanWeHelpYouMessage = howCanWeHelpYouMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
}
