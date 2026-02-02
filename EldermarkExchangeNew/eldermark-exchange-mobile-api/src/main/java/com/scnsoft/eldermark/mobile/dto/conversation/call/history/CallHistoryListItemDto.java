package com.scnsoft.eldermark.mobile.dto.conversation.call.history;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.video.VideoCallHistory_;
import org.springframework.data.domain.Sort;

import java.util.List;

public class CallHistoryListItemDto {
    private Long id;

    private String name;

    private String typeName;
    private String typeTitle;

    private Long duration;

    @DefaultSort(direction = Sort.Direction.DESC)
    @EntitySort(VideoCallHistory_.RECORD_DATETIME)
    //todo - pull active calls up
    private Long date;

    @Deprecated
    private List<Long> participatingEmployeeIds;
    private List<IdNameAvatarIdActiveDto> participatingEmployees;
    private String conversationSid;
    private boolean isConversationDisconnected;
    private String roomAccessToken;
    private String roomSid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public List<Long> getParticipatingEmployeeIds() {
        return participatingEmployeeIds;
    }

    public void setParticipatingEmployeeIds(List<Long> participatingEmployeeIds) {
        this.participatingEmployeeIds = participatingEmployeeIds;
    }

    public List<IdNameAvatarIdActiveDto> getParticipatingEmployees() {
        return participatingEmployees;
    }

    public void setParticipatingEmployees(List<IdNameAvatarIdActiveDto> participatingEmployees) {
        this.participatingEmployees = participatingEmployees;
    }

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public boolean getIsConversationDisconnected() {
        return isConversationDisconnected;
    }

    public void setIsConversationDisconnected(boolean isConversationDisconnected) {
        this.isConversationDisconnected = isConversationDisconnected;
    }

    public String getRoomAccessToken() {
        return roomAccessToken;
    }

    public void setRoomAccessToken(String roomAccessToken) {
        this.roomAccessToken = roomAccessToken;
    }

    public String getRoomSid() {
        return roomSid;
    }

    public void setRoomSid(String roomSid) {
        this.roomSid = roomSid;
    }
}
