package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PhrChatLastMessageDto {
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("sender")
	private String sender;
	
	@JsonProperty("receiver")
	private String receiver;
	
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("text")
	private String text;
	
	@JsonProperty("createdAt")
	private String createdAt;
	
	@JsonProperty("notifiedAt")
	private String notifiedAt;
	
	@JsonProperty("deliveredAt")
	private String deliveredAt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getNotifiedAt() {
		return notifiedAt;
	}

	public void setNotifiedAt(String notifiedAt) {
		this.notifiedAt = notifiedAt;
	}

	public String getDeliveredAt() {
		return deliveredAt;
	}

	public void setDeliveredAt(String deliveredAt) {
		this.deliveredAt = deliveredAt;
	}
}
