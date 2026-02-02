package com.scnsoft.eldermark.shared.carecoordination.contacts;

import com.scnsoft.eldermark.shared.carecoordination.NameDto;

/**
 * Created by pzhurba on 29-Oct-15.
 */
public class ContactListItemDto extends NameDto {
    private Long id;
    private String role;
    private String status;
    private String email;
    private String phone;
    private boolean editable = false;
    private boolean viewOnly = false;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isViewOnly() {
        return viewOnly;
    }

    public void setViewOnly(boolean viewOnly) {
        this.viewOnly = viewOnly;
    }
}
