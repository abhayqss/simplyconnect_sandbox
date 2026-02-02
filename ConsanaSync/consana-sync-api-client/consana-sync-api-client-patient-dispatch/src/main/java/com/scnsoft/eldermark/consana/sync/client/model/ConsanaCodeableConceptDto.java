package com.scnsoft.eldermark.consana.sync.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ConsanaCodeableConceptDto {

    private List<ConsanaCodingDto> coding;
    private String text;

    public List<ConsanaCodingDto> getCoding() {
        return coding;
    }

    public void setCoding(List<ConsanaCodingDto> coding) {
        this.coding = coding;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
