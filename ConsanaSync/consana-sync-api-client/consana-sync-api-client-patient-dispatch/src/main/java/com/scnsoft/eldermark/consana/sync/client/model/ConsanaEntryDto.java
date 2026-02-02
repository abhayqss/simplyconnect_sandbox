package com.scnsoft.eldermark.consana.sync.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ConsanaEntryDto {

    private ConsanaResourceDto resource;

    public ConsanaResourceDto getResource() {
        return resource;
    }

    public void setResource(ConsanaResourceDto resource) {
        this.resource = resource;
    }
}
