package com.scnsoft.eldermark.mobile.dto.careteam;

public class BaseCareTeamMemberDto<CONTACT extends BaseCareTeamContactItem> {
    private Long id;
    private String role;
    private boolean isOnHold;

    private boolean canDelete;

    private CONTACT contact;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean getIsOnHold() {
        return isOnHold;
    }

    public void setIsOnHold(boolean onHold) {
        isOnHold = onHold;
    }

    public boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public CONTACT getContact() {
        return contact;
    }

    public void setContact(CONTACT contact) {
        this.contact = contact;
    }
}
