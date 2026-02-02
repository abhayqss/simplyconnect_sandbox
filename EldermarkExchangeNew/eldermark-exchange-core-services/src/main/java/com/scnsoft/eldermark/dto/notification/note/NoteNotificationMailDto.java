package com.scnsoft.eldermark.dto.notification.note;

import com.scnsoft.eldermark.dto.notification.BaseNotificationMailDto;

public class NoteNotificationMailDto extends BaseNotificationMailDto {

    private String action;
    private String noteUrl;
    private String communityName;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getNoteUrl() {
        return noteUrl;
    }

    public void setNoteUrl(String noteUrl) {
        this.noteUrl = noteUrl;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }
}
