package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PhrChatThreadDto {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("receiverId")
    private String receiverId;
    
    @JsonProperty("notifyUserId")
    private Long notifyUserId;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("quantity")
    private Long quantity;
    
    @JsonProperty("unreadMessages")
    private Long unreadMessages;
    
    @JsonProperty("lastMessage")
    private PhrChatLastMessageDto lastMessage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Long getNotifyUserId() {
        return notifyUserId;
    }

    public void setNotifyUserId(Long notifyUserId) {
        this.notifyUserId = notifyUserId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(Long unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

	public PhrChatLastMessageDto getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(PhrChatLastMessageDto lastMessage) {
		this.lastMessage = lastMessage;
	}
    
}
