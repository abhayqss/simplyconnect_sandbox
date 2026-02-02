package com.scnsoft.eldermark.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class AccessRight implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    public enum Code {
        /**
         * Read access to Personal Health Record
         */
        MY_PHR("MY_PHR"),
        /**
         * Read access to Medications list.
         */
        MEDICATIONS_LIST("MEDICATIONS_LIST"),
        /**
         * Read access to Event notifications
         */
        EVENT_NOTIFICATIONS("EVENT_NOTIFICATIONS"),
        /**
         * Read access to Care Team
         */
        MY_CT_VISIBILITY("MY_CT_VISIBILITY");

        private final String value;

        Code(String value) {
            this.value = value;
        }

    }

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    private Code code;

    @Column(name = "display_name")
    private String displayName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AccessRight that = (AccessRight) o;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}