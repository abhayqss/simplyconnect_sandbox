package com.scnsoft.eldermark.entity.password;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "DatabasePasswordSettings")
public class DatabasePasswordSettings implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "value")
    private Long value;

    @JoinColumn(name = "password_settings_id", referencedColumnName = "id")
    @ManyToOne
    private PasswordSettings passwordSettings;

    @Column(name = "database_id", nullable = false)
    private long databaseId;

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

    public PasswordSettings getPasswordSettings() {
        return passwordSettings;
    }

    public void setPasswordSettings(PasswordSettings passwordSettings) {
        this.passwordSettings = passwordSettings;
    }

    public long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(long databaseId) {
        this.databaseId = databaseId;
    }
}
