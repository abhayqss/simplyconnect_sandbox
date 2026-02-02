package com.scnsoft.eldermark.dto.docutrack;

public class AttachFromDocutrackDto {

    private String conversationSid;
    private Long documentId;

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }
}
