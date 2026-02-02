package com.scnsoft.eldermark.consana.sync.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ConsanaPractitionerRoleDto {

    private ConsanaCodeableConceptDto code;

    public ConsanaPractitionerRoleDto(ConsanaCodeableConceptDto code) {
        this.code = code;
    }

    public ConsanaCodeableConceptDto getCode() {
        return code;
    }

    public void setCode(ConsanaCodeableConceptDto code) {
        this.code = code;
    }
}
