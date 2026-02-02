package com.scnsoft.eldermark.entity.password;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PasswordSettings")
public class PasswordSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="name", nullable = false, unique = true)
    private PasswordSettingsType passwordSettingsType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PasswordSettingsType getPasswordSettingsType() {
        return passwordSettingsType;
    }

    public void setPasswordSettingsType(PasswordSettingsType passwordSettingsType) {
        this.passwordSettingsType = passwordSettingsType;
    }
}
