package com.scnsoft.eldermark.shared.carecoordination;

import com.scnsoft.eldermark.entity.password.PasswordSettingsType;

import java.io.Serializable;

public class DatabasePasswordSettingsDto implements Serializable {
    private Long id;
    private Boolean enabled;
    private Long value;
    private PasswordSettingsType passwordSettingsType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public PasswordSettingsType getPasswordSettingsType() {
        return passwordSettingsType;
    }

    public void setPasswordSettingsType(PasswordSettingsType passwordSettingsType) {
        this.passwordSettingsType = passwordSettingsType;
    }
}
