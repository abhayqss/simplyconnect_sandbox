package com.scnsoft.eldermark.consana.sync.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ConsanaHumanNameDto {

    private String use;
    private String text;
    private List<String> family;
    private List<String> given;

    public ConsanaHumanNameDto(String use, String text, List<String> family, List<String> given) {
        this.use = use;
        this.text = text;
        this.family = family;
        this.given = given;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getFamily() {
        return family;
    }

    public void setFamily(List<String> family) {
        this.family = family;
    }

    public List<String> getGiven() {
        return given;
    }

    public void setGiven(List<String> given) {
        this.given = given;
    }
}
