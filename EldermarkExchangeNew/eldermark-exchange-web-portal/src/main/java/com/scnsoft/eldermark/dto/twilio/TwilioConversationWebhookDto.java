package com.scnsoft.eldermark.dto.twilio;

public class TwilioConversationWebhookDto {

//payload is form data, not JSON
//    @JsonProperty("MessageSid")
    private String messageSid;

//    @JsonProperty("ConversationSid")
    private String conversationSid;

//    @JsonProperty("EventType")
    private String eventType;

    private Integer index;

    //DateCreated: 2022-03-28T18:10:36.590Z
    private String dateCreated;

//    @JsonProperty("ParticipantSid")
    private String participantSid;

//    @JsonProperty("Author")
    private String author;

//    @JsonProperty("Media")
    private String media;

//    @JsonProperty("Attributes")
    private String attributes;

//    @JsonProperty("LastReadMessageIndex")
    private Integer lastReadMessageIndex;

    private String identity;

    public String getMessageSid() {
        return messageSid;
    }

    public void setMessageSid(String messageSid) {
        this.messageSid = messageSid;
    }

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getParticipantSid() {
        return participantSid;
    }

    public void setParticipantSid(String participantSid) {
        this.participantSid = participantSid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public Integer getLastReadMessageIndex() {
        return lastReadMessageIndex;
    }

    public void setLastReadMessageIndex(Integer lastReadMessageIndex) {
        this.lastReadMessageIndex = lastReadMessageIndex;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }


    @Override
    public String toString() {
        return "TwilioConversationWebhookDto{" +
                "messageSid='" + messageSid + '\'' +
                ", conversationSid='" + conversationSid + '\'' +
                ", eventType='" + eventType + '\'' +
                ", index='" + index + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", participantSid='" + participantSid + '\'' +
                ", author='" + author + '\'' +
                ", media='" + media + '\'' +
                ", attributes='" + attributes + '\'' +
                ", lastReadMessageIndex=" + lastReadMessageIndex +
                ", identity='" + identity + '\'' +
                '}';
    }
}
