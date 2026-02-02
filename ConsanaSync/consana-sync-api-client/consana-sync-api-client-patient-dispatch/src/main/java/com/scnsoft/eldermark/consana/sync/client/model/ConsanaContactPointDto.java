package com.scnsoft.eldermark.consana.sync.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ConsanaContactPointDto {

    private String system;
    private String value;
    private String use;

    public ConsanaContactPointDto(String system, String value) {
        this(system, value, null);
    }

    public ConsanaContactPointDto(String system, String value, String use) {
        this.system = system;
        this.value = value;
        this.use = use;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }
}
