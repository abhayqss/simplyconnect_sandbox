package com.scnsoft.eldermark.event.xml.entity.emuns;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Gender")
@XmlEnum
public enum Gender {
    @XmlEnumValue("MALE")
    MALE("M"),

    @XmlEnumValue("FEMALE")
    FEMALE("F"),

    @XmlEnumValue("UNDIFFERENTIATED")
    UNDIFFERENTIATED("UN");

    private String administrativeGenderCode;

    Gender(String administrativeGenderCode) {
        this.administrativeGenderCode = administrativeGenderCode;
    }

    /**
     * Gender administrative code
     */
    public String getAdministrativeGenderCode() {
        return administrativeGenderCode;
    }

    /**
     * Get gender by administrative code
     */
    public static Gender getGenderByCode(String code) {
        Validate.notNull(code, "code cannot be null");

        Gender gender = null;
        for (Gender g : Gender.values()) {
            if (code.equals(g.getAdministrativeGenderCode())) {
                gender = g;
                break;
            }
        }

        if (gender == null) {
            throw new IllegalArgumentException("Gender with code '" + code + "' not found");
        }

        return gender;
    }

    /**
     * Gender display name
     */
    @JsonValue
    public String getLabel() {
        return StringUtils.capitalize(StringUtils.lowerCase(this.toString()));
    }
}
