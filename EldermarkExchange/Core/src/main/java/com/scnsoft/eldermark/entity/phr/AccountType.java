package com.scnsoft.eldermark.entity.phr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

@Entity
@Table
public class AccountType extends BaseEntity {

    /**
     * User role (account type)
     */
    public enum Type {
        CONSUMER("CONSUMER"),
        PROVIDER("PROVIDER"),
        NOTIFY("NOTIFY"),
        APPLICATION("APPLICATION");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return value;
        }

        @JsonCreator
        public static Type fromValue(String text) {
            text = StringUtils.upperCase(text);
            for (Type b : Type.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type;

    @Column
    private String name;


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

