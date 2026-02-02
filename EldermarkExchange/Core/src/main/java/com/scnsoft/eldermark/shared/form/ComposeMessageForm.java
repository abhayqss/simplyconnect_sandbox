package com.scnsoft.eldermark.shared.form;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.List;

/**
 * Created by stsiushkevich on 19.05.15.
 */
public class ComposeMessageForm {
    private String messageId;

    private String from;

    private String to;

    private String subject;

    private String body;

    private List<String> reportTypes;

    private List<String> customDocumentIds;

    private List<CommonsMultipartFile> files;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getCustomDocumentIds() {
        return customDocumentIds;
    }

    public void setCustomDocumentIds(List<String> customDocumentIds) {
        this.customDocumentIds = customDocumentIds;
    }

    public List<CommonsMultipartFile> getFiles() {
        return files;
    }

    public void setFiles(List<CommonsMultipartFile> files) {
        this.files = files;
    }

    public List<String> getReportTypes() {
        return reportTypes;
    }

    public void setReportTypes(List<String> reportTypes) {
        this.reportTypes = reportTypes;
    }
}
