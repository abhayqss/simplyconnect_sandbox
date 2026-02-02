package com.scnsoft.eldermark.mobile.dto.conversation;

public class ConversationClientListItemDto {

    private Long id;
    private Long associatedContactId;
    private String fullName;

    public ConversationClientListItemDto() {
    }

    public ConversationClientListItemDto(Long id, Long associatedContactId, String fullName) {
        this.id = id;
        this.associatedContactId = associatedContactId;
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public ConversationClientListItemDto setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getAssociatedContactId() {
        return associatedContactId;
    }

    public ConversationClientListItemDto setAssociatedContactId(Long associatedContactId) {
        this.associatedContactId = associatedContactId;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public ConversationClientListItemDto setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }
}
