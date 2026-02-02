package com.scnsoft.eldermark.entity.password;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "PasswordSettings")
public class PasswordSettings implements Serializable {
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
