package com.scnsoft.eldermark.entity.phr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.*;
import java.io.Serializable;

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

        @Override
        @JsonValue
        public String toString() {
            return value;
        }

        @JsonCreator
        public static Code fromValue(String text) {
            for (Code b : Code.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
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

