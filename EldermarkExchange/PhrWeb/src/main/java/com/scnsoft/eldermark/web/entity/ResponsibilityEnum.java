package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Responsibility
 */
public enum ResponsibilityEnum {

    RESPONSIBLE("Responsible"),
    ACCOUNTABLE("Accountable"),
    CONSULTED("Consulted"),
    INFORMED("Informed"),
    VIEWABLE("Viewable"),
    NOT_VIEWABLE("Not Viewable");

    private String value;

    ResponsibilityEnum(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }

    @JsonCreator
    public static ResponsibilityEnum fromValue(String text) {
        for (ResponsibilityEnum b : ResponsibilityEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}

